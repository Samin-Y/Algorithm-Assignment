package com.graphprompt.view;

import com.graphprompt.model.EntityNode;
import com.graphprompt.model.KnowledgeGraph;
import com.graphprompt.model.RelationshipEdge;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.*;

public class GraphRenderer {
    private Pane parentPane;
    private KnowledgeGraph graph;
    private Map<EntityNode, Point3D> nodePositions;
    private Random random;

    private SubScene subScene;
    private Group root3D;
    private Group graphGroup;
    
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    public GraphRenderer(Pane parentPane) {
        this.parentPane = parentPane;
        this.nodePositions = new HashMap<>();
        this.random = new Random(42);
        
        root3D = new Group();
        graphGroup = new Group();
        graphGroup.getTransforms().addAll(rotateX, rotateY);
        root3D.getChildren().add(graphGroup);
        
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-1000);
        camera.setNearClip(0.1);
        camera.setFarClip(3000.0);
        
        subScene = new SubScene(root3D, 700, 400, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.web("#1e1f20"));
        subScene.setCamera(camera);
        
        subScene.widthProperty().bind(parentPane.widthProperty());
        subScene.heightProperty().bind(parentPane.heightProperty());
        parentPane.getChildren().add(subScene);
        
        initMouseControl(parentPane);
    }

    private void initMouseControl(Pane pane) {
        pane.setOnMousePressed(me -> {
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        pane.setOnMouseDragged(me -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            rotateX.setAngle(rotateX.getAngle() - (mousePosY - mouseOldY) * 0.3);
            rotateY.setAngle(rotateY.getAngle() + (mousePosX - mouseOldX) * 0.3);
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
        });
        pane.setOnScroll(se -> {
            double z = subScene.getCamera().getTranslateZ();
            double newZ = z + se.getDeltaY() * 2;
            subScene.getCamera().setTranslateZ(newZ);
        });
    }

    public void setGraph(KnowledgeGraph graph) {
        this.graph = graph;
        this.nodePositions.clear();
        layoutNodes();
    }

    private void layoutNodes() {
        if (graph == null) return;
        List<EntityNode> nodes = graph.getAllNodes();
        
        for (EntityNode node : nodes) {
            if (!nodePositions.containsKey(node)) {
                double x = (random.nextDouble() - 0.5) * 600;
                double y = (random.nextDouble() - 0.5) * 600;
                double z = (random.nextDouble() - 0.5) * 600;
                nodePositions.put(node, new Point3D(x, y, z));
            }
        }
    }

    public void drawGraph(Collection<EntityNode> highlightedNodes, Collection<RelationshipEdge> highlightedEdges) {
        graphGroup.getChildren().clear();
        if (graph == null) return;
        
        PhongMaterial defaultNodeMat = new PhongMaterial(Color.LIGHTBLUE);
        PhongMaterial highlightNodeMat = new PhongMaterial(Color.LAWNGREEN);
        PhongMaterial defaultEdgeMat = new PhongMaterial(Color.GRAY);
        PhongMaterial highlightEdgeMat = new PhongMaterial(Color.LAWNGREEN);
        
        for (EntityNode node : graph.getAllNodes()) {
            Point3D pos1 = nodePositions.get(node);
            if (pos1 == null) continue;
            
            for (RelationshipEdge edge : graph.getEdges(node)) {
                EntityNode dest = edge.getDestination();
                Point3D pos2 = nodePositions.get(dest);
                if (pos2 != null) {
                    boolean isHighlighted = highlightedEdges != null && highlightedEdges.contains(edge);
                    Cylinder line = createConnection(pos1, pos2);
                    line.setMaterial(isHighlighted ? highlightEdgeMat : defaultEdgeMat);
                    line.setRadius(isHighlighted ? 2.0 : 0.5);
                    graphGroup.getChildren().add(line);
                }
            }
        }
        
        for (EntityNode node : graph.getAllNodes()) {
            Point3D pos = nodePositions.get(node);
            if (pos != null) {
                boolean isHighlighted = highlightedNodes != null && highlightedNodes.contains(node);
                Sphere sphere = new Sphere(15 + Math.min(10, node.getSalienceScore()));
                sphere.setMaterial(isHighlighted ? highlightNodeMat : defaultNodeMat);
                sphere.setTranslateX(pos.getX());
                sphere.setTranslateY(pos.getY());
                sphere.setTranslateZ(pos.getZ());
                
                Text label = new Text(node.getConcept());
                label.setFont(new Font(14));
                label.setFill(Color.WHITE);
                label.setTranslateX(pos.getX() + sphere.getRadius() + 2);
                label.setTranslateY(pos.getY() - sphere.getRadius() - 2);
                label.setTranslateZ(pos.getZ());
                
                graphGroup.getChildren().addAll(sphere, label);
            }
        }
    }
    
    private Cylinder createConnection(Point3D origin, Point3D target) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();
        
        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());
        
        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);
        
        Cylinder line = new Cylinder(1, height);
        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);
        return line;
    }
}
