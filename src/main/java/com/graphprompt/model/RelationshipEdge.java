package com.graphprompt.model;

public class RelationshipEdge {
    private EntityNode source;
    private EntityNode destination;
    private double confidence;
    private int frequency;

    public RelationshipEdge(EntityNode source, EntityNode destination, double confidence) {
        this.source = source;
        this.destination = destination;
        this.confidence = confidence;
        this.frequency = 1;
    }

    public EntityNode getSource() { return source; }
    public EntityNode getDestination() { return destination; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public int getFrequency() { return frequency; }
    public void incrementFrequency() { this.frequency++; }
}
