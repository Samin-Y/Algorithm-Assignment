package com.graphprompt.algorithms;

import com.graphprompt.model.EntityNode;
import java.util.*;

public class TokenKnapsack {

    public static List<EntityNode> packDP(List<EntityNode> sortedNodes, int budget) {
        int n = sortedNodes.size();
        if (n == 0 || budget <= 0) return new ArrayList<>();

        double[][] dp = new double[n + 1][budget + 1];
        
        for (int i = 1; i <= n; i++) {
            EntityNode node = sortedNodes.get(i - 1);
            int weight = node.getTokenWeight();
            double value = node.getSalienceScore();

            for (int w = 1; w <= budget; w++) {
                if (weight <= w) {
                    dp[i][w] = Math.max(dp[i - 1][w], dp[i - 1][w - weight] + value);
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        List<EntityNode> selected = new ArrayList<>();
        int w = budget;
        for (int i = n; i > 0 && w > 0; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                EntityNode node = sortedNodes.get(i - 1);
                selected.add(node);
                w -= node.getTokenWeight();
            }
        }
        return selected;
    }

    public static List<EntityNode> packGreedy(List<EntityNode> sortedNodes, int budget) {
        List<EntityNode> selected = new ArrayList<>();
        
        List<EntityNode> ratioSorted = new ArrayList<>(sortedNodes);
        ratioSorted.sort((a, b) -> {
            double ratioA = a.getSalienceScore() / Math.max(1, a.getTokenWeight());
            double ratioB = b.getSalienceScore() / Math.max(1, b.getTokenWeight());
            return Double.compare(ratioB, ratioA); 
        });

        int currentWeight = 0;
        for (EntityNode node : ratioSorted) {
            if (currentWeight + node.getTokenWeight() <= budget) {
                selected.add(node);
                currentWeight += node.getTokenWeight();
            }
        }

        return selected;
    }
}
