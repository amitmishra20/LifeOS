package com.lifeos.calendar.dto;

import com.lifeos.calendar.entity.EventType;

import java.time.LocalDateTime;

public class UnifiedCalendarItemDto {

    private String id;
    private Long sourceId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private EventType itemType;
    private String status;
    private String priority;
    private String linkUrl;

    public UnifiedCalendarItemDto() {
    }

    public UnifiedCalendarItemDto(String id, Long sourceId, String title, String description,
                                  LocalDateTime startTime, LocalDateTime endTime,
                                  EventType itemType, String status, String priority, String linkUrl) {
        this.id = id;
        this.sourceId = sourceId;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.itemType = itemType;
        this.status = status;
        this.priority = priority;
        this.linkUrl = linkUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public EventType getItemType() {
        return itemType;
    }

    public void setItemType(EventType itemType) {
        this.itemType = itemType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
}
