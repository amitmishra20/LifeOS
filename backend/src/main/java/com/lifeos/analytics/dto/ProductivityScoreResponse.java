package com.lifeos.analytics.dto;

import java.util.Map;

public class ProductivityScoreResponse {

    private double score;
    private ProductivityWeightsDto weights;
    private Map<String, ProductivityComponentDto> components;
    private int activeDomainCount;
    private int totalDomainCount;

    public ProductivityScoreResponse() {
    }

    public ProductivityScoreResponse(double score, ProductivityWeightsDto weights, Map<String, ProductivityComponentDto> components, int activeDomainCount, int totalDomainCount) {
        this.score = score;
        this.weights = weights;
        this.components = components;
        this.activeDomainCount = activeDomainCount;
        this.totalDomainCount = totalDomainCount;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public ProductivityWeightsDto getWeights() {
        return weights;
    }

    public void setWeights(ProductivityWeightsDto weights) {
        this.weights = weights;
    }

    public Map<String, ProductivityComponentDto> getComponents() {
        return components;
    }

    public void setComponents(Map<String, ProductivityComponentDto> components) {
        this.components = components;
    }

    public int getActiveDomainCount() {
        return activeDomainCount;
    }

    public void setActiveDomainCount(int activeDomainCount) {
        this.activeDomainCount = activeDomainCount;
    }

    public int getTotalDomainCount() {
        return totalDomainCount;
    }

    public void setTotalDomainCount(int totalDomainCount) {
        this.totalDomainCount = totalDomainCount;
    }
}
