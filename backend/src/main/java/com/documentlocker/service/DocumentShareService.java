package com.documentlocker.service;

import com.documentlocker.dto.ShareRequest;
import com.documentlocker.dto.ShareResponse;
import com.documentlocker.exception.ApiException;
import com.documentlocker.exception.ResourceNotFoundException;
import com.documentlocker.exception.UnauthorizedException;
import com.documentlocker.model.Document;
import com.documentlocker.model.DocumentShare;
import com.documentlocker.model.SharePermission;
import com.documentlocker.repository.DocumentShareRepository;
import com.documentlocker.security.UserPrincipal;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentShareService {

    private final DocumentShareRepository shareRepository;
    private final DocumentService documentService;
    private final FileStorageService fileStorageService;

    public DocumentShareService(DocumentShareRepository shareRepository,
                                DocumentService documentService,
                                FileStorageService fileStorageService) {
        this.shareRepository = shareRepository;
        this.documentService = documentService;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public ShareResponse createShare(Long documentId, ShareRequest request, UserPrincipal currentUser) {
        Document document = documentService.getDocumentEntity(documentId);

        if (!document.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only share documents you own.");
        }

        DocumentShare share = new DocumentShare();
        share.setDocument(document);
        share.setSharedWithEmail(request.getSharedWithEmail() != null ? request.getSharedWithEmail().trim().toLowerCase() : null);
        share.setShareToken(UUID.randomUUID().toString().replace("-", ""));
        share.setPermission(request.getPermission() != null ? request.getPermission() : SharePermission.VIEW);

        if (request.getExpiresInDays() != null && request.getExpiresInDays() > 0) {
            share.setExpiresAt(Instant.now().plus(request.getExpiresInDays(), ChronoUnit.DAYS));
        }

        DocumentShare saved = shareRepository.save(share);
        return ShareResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public ShareResponse getShareByToken(String shareToken) {
        DocumentShare share = shareRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new ResourceNotFoundException("Shared document not found or link has expired."));

        if (share.isExpired()) {
            throw new ApiException("This document link has expired.");
        }

        return ShareResponse.fromEntity(share);
    }

    @Transactional(readOnly = true)
    public Document getSharedDocumentByToken(String shareToken) {
        DocumentShare share = shareRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new ResourceNotFoundException("Shared document not found."));

        if (share.isExpired()) {
            throw new ApiException("This document link has expired.");
        }

        return share.getDocument();
    }

    @Transactional(readOnly = true)
    public Resource getSharedFileResource(String shareToken) {
        DocumentShare share = shareRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new ResourceNotFoundException("Shared document link invalid."));

        if (share.isExpired()) {
            throw new ApiException("This link has expired.");
        }

        return fileStorageService.loadFileAsResource(share.getDocument().getStoredFileName());
    }

    @Transactional(readOnly = true)
    public List<ShareResponse> getSharesForDocument(Long documentId, UserPrincipal currentUser) {
        Document document = documentService.getDocumentEntity(documentId);
        if (!document.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Access denied.");
        }

        return shareRepository.findByDocumentId(documentId).stream()
                .map(ShareResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void revokeShare(Long shareId, UserPrincipal currentUser) {
        DocumentShare share = shareRepository.findById(shareId)
                .orElseThrow(() -> new ResourceNotFoundException("Share record not found."));

        if (!share.getDocument().getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You cannot revoke this share link.");
        }

        shareRepository.delete(share);
    }
}
