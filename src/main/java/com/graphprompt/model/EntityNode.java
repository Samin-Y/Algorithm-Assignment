package com.graphprompt.model;

import java.util.Objects;

public class EntityNode {
    private String id;
    private String concept;
    private int frequency;
    private double recency;
    private int tokenWeight;

    // Transient algorithm state
    private double salienceScore;

    public EntityNode(String id, String concept, int tokenWeight) {
        this.id = id;
        this.concept = concept;
        this.tokenWeight = tokenWeight;
        this.frequency = 1;
        this.recency = 1.0;
        this.salienceScore = 0.0;
    }

    public String getId() { return id; }
    public String getConcept() { return concept; }
    public int getFrequency() { return frequency; }
    public void incrementFrequency() { this.frequency++; }
    public double getRecency() { return recency; }
    public void setRecency(double recency) { this.recency = recency; }
    public int getTokenWeight() { return tokenWeight; }
    public void setTokenWeight(int tokenWeight) { this.tokenWeight = tokenWeight; }
    public double getSalienceScore() { return salienceScore; }
    public void setSalienceScore(double salienceScore) { this.salienceScore = salienceScore; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityNode that = (EntityNode) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return concept;
    }
}
