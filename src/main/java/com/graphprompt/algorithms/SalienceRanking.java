package com.graphprompt.algorithms;

import com.graphprompt.model.EntityNode;
import com.graphprompt.model.KnowledgeGraph;
import java.util.ArrayList;
import java.util.List;

public class SalienceRanking {

    public static void rankNodes(KnowledgeGraph graph, double alpha, double beta, double gamma) {
        List<EntityNode> nodes = graph.getAllNodes();
        
        // Calculate Salience
        for (EntityNode node : nodes) {
            double freq = node.getFrequency();
            double recency = node.getRecency();
            double degree = graph.getDegree(node);
            
            double salience = (alpha * freq) + (beta * recency) + (gamma * degree);
            node.setSalienceScore(salience);
        }
    }
    
    public static List<EntityNode> sortNodes(List<EntityNode> nodes) {
        if (nodes == null || nodes.size() <= 1) {
            return nodes;
        }
        EntityNode[] array = nodes.toArray(new EntityNode[0]);
        mergeSort(array, 0, array.length - 1);
        return List.of(array);
    }
    
    private static void mergeSort(EntityNode[] array, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(array, left, mid);
            mergeSort(array, mid + 1, right);
            merge(array, left, mid, right);
        }
    }
    
    private static void merge(EntityNode[] array, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        
        EntityNode[] L = new EntityNode[n1];
        EntityNode[] R = new EntityNode[n2];
        
        for (int i = 0; i < n1; ++i) L[i] = array[left + i];
        for (int j = 0; j < n2; ++j) R[j] = array[mid + 1 + j];
        
        int i = 0, j = 0;
        int k = left;
        
        // Sort descending by salience
        while (i < n1 && j < n2) {
            if (L[i].getSalienceScore() >= R[j].getSalienceScore()) {
                array[k] = L[i];
                i++;
            } else {
                array[k] = R[j];
                j++;
            }
            k++;
        }
        
        while (i < n1) {
            array[k] = L[i];
            i++;
            k++;
        }
        
        while (j < n2) {
            array[k] = R[j];
            j++;
            k++;
        }
    }
}
