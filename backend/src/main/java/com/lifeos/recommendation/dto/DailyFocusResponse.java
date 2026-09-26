package com.lifeos.recommendation.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyFocusResponse {

    private LocalDate date;
    private List<FocusItemResponse> items = new ArrayList<>();
    private int totalFocusItems;
    private FocusItemResponse primaryFocus;
    private boolean allCompleted;

    public DailyFocusResponse() {
        this.date = LocalDate.now();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<FocusItemResponse> getItems() {
        return items;
    }

    public void setItems(List<FocusItemResponse> items) {
        this.items = items != null ? items : new ArrayList<>();
        this.totalFocusItems = this.items.size();
        this.primaryFocus = !this.items.isEmpty() ? this.items.get(0) : null;
    }

    public int getTotalFocusItems() {
        return totalFocusItems;
    }

    public void setTotalFocusItems(int totalFocusItems) {
        this.totalFocusItems = totalFocusItems;
    }

    public FocusItemResponse getPrimaryFocus() {
        return primaryFocus;
    }

    public void setPrimaryFocus(FocusItemResponse primaryFocus) {
        this.primaryFocus = primaryFocus;
    }

    public boolean isAllCompleted() {
        return allCompleted;
    }

    public void setAllCompleted(boolean allCompleted) {
        this.allCompleted = allCompleted;
    }
}
