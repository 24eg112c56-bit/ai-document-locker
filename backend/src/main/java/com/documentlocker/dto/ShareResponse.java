package com.documentlocker.dto;

import com.documentlocker.model.DocumentShare;
import com.documentlocker.model.SharePermission;

import java.time.Instant;

public class ShareResponse {

    private Long id;
    private Long documentId;
    private String documentTitle;
    private String sharedWithEmail;
    private String shareToken;
    private SharePermission permission;
    private Instant expiresAt;
    private boolean expired;
    private Instant createdAt;

    public ShareResponse() {}

    public static ShareResponse fromEntity(DocumentShare share) {
        ShareResponse res = new ShareResponse();
        res.setId(share.getId());
        res.setDocumentId(share.getDocument().getId());
        res.setDocumentTitle(share.getDocument().getTitle());
        res.setSharedWithEmail(share.getSharedWithEmail());
        res.setShareToken(share.getShareToken());
        res.setPermission(share.getPermission());
        res.setExpiresAt(share.getExpiresAt());
        res.setExpired(share.isExpired());
        res.setCreatedAt(share.getCreatedAt());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getSharedWithEmail() {
        return sharedWithEmail;
    }

    public void setSharedWithEmail(String sharedWithEmail) {
        this.sharedWithEmail = sharedWithEmail;
    }

    public String getShareToken() {
        return shareToken;
    }

    public void setShareToken(String shareToken) {
        this.shareToken = shareToken;
    }

    public SharePermission getPermission() {
        return permission;
    }

    public void setPermission(SharePermission permission) {
        this.permission = permission;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
