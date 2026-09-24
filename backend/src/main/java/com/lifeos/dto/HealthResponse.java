package com.lifeos.dto;

import java.time.Instant;

public class HealthResponse {

    private String status;
    private Instant timestamp;
    private String version;

    public HealthResponse() {
    }

    public HealthResponse(String status, Instant timestamp, String version) {
        this.status = status;
        this.timestamp = timestamp;
        this.version = version;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public static class Builder {
        private String status;
        private Instant timestamp;
        private String version;

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public HealthResponse build() {
            return new HealthResponse(status, timestamp, version);
        }
    }
}
