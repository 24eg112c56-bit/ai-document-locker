package com.documentlocker.service;

import com.documentlocker.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${app.storage.upload-dir:./storage/documents}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new ApiException("Could not create the directory where uploaded files will be stored", ex);
        }
    }

    public record StoredFileInfo(String originalFileName, String storedFileName, String filePath, long fileSize, String contentType) {}

    public StoredFileInfo storeFile(MultipartFile file, Long userId) {
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        if (originalFileName.contains("..")) {
            throw new ApiException("Filename contains invalid path sequence: " + originalFileName);
        }

        String fileExtension = "";
        int extIndex = originalFileName.lastIndexOf(".");
        if (extIndex >= 0) {
            fileExtension = originalFileName.substring(extIndex);
        }

        String storedFileName = "u" + userId + "_" + UUID.randomUUID() + fileExtension;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            return new StoredFileInfo(
                    originalFileName,
                    storedFileName,
                    targetLocation.toString(),
                    file.getSize(),
                    file.getContentType()
            );
        } catch (IOException ex) {
            throw new ApiException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String storedFileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(storedFileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ApiException("File not found or not readable: " + storedFileName);
            }
        } catch (MalformedURLException ex) {
            throw new ApiException("File not found: " + storedFileName, ex);
        }
    }

    public void deleteFile(String storedFileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(storedFileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            // Log and ignore delete errors if already missing
        }
    }
}
