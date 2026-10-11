package com.documentlocker.service;

import com.documentlocker.dto.AiAnalysisResponse;
import com.documentlocker.model.Document;
import com.documentlocker.model.DocumentCategory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Service
public class AiService {

    private static final Logger logger = LoggerFactory.getLogger(AiService.class);

    @Value("${app.ai.gemini.api-key:}")
    private String geminiApiKey;

    @Value("${app.ai.gemini.model:gemini-2.0-flash}")
    private String geminiModel;

    @Value("${app.ai.gemini.api-url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String geminiApiUrl;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public AiService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    public boolean isAiConfigured() {
        return geminiApiKey != null && !geminiApiKey.trim().isBlank();
    }

    /**
     * Complete AI Analysis: Category classification, Executive summary, and Key Entities.
     */
    public AiAnalysisResponse analyzeDocument(String text, String fileName) {
        AiAnalysisResponse response = new AiAnalysisResponse();
        response.setRawPreview(text.length() > 300 ? text.substring(0, 300) + "..." : text);

        if (isAiConfigured() && !text.isBlank() && !text.startsWith("[Binary file")) {
            try {
                String prompt = buildAnalysisPrompt(text, fileName);
                String aiJson = callGemini(prompt);
                parseAiAnalysisJson(aiJson, response);
                return response;
            } catch (Exception e) {
                logger.error("Error during Gemini AI analysis: {}", e.getMessage());
            }
        }

        // Smart Heuristic Fallback
        applyHeuristicAnalysis(text, fileName, response);
        return response;
    }

    /**
     * Natural Language Query Answering across user's document repository.
     */
    public String answerNaturalLanguageQuery(String query, List<Document> contextDocuments) {
        if (!isAiConfigured() || contextDocuments.isEmpty()) {
            return generateLocalQuerySummary(query, contextDocuments);
        }

        try {
            StringBuilder context = new StringBuilder();
            for (Document doc : contextDocuments) {
                context.append("--- DOCUMENT #").append(doc.getId())
                        .append(" | Title: ").append(doc.getTitle())
                        .append(" | Category: ").append(doc.getCategory())
                        .append(" ---\n");
                if (doc.getSummary() != null && !doc.getSummary().isBlank()) {
                    context.append("Summary: ").append(doc.getSummary()).append("\n");
                }
                if (doc.getExtractedText() != null && !doc.getExtractedText().isBlank()) {
                    String snippet = doc.getExtractedText();
                    if (snippet.length() > 1500) {
                        snippet = snippet.substring(0, 1500) + "...";
                    }
                    context.append("Content Snippet: ").append(snippet).append("\n");
                }
                context.append("\n");
            }

            String prompt = "You are an intelligent document assistant in a secure Document Locker system.\n" +
                    "The user asks: \"" + query + "\"\n\n" +
                    "Here are the matching documents from their personal locker:\n" + context + "\n\n" +
                    "Please answer the user's question clearly, concisely, and accurately based on the documents above. " +
                    "Cite the document titles where appropriate. If the documents do not contain the answer, politely say so.";

            return callGemini(prompt);
        } catch (Exception e) {
            logger.error("Gemini query error: {}", e.getMessage());
            return generateLocalQuerySummary(query, contextDocuments);
        }
    }

    private String buildAnalysisPrompt(String text, String fileName) {
        return "You are an advanced AI document classifier and parser for a secure Document Locker.\n" +
                "Analyze the following document named \"" + fileName + "\":\n\n" +
                "\"\"\"\n" + (text.length() > 6000 ? text.substring(0, 6000) : text) + "\n\"\"\"\n\n" +
                "Respond ONLY with a valid JSON object in this exact schema (no markdown formatting, no code fence):\n" +
                "{\n" +
                "  \"category\": \"INVOICE|RESUME|ID_CARD|CONTRACT|ACADEMIC|MEDICAL|FINANCIAL|REPORT|OTHER\",\n" +
                "  \"summary\": \"Concise 2-3 sentence executive overview of what this document is.\",\n" +
                "  \"keyPoints\": [\"Key fact 1\", \"Key fact 2\", \"Key fact 3\"],\n" +
                "  \"metadata\": {\n" +
                "    \"dates\": [\"any mentioned dates\"],\n" +
                "    \"amounts\": [\"monetary figures if any\"],\n" +
                "    \"parties\": [\"people or organizations involved\"]\n" +
                "  }\n" +
                "}";
    }

    private String callGemini(String prompt) throws Exception {
        String url = String.format("%s/%s:generateContent?key=%s", geminiApiUrl, geminiModel, geminiApiKey);

        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> requestBodyMap = Map.of(
                "contents", List.of(content),
                "generationConfig", Map.of("temperature", 0.2)
        );

        String requestJson = objectMapper.writeValueAsString(requestBodyMap);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Gemini API call failed with code: " + response.statusCode() + " body: " + response.body());
        }

