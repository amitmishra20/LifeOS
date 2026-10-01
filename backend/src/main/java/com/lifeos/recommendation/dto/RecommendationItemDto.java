package com.lifeos.recommendation.dto;

import java.util.ArrayList;
import java.util.List;

public class RecommendationItemDto {

    private String id;
    private RecommendationType type;
    private Long entityId;
    private String title;
    private String subtitle;
    private String primaryReason;
    private List<String> reasons = new ArrayList<>();
    private String actionUrl;

    public RecommendationItemDto() {
    }

    public RecommendationItemDto(
            String id,
            RecommendationType type,
            Long entityId,
            String title,
            String subtitle,
            String primaryReason,
            List<String> reasons,
            String actionUrl
    ) {
        this.id = id;
        this.type = type;
        this.entityId = entityId;
        this.title = title;
        this.subtitle = subtitle;
        this.primaryReason = primaryReason;
        this.reasons = reasons != null ? reasons : new ArrayList<>();
        this.actionUrl = actionUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RecommendationType getType() {
        return type;
    }

    public void setType(RecommendationType type) {
        this.type = type;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getPrimaryReason() {
        return primaryReason;
    }

    public void setPrimaryReason(String primaryReason) {
        this.primaryReason = primaryReason;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }
}
