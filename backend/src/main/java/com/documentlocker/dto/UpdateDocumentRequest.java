package com.documentlocker.dto;

import com.documentlocker.model.DocumentCategory;
import jakarta.validation.constraints.NotBlank;

public class UpdateDocumentRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private DocumentCategory category;

    private String tags;

    public UpdateDocumentRequest() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
