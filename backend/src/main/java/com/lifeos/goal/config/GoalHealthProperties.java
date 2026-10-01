package com.lifeos.goal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.goal-health")
public class GoalHealthProperties {

    private double atRiskThreshold = -5.0;
    private double behindThreshold = -25.0;

    public double getAtRiskThreshold() {
        return atRiskThreshold;
    }

    public void setAtRiskThreshold(double atRiskThreshold) {
        this.atRiskThreshold = atRiskThreshold;
    }

    public double getBehindThreshold() {
        return behindThreshold;
    }

    public void setBehindThreshold(double behindThreshold) {
        this.behindThreshold = behindThreshold;
    }
}
