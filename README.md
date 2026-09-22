# Dynamic Knowledge Graph Retrieval (Graph-to-Prompt Engine)

This project is a sophisticated **Graph-to-Prompt Engine** simulation built using Java and JavaFX. It demonstrates how a dynamic knowledge graph can ingest data, calculate mathematical entity salience (node scoring), execute Dijkstra's algorithm for reasoning chain extraction, and optimize context token budgets using 0/1 Knapsack (DP) and Greedy packing algorithms.

The application features a modern, interactive **Gemini-inspired UI**, complete with an orbitable 3D knowledge graph, live metrics, and a mock AI conversation view.

---

## Features

- **Knowledge Graph Ingestion**: Automatically extracts entities from mock AI-User conversational data to build a connected graph.
- **Salience Node Ranking**: Mathematically scores each entity based on normalized frequency, exponential decay recency, and degree centrality.
- **Interactive 3D Graph Visualization**: The graph is rendered in a 3D space (`JavaFX SubScene`). You can click and drag to **rotate/orbit**, and scroll to **zoom**.
- **Dijkstra Reasoning Chains**: Finds the shortest, highest-confidence paths connecting the most salient entities, forming a sub-graph of reasoning chains.
- **Token Budget Assembly**: Efficiently packs the highest-salience context into a constrained token budget using either Exact **0/1 Knapsack (DP)** or **Greedy Optimization**.
- **Messenger Interface**: A sleek, dedicated chat view mimicking a live conversation between the User and AI.

---

## Prerequisites

- **Java Development Kit (JDK) 17+** (JavaFX requires modern JDK versions).
- **Gradle** (The project includes a Gradle Wrapper, so you don't need to install Gradle manually).

---

## How to Run

You can build and run the application directly from the terminal using the included Gradle Wrapper.

### On Windows:
Open Command Prompt or PowerShell in the root directory of the project and run:

```cmd
.\gradlew.bat build
.\gradlew.bat run
```

### On macOS / Linux:
Open a terminal in the root directory, make the wrapper executable (if it isn't already), and run:

```bash
chmod +x gradlew
./gradlew build
./gradlew run
```

---

## Using the Application

1. **Ingest Mock Data**: Click this button to populate the 3D graph with entities and relationships from the mock conversation.
2. **Interact with the Graph**: 
   - **Click & Drag**: Orbit the camera around the 3D entities.
   - **Scroll Wheel**: Zoom in and out.
3. **Adjust Sliders**: Use the text boxes or sliders to adjust the mathematical weights for `Alpha (Frequency)`, `Beta (Recency)`, `Gamma (Degree)`, and `Token Budget`.
4. **Run Dijkstra**: Click to extract the reasoning subgraph radiating from the most highly-ranked entity based on your slider settings.
5. **Assemble Prompt**: Click to pack the most important context into your token budget constraint and watch the metrics in the Output Console.
6. **Show Conversation**: Click to view the immersive messenger-style chat log.

---
