package com.graphprompt.model;

import java.util.*;

public class KnowledgeGraph {
    private Map<String, EntityNode> nodes;
    private Map<EntityNode, List<RelationshipEdge>> adjacencyList;

    public KnowledgeGraph() {
        this.nodes = new HashMap<>();
        this.adjacencyList = new HashMap<>();
    }

    public void addNode(EntityNode node) {
        if (!nodes.containsKey(node.getId())) {
            nodes.put(node.getId(), node);
            adjacencyList.put(node, new ArrayList<>());
        } else {
            nodes.get(node.getId()).incrementFrequency();
        }
    }

    public void addEdge(EntityNode source, EntityNode dest, double confidence) {
        addNode(source);
        addNode(dest);
        
        EntityNode srcNode = nodes.get(source.getId());
        EntityNode destNode = nodes.get(dest.getId());
        
        // Check if edge exists
        List<RelationshipEdge> edges = adjacencyList.get(srcNode);
        for (RelationshipEdge edge : edges) {
            if (edge.getDestination().equals(destNode)) {
                edge.incrementFrequency();
                edge.setConfidence((edge.getConfidence() + confidence) / 2.0); // simple average
                return;
            }
        }
        edges.add(new RelationshipEdge(srcNode, destNode, confidence));
        
        // Add reverse edge for undirected traversal
        List<RelationshipEdge> destEdges = adjacencyList.get(destNode);
        boolean reverseExists = false;
        for (RelationshipEdge edge : destEdges) {
            if (edge.getDestination().equals(srcNode)) {
                reverseExists = true;
                break;
            }
        }
        if (!reverseExists) {
            destEdges.add(new RelationshipEdge(destNode, srcNode, confidence));
        }
    }

    public EntityNode getNode(String id) {
        return nodes.get(id);
    }

    public List<EntityNode> getAllNodes() {
        return new ArrayList<>(nodes.values());
    }

    public List<RelationshipEdge> getEdges(EntityNode node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }

    public int getDegree(EntityNode node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>()).size();
    }
    
    public void clear() {
        nodes.clear();
        adjacencyList.clear();
    }
}
