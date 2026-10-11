package com.documentlocker.dto;

import com.documentlocker.model.DocumentCategory;

import java.util.List;
import java.util.Map;

public class AiAnalysisResponse {
    private DocumentCategory category;
    private String summary;
    private List<String> keyPoints;
    private Map<String, Object> extractedMetadata;
    private String rawPreview;

    public AiAnalysisResponse() {}

    public DocumentCategory getCategory() {
        return category;
    }

    public void setCategory(DocumentCategory category) {
        this.category = category;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getKeyPoints() {
        return keyPoints;
    }

    public void setKeyPoints(List<String> keyPoints) {
        this.keyPoints = keyPoints;
    }

    public Map<String, Object> getExtractedMetadata() {
        return extractedMetadata;
    }

    public void setExtractedMetadata(Map<String, Object> extractedMetadata) {
        this.extractedMetadata = extractedMetadata;
    }

    public String getRawPreview() {
        return rawPreview;
    }

    public void setRawPreview(String rawPreview) {
        this.rawPreview = rawPreview;
    }
}
