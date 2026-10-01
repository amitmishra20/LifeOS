package com.lifeos.analytics.dto;

public class AnalyticsDashboardResponse {

    private ProductivityScoreResponse productivity;
    private GoalAnalyticsResponse goals;
    private TaskAnalyticsResponse tasks;
    private HabitAnalyticsResponse habits;
    private LearningAnalyticsResponse learning;

    public AnalyticsDashboardResponse() {
    }

    public AnalyticsDashboardResponse(ProductivityScoreResponse productivity, GoalAnalyticsResponse goals, TaskAnalyticsResponse tasks, HabitAnalyticsResponse habits, LearningAnalyticsResponse learning) {
        this.productivity = productivity;
        this.goals = goals;
        this.tasks = tasks;
        this.habits = habits;
        this.learning = learning;
    }

    public ProductivityScoreResponse getProductivity() {
        return productivity;
    }

    public void setProductivity(ProductivityScoreResponse productivity) {
        this.productivity = productivity;
    }

    public GoalAnalyticsResponse getGoals() {
        return goals;
    }

    public void setGoals(GoalAnalyticsResponse goals) {
        this.goals = goals;
    }

    public TaskAnalyticsResponse getTasks() {
        return tasks;
    }

    public void setTasks(TaskAnalyticsResponse tasks) {
        this.tasks = tasks;
    }

    public HabitAnalyticsResponse getHabits() {
        return habits;
    }

    public void setHabits(HabitAnalyticsResponse habits) {
        this.habits = habits;
    }

    public LearningAnalyticsResponse getLearning() {
        return learning;
    }

    public void setLearning(LearningAnalyticsResponse learning) {
        this.learning = learning;
    }
}
