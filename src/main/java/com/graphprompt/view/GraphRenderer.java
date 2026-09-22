package com.graphprompt.view;

import com.graphprompt.model.EntityNode;
import com.graphprompt.model.KnowledgeGraph;
import com.graphprompt.model.RelationshipEdge;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.*;

public class GraphRenderer {
    private Canvas canvas;
    private KnowledgeGraph graph;
    private Map<EntityNode, double[]> nodePositions; 
    private Random random;

    public GraphRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.nodePositions = new HashMap<>();
        this.random = new Random(42); 
    }

    public void setGraph(KnowledgeGraph graph) {
        this.graph = graph;
        this.nodePositions.clear();
        layoutNodes();
    }

    private void layoutNodes() {
        if (graph == null) return;
        List<EntityNode> nodes = graph.getAllNodes();
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        
        for (EntityNode node : nodes) {
            if (!nodePositions.containsKey(node)) {
                double x = 50 + random.nextDouble() * (Math.max(100, width - 100));
                double y = 50 + random.nextDouble() * (Math.max(100, height - 100));
                nodePositions.put(node, new double[]{x, y});
            }
        }
    }

    public void drawGraph(Collection<EntityNode> highlightedNodes, Collection<RelationshipEdge> highlightedEdges) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        if (graph == null) return;
        
        for (EntityNode node : graph.getAllNodes()) {
            double[] pos1 = nodePositions.get(node);
            for (RelationshipEdge edge : graph.getEdges(node)) {
                EntityNode dest = edge.getDestination();
                double[] pos2 = nodePositions.get(dest);
                
                if (pos1 != null && pos2 != null) {
                    boolean isHighlighted = highlightedEdges != null && highlightedEdges.contains(edge);
                    gc.setStroke(isHighlighted ? Color.LAWNGREEN : Color.GRAY);
                    gc.setLineWidth(isHighlighted ? 2.0 : 0.5);
                    gc.strokeLine(pos1[0], pos1[1], pos2[0], pos2[1]);
                }
            }
        }
        
        gc.setFont(new Font(10));
        for (EntityNode node : graph.getAllNodes()) {
            double[] pos = nodePositions.get(node);
            if (pos != null) {
                boolean isHighlighted = highlightedNodes != null && highlightedNodes.contains(node);
                gc.setFill(isHighlighted ? Color.LAWNGREEN : Color.LIGHTBLUE);
                double radius = 15 + Math.min(10, node.getSalienceScore());
                
                gc.fillOval(pos[0] - radius/2, pos[1] - radius/2, radius, radius);
                gc.setStroke(Color.DARKBLUE);
                gc.setLineWidth(1.0);
                gc.strokeOval(pos[0] - radius/2, pos[1] - radius/2, radius, radius);
                
                gc.setFill(Color.BLACK);
                gc.fillText(node.getConcept(), pos[0] - radius/2, pos[1] - radius/2 - 2);
            }
        }
    }
}
