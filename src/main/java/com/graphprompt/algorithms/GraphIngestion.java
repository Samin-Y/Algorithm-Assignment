package com.graphprompt.algorithms;

import com.graphprompt.model.EntityNode;
import com.graphprompt.model.KnowledgeGraph;
import com.graphprompt.util.TokenEstimator;
import java.util.*;

public class GraphIngestion {
    
    // Simplistic NLP keyword extraction for demonstration purposes.
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "the", "is", "at", "which", "on", "in", "a", "an", "and", "or", "for", "with", "to", "of", "it",
        "what", "are", "how", "does", "help", "about", "i", "want", "know", "more", "so", "tell", "me", "is", "yes",
        "if", "goes", "down", "user:", "ai:"
    ));

    public static void ingest(KnowledgeGraph graph, String message) {
        // Very basic entity extraction: split by non-word chars, remove stop words
        String[] words = message.toLowerCase().split("[^a-zA-Z0-9.-]+");
        List<EntityNode> extractedEntities = new ArrayList<>();
        
        for (String w : words) {
            if (w.length() > 2 && !STOP_WORDS.contains(w)) {
                String id = w;
                String concept = w; // simplify
                EntityNode node = graph.getNode(id);
                if (node == null) {
                    node = new EntityNode(id, concept, TokenEstimator.estimateTokens(concept));
                    graph.addNode(node);
                } else {
                    node.incrementFrequency();
                    // Update recency (simulated by just resetting to a high value, will decay later)
                    node.setRecency(1.0); 
                }
                extractedEntities.add(node);
            }
        }
        
        // Create edges between adjacent entities in the text to simulate relationships
        for (int i = 0; i < extractedEntities.size() - 1; i++) {
            EntityNode e1 = extractedEntities.get(i);
            EntityNode e2 = extractedEntities.get(i+1);
            if (!e1.equals(e2)) {
                graph.addEdge(e1, e2, 0.8); // Default confidence
            }
        }
    }
}
