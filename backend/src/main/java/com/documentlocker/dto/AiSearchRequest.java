package com.documentlocker.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class AiSearchRequest {

    @NotBlank(message = "Query is required")
    private String query;

    public AiSearchRequest() {}

    public AiSearchRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
