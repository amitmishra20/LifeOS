package com.lifeos.analytics.dto;

public class TaskAnalyticsResponse {

    private double completionRate;
    private long totalTasks;
    private long completedTasks;
    private long todoTasks;
    private long inProgressTasks;
    private long overdueTasks;

    public TaskAnalyticsResponse() {
    }

    public TaskAnalyticsResponse(double completionRate, long totalTasks, long completedTasks, long todoTasks, long inProgressTasks, long overdueTasks) {
        this.completionRate = completionRate;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.todoTasks = todoTasks;
        this.inProgressTasks = inProgressTasks;
        this.overdueTasks = overdueTasks;
    }

    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }

    public long getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(long totalTasks) {
        this.totalTasks = totalTasks;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(long completedTasks) {
        this.completedTasks = completedTasks;
    }

    public long getTodoTasks() {
        return todoTasks;
    }

    public void setTodoTasks(long todoTasks) {
        this.todoTasks = todoTasks;
    }

    public long getInProgressTasks() {
        return inProgressTasks;
    }

    public void setInProgressTasks(long inProgressTasks) {
        this.inProgressTasks = inProgressTasks;
    }

    public long getOverdueTasks() {
        return overdueTasks;
    }

    public void setOverdueTasks(long overdueTasks) {
        this.overdueTasks = overdueTasks;
    }
}