        JsonNode rootNode = objectMapper.readTree(response.body());
        JsonNode candidates = rootNode.path("candidates");
        if (candidates.isArray() && !candidates.isEmpty()) {
            JsonNode textNode = candidates.get(0).path("content").path("parts").get(0).path("text");
            return textNode.asText("");
        }

        throw new RuntimeException("Empty response from Gemini API");
    }

    private void parseAiAnalysisJson(String rawResponse, AiAnalysisResponse target) {
        try {
            String cleaned = rawResponse.trim();
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.substring(7);
            }
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.substring(3);
            }
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3);
            }
            cleaned = cleaned.trim();

            JsonNode node = objectMapper.readTree(cleaned);

            String catStr = node.path("category").asText("OTHER").toUpperCase();
            try {
                target.setCategory(DocumentCategory.valueOf(catStr));
            } catch (Exception e) {
                target.setCategory(DocumentCategory.OTHER);
            }

            target.setSummary(node.path("summary").asText("Document processed by AI."));

            List<String> points = new ArrayList<>();
            JsonNode keyPointsNode = node.path("keyPoints");
            if (keyPointsNode.isArray()) {
                keyPointsNode.forEach(p -> points.add(p.asText()));
            }
            target.setKeyPoints(points);

            Map<String, Object> meta = new HashMap<>();
            JsonNode metaNode = node.path("metadata");
            if (metaNode.isObject()) {
                metaNode.fields().forEachRemaining(entry -> meta.put(entry.getKey(), entry.getValue()));
            }
            target.setExtractedMetadata(meta);

        } catch (Exception e) {
            logger.warn("Could not parse AI JSON output directly: {}. Using text as summary.", e.getMessage());
            target.setSummary(rawResponse);
            target.setCategory(DocumentCategory.OTHER);
            target.setKeyPoints(List.of("Processed with Gemini AI"));
            target.setExtractedMetadata(Collections.emptyMap());
        }
    }

    private void applyHeuristicAnalysis(String text, String fileName, AiAnalysisResponse response) {
        String lower = (fileName + " " + text).toLowerCase();

        DocumentCategory category = DocumentCategory.OTHER;
        if (lower.contains("invoice") || lower.contains("bill") || lower.contains("receipt") || lower.contains("tax invoice") || lower.contains("subtotal")) {
            category = DocumentCategory.INVOICE;
        } else if (lower.contains("resume") || lower.contains("curriculum vitae") || lower.contains("education") && lower.contains("experience")) {
            category = DocumentCategory.RESUME;
        } else if (lower.contains("passport") || lower.contains("license") || lower.contains("identity") || lower.contains("aadhar") || lower.contains("ssn")) {
            category = DocumentCategory.ID_CARD;
        } else if (lower.contains("agreement") || lower.contains("contract") || lower.contains("terms") || lower.contains("clause")) {
            category = DocumentCategory.CONTRACT;
        } else if (lower.contains("transcript") || lower.contains("diploma") || lower.contains("grade") || lower.contains("syllabus") || lower.contains("semester")) {
            category = DocumentCategory.ACADEMIC;
        } else if (lower.contains("patient") || lower.contains("hospital") || lower.contains("doctor") || lower.contains("prescription") || lower.contains("medical")) {
            category = DocumentCategory.MEDICAL;
        } else if (lower.contains("bank") || lower.contains("statement") || lower.contains("balance") || lower.contains("investment")) {
            category = DocumentCategory.FINANCIAL;
        }

        response.setCategory(category);
        String previewText = text.isBlank() ? fileName : (text.length() > 250 ? text.substring(0, 250) + "..." : text);
        response.setSummary("Document (" + category + "): " + previewText.replaceAll("\n", " ").trim());
        response.setKeyPoints(List.of(
                "Categorized as " + category,
                "File name: " + fileName,
                isAiConfigured() ? "AI enabled" : "Configure GEMINI_API_KEY in .env for deeper AI insights"
        ));

        Map<String, Object> meta = new HashMap<>();
        meta.put("detectedCategory", category.name());
        meta.put("fileName", fileName);
        response.setExtractedMetadata(meta);
    }

    private String generateLocalQuerySummary(String query, List<Document> contextDocuments) {
        return "Found " + contextDocuments.size() + " document(s) matching \"" + query + "\": " +
                contextDocuments.stream().map(Document::getTitle).reduce((a, b) -> a + ", " + b).orElse("") +
                (isAiConfigured() ? "" : ". (Add GEMINI_API_KEY in .env for natural-language generative answers).");
    }
}
