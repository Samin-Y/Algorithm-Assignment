package com.graphprompt.util;

public class TokenEstimator {
    public static int estimateTokens(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        // Simple estimation: 1 word ~ 1.3 tokens
        String[] words = text.trim().split("\\s+");
        return (int) Math.ceil(words.length * 1.3);
    }
}
