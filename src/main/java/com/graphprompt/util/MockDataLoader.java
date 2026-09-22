package com.graphprompt.util;

import java.util.Arrays;
import java.util.List;

public class MockDataLoader {
    
    public static List<String> getMockChatTurns() {
        return Arrays.asList(
            "User: What are the specs for Span.io?",
            "AI: Span.io monitors circuit-level energy. It replaces the traditional electrical panel.",
            "User: How does circuit-level energy monitoring help with energy efficiency?",
            "AI: By tracking each circuit, you can identify power-hungry appliances and optimize their usage for better energy efficiency.",
            "User: What about smart grid integration?",
            "AI: Span.io supports smart grid features by allowing utilities to demand-response and manage loads dynamically.",
            "User: I want to know more about demand-response.",
            "AI: Demand-response programs incentivize users to reduce energy consumption during peak hours.",
            "User: How does solar tie into this?",
            "AI: Span.io integrates directly with solar inverters, optimizing energy storage and consumption based on grid rates.",
            "User: So energy storage is required?",
            "AI: Energy storage like a home battery is highly recommended to maximize the benefits of solar and demand-response.",
            "User: Tell me about the installation process.",
            "AI: Installation requires a certified electrician to replace the main panel and connect all existing circuits to the smart panel.",
            "User: Is it compatible with EV chargers?",
            "AI: Yes, it prioritizes EV charging schedules based on time-of-use rates and available solar power.",
            "User: What if the grid goes down?",
            "AI: During an outage, the system automatically isolates from the grid and powers essential circuits using the battery."
        );
    }
}
