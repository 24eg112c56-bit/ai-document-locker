package com.documentlocker.dto;

import com.documentlocker.model.Document;
import com.documentlocker.model.DocumentCategory;

import java.time.Instant;

public class DocumentResponse {
    private Long id;
    private String title;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private DocumentCategory category;
    private String tags;
    private String summary;
    private String keyEntitiesJson;
    private boolean aiProcessed;
    private Instant createdAt;
    private Instant updatedAt;

    public DocumentResponse() {}

    public static DocumentResponse fromEntity(Document doc) {
        DocumentResponse res = new DocumentResponse();
        res.setId(doc.getId());
        res.setTitle(doc.getTitle());
        res.setFileName(doc.getFileName());
        res.setFileType(doc.getFileType());
        res.setFileSize(doc.getFileSize());
        res.setCategory(doc.getCategory());
        res.setTags(doc.getTags());
        res.setSummary(doc.getSummary());
        res.setKeyEntitiesJson(doc.getKeyEntitiesJson());
        res.setAiProcessed(doc.isAiProcessed());
        res.setCreatedAt(doc.getCreatedAt());
        res.setUpdatedAt(doc.getUpdatedAt());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public DocumentCategory getCategory() {
        return category;
    }

    public void setCategory(DocumentCategory category) {
        this.category = category;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getKeyEntitiesJson() {
        return keyEntitiesJson;
    }

    public void setKeyEntitiesJson(String keyEntitiesJson) {
        this.keyEntitiesJson = keyEntitiesJson;
    }

    public boolean isAiProcessed() {
        return aiProcessed;
    }

    public void setAiProcessed(boolean aiProcessed) {
        this.aiProcessed = aiProcessed;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
