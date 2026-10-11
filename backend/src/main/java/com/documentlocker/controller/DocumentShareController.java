package com.documentlocker.controller;

import com.documentlocker.dto.DocumentResponse;
import com.documentlocker.dto.ShareRequest;
import com.documentlocker.dto.ShareResponse;
import com.documentlocker.model.Document;
import com.documentlocker.model.SharePermission;
import com.documentlocker.security.UserPrincipal;
import com.documentlocker.service.DocumentShareService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class DocumentShareController {

    private final DocumentShareService shareService;

    public DocumentShareController(DocumentShareService shareService) {
        this.shareService = shareService;
    }

    @PostMapping("/api/documents/{id}/share")
    public ResponseEntity<ShareResponse> createShare(
            @PathVariable Long id,
            @RequestBody ShareRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        ShareResponse response = shareService.createShare(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/documents/{id}/shares")
    public ResponseEntity<List<ShareResponse>> getDocumentShares(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        List<ShareResponse> shares = shareService.getSharesForDocument(id, currentUser);
        return ResponseEntity.ok(shares);
    }

    @DeleteMapping("/api/documents/shares/{shareId}")
    public ResponseEntity<Void> revokeShare(
            @PathVariable Long shareId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        shareService.revokeShare(shareId, currentUser);
        return ResponseEntity.noContent().build();
    }

    // Public link access
    @GetMapping("/api/share/access/{token}")
    public ResponseEntity<Map<String, Object>> accessSharedDocument(@PathVariable String token) {
        ShareResponse share = shareService.getShareByToken(token);
        Document document = shareService.getSharedDocumentByToken(token);

        return ResponseEntity.ok(Map.of(
                "share", share,
                "document", DocumentResponse.fromEntity(document)
        ));
    }

    // Public file download
    @GetMapping("/api/share/access/{token}/file")
    public ResponseEntity<Resource> downloadSharedFile(
            @PathVariable String token,
            @RequestParam(value = "download", defaultValue = "false") boolean download) {

        ShareResponse share = shareService.getShareByToken(token);
        Document document = shareService.getSharedDocumentByToken(token);
        Resource resource = shareService.getSharedFileResource(token);

        String disposition = (download || share.getPermission() == SharePermission.DOWNLOAD) ? "attachment" : "inline";
        String contentType = document.getFileType() != null ? document.getFileType() : "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition + "; filename=\"" + document.getFileName() + "\"")
                .body(resource);
    }
}
