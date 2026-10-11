package com.documentlocker.dto;

import com.documentlocker.model.SharePermission;

public class ShareRequest {

    private String sharedWithEmail;
    private SharePermission permission = SharePermission.VIEW;
    private Integer expiresInDays; // null means no expiration

    public ShareRequest() {}

    public String getSharedWithEmail() {
        return sharedWithEmail;
    }

    public void setSharedWithEmail(String sharedWithEmail) {
        this.sharedWithEmail = sharedWithEmail;
    }

    public SharePermission getPermission() {
        return permission;
    }

    public void setPermission(SharePermission permission) {
        this.permission = permission;
    }

    public Integer getExpiresInDays() {
        return expiresInDays;
    }

    public void setExpiresInDays(Integer expiresInDays) {
        this.expiresInDays = expiresInDays;
    }
}
