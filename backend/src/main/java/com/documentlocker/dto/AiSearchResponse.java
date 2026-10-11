package com.documentlocker.dto;

import java.util.List;

public class AiSearchResponse {

    private String query;
    private String answer;
    private List<DocumentResponse> matchedDocuments;

    public AiSearchResponse() {}

    public AiSearchResponse(String query, String answer, List<DocumentResponse> matchedDocuments) {
        this.query = query;
        this.answer = answer;
        this.matchedDocuments = matchedDocuments;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<DocumentResponse> getMatchedDocuments() {
        return matchedDocuments;
    }

    public void setMatchedDocuments(List<DocumentResponse> matchedDocuments) {
        this.matchedDocuments = matchedDocuments;
    }
}
