package com.graphprompt.algorithms;

import com.graphprompt.model.EntityNode;
import com.graphprompt.model.KnowledgeGraph;
import com.graphprompt.model.RelationshipEdge;
import java.util.*;

public class Pathfinding {

    public static List<EntityNode> findShortestPathDijkstra(KnowledgeGraph graph, EntityNode start, EntityNode end) {
        if (start == null || end == null || graph == null) return Collections.emptyList();

        Map<EntityNode, Double> distances = new HashMap<>();
        Map<EntityNode, EntityNode> previous = new HashMap<>();
        PriorityQueue<EntityNode> queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        for (EntityNode node : graph.getAllNodes()) {
            distances.put(node, Double.MAX_VALUE);
            previous.put(node, null);
        }

        distances.put(start, 0.0);
        queue.add(start);

        while (!queue.isEmpty()) {
            EntityNode current = queue.poll();
            
            if (current.equals(end)) {
                break;
            }

            for (RelationshipEdge edge : graph.getEdges(current)) {
                EntityNode neighbor = edge.getDestination();
                double weight = 1.0 / (edge.getConfidence() + 0.001);
                double alt = distances.get(current) + weight;
                
                if (alt < distances.get(neighbor)) {
                    distances.put(neighbor, alt);
                    previous.put(neighbor, current);
                    
                    queue.remove(neighbor); 
                    queue.add(neighbor);
                }
            }
        }

        List<EntityNode> path = new ArrayList<>();
        EntityNode curr = end;
        if (previous.get(curr) != null || curr.equals(start)) {
            while (curr != null) {
                path.add(0, curr);
                curr = previous.get(curr);
            }
        }
        
        return path.size() > 1 ? path : Collections.emptyList();
    }

    public static Set<EntityNode> findLocalContextBFS(KnowledgeGraph graph, EntityNode start, int depth) {
        Set<EntityNode> visited = new HashSet<>();
        if (start == null) return visited;
        
        Queue<EntityNode> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);
        
        int currentDepth = 0;
        while (!queue.isEmpty() && currentDepth <= depth) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                EntityNode current = queue.poll();
                
                for (RelationshipEdge edge : graph.getEdges(current)) {
                    EntityNode neighbor = edge.getDestination();
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
            currentDepth++;
        }
        
        return visited;
    }
    
    public static Set<EntityNode> findReasoningSubgraph(KnowledgeGraph graph, EntityNode start, List<EntityNode> targets) {
        if (start == null || graph == null) return Collections.emptySet();

        Map<EntityNode, Double> distances = new HashMap<>();
        Map<EntityNode, EntityNode> previous = new HashMap<>();
        PriorityQueue<EntityNode> queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        for (EntityNode node : graph.getAllNodes()) {
            distances.put(node, Double.MAX_VALUE);
            previous.put(node, null);
        }

        distances.put(start, 0.0);
        queue.add(start);

        while (!queue.isEmpty()) {
            EntityNode current = queue.poll();
            
            for (RelationshipEdge edge : graph.getEdges(current)) {
                EntityNode neighbor = edge.getDestination();
                double weight = 1.0 / (edge.getConfidence() + 0.001);
                double alt = distances.get(current) + weight;
                
                if (alt < distances.get(neighbor)) {
                    distances.put(neighbor, alt);
                    previous.put(neighbor, current);
                    
                    queue.remove(neighbor); 
                    queue.add(neighbor);
                }
            }
        }
        
        Set<EntityNode> subgraph = new HashSet<>();
        for (EntityNode target : targets) {
            EntityNode curr = target;
            if (previous.get(curr) != null || curr.equals(start)) {
                while (curr != null) {
                    subgraph.add(curr);
                    curr = previous.get(curr);
                }
            }
        }
        return subgraph;
    }
}
