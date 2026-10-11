package com.documentlocker.controller;

import com.documentlocker.dto.DocumentResponse;
import com.documentlocker.dto.DocumentStatsResponse;
import com.documentlocker.dto.UpdateDocumentRequest;
import com.documentlocker.model.Document;
import com.documentlocker.model.DocumentCategory;
import com.documentlocker.security.UserPrincipal;
import com.documentlocker.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "category", required = false) DocumentCategory category,
            @RequestParam(value = "tags", required = false) String tags,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DocumentResponse response = documentService.uploadDocument(file, title, category, tags, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getDocuments(
            @RequestParam(value = "category", required = false) DocumentCategory category,
            @RequestParam(value = "query", required = false) String query,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        List<DocumentResponse> documents = documentService.getUserDocuments(currentUser, category, query);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/stats")
    public ResponseEntity<DocumentStatsResponse> getStats(@AuthenticationPrincipal UserPrincipal currentUser) {
        DocumentStatsResponse stats = documentService.getUserStats(currentUser);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DocumentResponse document = documentService.getDocumentById(id, currentUser);
        return ResponseEntity.ok(document);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentResponse> updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDocumentRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DocumentResponse document = documentService.updateDocument(id, request, currentUser);
        return ResponseEntity.ok(document);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        documentService.deleteDocument(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long id,
            @RequestParam(value = "download", defaultValue = "false") boolean download,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Resource resource = documentService.getFileResource(id, currentUser);
        Document document = documentService.getDocumentEntity(id);

        String disposition = download ? "attachment" : "inline";
        String contentType = document.getFileType() != null ? document.getFileType() : "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition + "; filename=\"" + document.getFileName() + "\"")
                .body(resource);
    }
}
