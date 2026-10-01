package com.lifeos.analytics.dto;

public class ProductivityComponentDto {

    private double score;
    private double weight;
    private double contribution;
    private boolean active;

    public ProductivityComponentDto() {
    }

    public ProductivityComponentDto(double score, double weight, double contribution, boolean active) {
        this.score = score;
        this.weight = weight;
        this.contribution = contribution;
        this.active = active;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getContribution() {
        return contribution;
    }

    public void setContribution(double contribution) {
        this.contribution = contribution;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
