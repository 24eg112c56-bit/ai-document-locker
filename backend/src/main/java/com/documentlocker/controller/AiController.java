package com.documentlocker.controller;

import com.documentlocker.dto.AiSearchRequest;
import com.documentlocker.dto.AiSearchResponse;
import com.documentlocker.dto.DocumentResponse;
import com.documentlocker.security.UserPrincipal;
import com.documentlocker.service.AiService;
import com.documentlocker.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final DocumentService documentService;
    private final AiService aiService;

    public AiController(DocumentService documentService, AiService aiService) {
        this.documentService = documentService;
        this.aiService = aiService;
    }

    @PostMapping("/documents/{id}/reprocess")
    public ResponseEntity<DocumentResponse> reprocessDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DocumentResponse response = documentService.reprocessWithAi(id, currentUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<AiSearchResponse> aiSearch(
            @Valid @RequestBody AiSearchRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        AiSearchResponse response = documentService.searchDocumentsWithAi(request.getQuery(), currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAiStatus() {
        return ResponseEntity.ok(Map.of(
                "aiConfigured", aiService.isAiConfigured(),
                "provider", "Google Gemini"
        ));
    }
}
