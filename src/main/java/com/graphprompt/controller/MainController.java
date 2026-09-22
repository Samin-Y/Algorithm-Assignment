package com.graphprompt.controller;

import com.graphprompt.algorithms.*;
import com.graphprompt.model.EntityNode;
import com.graphprompt.model.KnowledgeGraph;
import com.graphprompt.util.MockDataLoader;
import com.graphprompt.util.TokenEstimator;
import com.graphprompt.view.GraphRenderer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.util.*;

public class MainController {

    @FXML private Pane canvasPane;
    @FXML private TextArea consoleOutput;
    
    @FXML private Slider sliderBudget;
    @FXML private Slider sliderAlpha;
    @FXML private Slider sliderBeta;
    @FXML private Slider sliderGamma;
    
    @FXML private TextField txtBudget;
    @FXML private TextField txtAlpha;
    @FXML private TextField txtBeta;
    @FXML private TextField txtGamma;
    @FXML private Button btnShowConv;

    private KnowledgeGraph graph;
    private GraphRenderer renderer;
    private List<String> mockData;
    private int totalRawTokens = 0;

    @FXML
    public void initialize() {
        graph = new KnowledgeGraph();
        renderer = new GraphRenderer(canvasPane);
        mockData = MockDataLoader.getMockChatTurns();
        
        canvasPane.widthProperty().addListener((obs, oldVal, newVal) -> renderer.drawGraph(null, null));
        canvasPane.heightProperty().addListener((obs, oldVal, newVal) -> renderer.drawGraph(null, null));
        
        setupSliderBinding(sliderBudget, txtBudget, true);
        setupSliderBinding(sliderAlpha, txtAlpha, false);
        setupSliderBinding(sliderBeta, txtBeta, false);
        setupSliderBinding(sliderGamma, txtGamma, false);
        
        log("System initialized. Ready to ingest mock data.");
    }
    
    private void setupSliderBinding(Slider slider, TextField textField, boolean isInt) {
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (isInt) {
                textField.setText(String.valueOf(newVal.intValue()));
            } else {
                textField.setText(String.format(Locale.US, "%.2f", newVal.doubleValue()));
            }
        });
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (isInt) {
                    slider.setValue(Integer.parseInt(newVal));
                } else {
                    slider.setValue(Double.parseDouble(newVal));
                }
            } catch (NumberFormatException e) {
                // Ignore invalid input while typing
            }
        });
    }

    @FXML
    private void handleShowConversation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/graphprompt/view/conversation.fxml"));
            Parent conversationRoot = loader.load();
            
            ConversationController controller = loader.getController();
            controller.setPreviousRoot(canvasPane.getScene().getRoot());
            controller.loadConversation(mockData);
            
            canvasPane.getScene().setRoot(conversationRoot);
        } catch (Exception e) {
            log("Error loading conversation view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleIngest() {
        graph.clear();
        int rawTokens = 0;
        for (String message : mockData) {
            GraphIngestion.ingest(graph, message);
            rawTokens += TokenEstimator.estimateTokens(message);
        }
        this.totalRawTokens = rawTokens;
        
        updateSalience();
        renderer.setGraph(graph);
        renderer.drawGraph(null, null);
        
        log("\n--- Ingestion ---");
        log("Ingested " + mockData.size() + " messages.");
        log("Raw Context Token Size: ~" + rawTokens + " tokens.");
        log("Graph created with " + graph.getAllNodes().size() + " entities.");
    }

    @FXML
    private void handleDijkstra() {
        if (graph.getAllNodes().size() < 2) return;
        
        List<EntityNode> nodes = graph.getAllNodes();
        EntityNode start = nodes.get(new Random().nextInt(nodes.size()));
        EntityNode end = nodes.get(new Random().nextInt(nodes.size()));
        
        List<EntityNode> path = Pathfinding.findShortestPathDijkstra(graph, start, end);
        
        renderer.drawGraph(path, null);
        log("\n--- Dijkstra ---");
        log("Shortest Path from '" + start.getConcept() + "' to '" + end.getConcept() + "':");
        if (path.isEmpty()) {
            log("No path found.");
        } else {
            List<String> pathStr = new ArrayList<>();
            for (EntityNode n : path) pathStr.add(n.getConcept());
            log(String.join(" -> ", pathStr));
        }
    }

    @FXML
    private void handleBFS() {
        if (graph.getAllNodes().isEmpty()) return;
        
        List<EntityNode> nodes = graph.getAllNodes();
        EntityNode start = nodes.get(new Random().nextInt(nodes.size()));
        
        Set<EntityNode> context = Pathfinding.findLocalContextBFS(graph, start, 2);
        
        renderer.drawGraph(context, null);
        log("\n--- BFS ---");
        log("Local Context around '" + start.getConcept() + "' (depth=2):");
        log("Found " + context.size() + " nodes.");
    }

    @FXML
    private void handleDP() {
        assemblePrompt(true);
    }

    @FXML
    private void handleGreedy() {
        assemblePrompt(false);
    }
    
    private void assemblePrompt(boolean useDP) {
        updateSalience();
        List<EntityNode> nodes = graph.getAllNodes();
        List<EntityNode> sortedNodes = SalienceRanking.sortNodes(nodes);
        
        int budget = (int) sliderBudget.getValue();
        List<EntityNode> selected;
        
        long startTime = System.nanoTime();
        if (useDP) {
            selected = TokenKnapsack.packDP(sortedNodes, budget);
        } else {
            selected = TokenKnapsack.packGreedy(sortedNodes, budget);
        }
        long endTime = System.nanoTime();
        
        int usedTokens = selected.stream().mapToInt(EntityNode::getTokenWeight).sum();
        
        renderer.drawGraph(selected, null);
        
        log("\n=== Assemble Prompt (" + (useDP ? "0/1 Knapsack DP" : "Greedy") + ") ===");
        log(String.format("Raw Tokens Needed: %d | Optimized Tokens Needed (Budget): %d | Used: %d", totalRawTokens, budget, usedTokens));
        log("Execution time: " + String.format(Locale.US, "%.3f", (endTime - startTime) / 1000000.0) + " ms");
        
        List<String> concepts = new ArrayList<>();
        for (EntityNode n : selected) concepts.add(n.getConcept());
        log("Packed Prompt Context: " + String.join(", ", concepts));
    }

    private void updateSalience() {
        double a = sliderAlpha.getValue();
        double b = sliderBeta.getValue();
        double g = sliderGamma.getValue();
        SalienceRanking.rankNodes(graph, a, b, g);
    }

    private void log(String msg) {
        consoleOutput.appendText(msg + "\n");
    }
}
