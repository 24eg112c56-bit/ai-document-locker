package com.documentlocker.dto;

public class DocumentStatsResponse {
    private long totalDocuments;
    private long aiProcessedCount;
    private long totalSizeBytes;
    private long totalShares;

    public DocumentStatsResponse() {}

    public DocumentStatsResponse(long totalDocuments, long aiProcessedCount, long totalSizeBytes, long totalShares) {
        this.totalDocuments = totalDocuments;
        this.aiProcessedCount = aiProcessedCount;
        this.totalSizeBytes = totalSizeBytes;
        this.totalShares = totalShares;
    }

    public long getTotalDocuments() {
        return totalDocuments;
    }

    public void setTotalDocuments(long totalDocuments) {
        this.totalDocuments = totalDocuments;
    }

    public long getAiProcessedCount() {
        return aiProcessedCount;
    }

    public void setAiProcessedCount(long aiProcessedCount) {
        this.aiProcessedCount = aiProcessedCount;
    }

    public long getTotalSizeBytes() {
        return totalSizeBytes;
    }

    public void setTotalSizeBytes(long totalSizeBytes) {
        this.totalSizeBytes = totalSizeBytes;
    }

    public long getTotalShares() {
        return totalShares;
    }

    public void setTotalShares(long totalShares) {
        this.totalShares = totalShares;
    }
}
