package com.documentlocker.service;

import com.documentlocker.dto.*;
import com.documentlocker.exception.ApiException;
import com.documentlocker.exception.ResourceNotFoundException;
import com.documentlocker.exception.UnauthorizedException;
import com.documentlocker.model.Document;
import com.documentlocker.model.DocumentCategory;
import com.documentlocker.model.User;
import com.documentlocker.repository.DocumentRepository;
import com.documentlocker.repository.DocumentShareRepository;
import com.documentlocker.security.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentShareRepository shareRepository;
    private final FileStorageService fileStorageService;
    private final PdfExtractionService pdfExtractionService;
    private final AiService aiService;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    public DocumentService(DocumentRepository documentRepository,
                           DocumentShareRepository shareRepository,
                           FileStorageService fileStorageService,
                           PdfExtractionService pdfExtractionService,
                           AiService aiService,
                           AuthService authService,
                           ObjectMapper objectMapper) {
        this.documentRepository = documentRepository;
        this.shareRepository = shareRepository;
        this.fileStorageService = fileStorageService;
        this.pdfExtractionService = pdfExtractionService;
        this.aiService = aiService;
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public DocumentResponse uploadDocument(MultipartFile file,
                                           String title,
                                           DocumentCategory category,
                                           String tags,
                                           UserPrincipal currentUser) {
        if (file == null || file.isEmpty()) {
            throw new ApiException("Cannot upload an empty file");
        }

        User user = authService.getUserEntity(currentUser.getId());

        // 1. Store file safely in isolated storage
        FileStorageService.StoredFileInfo fileInfo = fileStorageService.storeFile(file, user.getId());

        // 2. Extract selectable text from file
        String extractedText = pdfExtractionService.extractText(fileInfo.filePath(), fileInfo.originalFileName());

        // 3. AI Analysis
        AiAnalysisResponse aiAnalysis = aiService.analyzeDocument(extractedText, fileInfo.originalFileName());

        Document document = new Document();
        document.setOwner(user);
        document.setTitle((title != null && !title.isBlank()) ? title.trim() : fileInfo.originalFileName());
        document.setFileName(fileInfo.originalFileName());
        document.setStoredFileName(fileInfo.storedFileName());
        document.setFilePath(fileInfo.filePath());
        document.setFileType(fileInfo.contentType());
        document.setFileSize(fileInfo.fileSize());
        document.setTags(tags != null ? tags.trim() : "");
        document.setExtractedText(extractedText);

        // Auto-assign category if not explicitly provided or if OTHER
        if (category == null || category == DocumentCategory.OTHER) {
            document.setCategory(aiAnalysis.getCategory());
        } else {
            document.setCategory(category);
        }

        document.setSummary(aiAnalysis.getSummary());
        try {
            document.setKeyEntitiesJson(objectMapper.writeValueAsString(aiAnalysis.getExtractedMetadata()));
        } catch (Exception e) {
            document.setKeyEntitiesJson("{}");
        }
        document.setAiProcessed(true);

        Document savedDoc = documentRepository.save(document);
        return DocumentResponse.fromEntity(savedDoc);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> getUserDocuments(UserPrincipal currentUser,
                                                   DocumentCategory category,
                                                   String query) {
        List<Document> docs;

        if (query != null && !query.trim().isBlank()) {
            String q = query.trim().toLowerCase();
            List<Document> all = documentRepository.findByOwnerIdOrderByCreatedAtDesc(currentUser.getId());
            docs = all.stream().filter(d ->
                (d.getTitle() != null && d.getTitle().toLowerCase().contains(q)) ||
                (d.getFileName() != null && d.getFileName().toLowerCase().contains(q)) ||
                (d.getTags() != null && d.getTags().toLowerCase().contains(q)) ||
                (d.getSummary() != null && d.getSummary().toLowerCase().contains(q)) ||
                (d.getExtractedText() != null && d.getExtractedText().toLowerCase().contains(q))
            ).collect(Collectors.toList());
        } else if (category != null) {
            docs = documentRepository.findByOwnerIdAndCategoryOrderByCreatedAtDesc(currentUser.getId(), category);
        } else {
            docs = documentRepository.findByOwnerIdOrderByCreatedAtDesc(currentUser.getId());
        }

        return docs.stream()
                .map(DocumentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DocumentResponse getDocumentById(Long id, UserPrincipal currentUser) {
        Document document = getDocumentEntityWithPermission(id, currentUser.getId());
        return DocumentResponse.fromEntity(document);
    }

    @Transactional
    public DocumentResponse updateDocument(Long id, UpdateDocumentRequest request, UserPrincipal currentUser) {
        Document document = getDocumentEntityWithPermission(id, currentUser.getId());

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            document.setTitle(request.getTitle().trim());
        }
        if (request.getCategory() != null) {
            document.setCategory(request.getCategory());
        }
        if (request.getTags() != null) {
            document.setTags(request.getTags().trim());
        }
        document.setUpdatedAt(Instant.now());

        return DocumentResponse.fromEntity(documentRepository.save(document));
    }

    @Transactional
    public void deleteDocument(Long id, UserPrincipal currentUser) {
        Document document = getDocumentEntityWithPermission(id, currentUser.getId());

        // Delete active shares
        shareRepository.deleteByDocumentId(document.getId());

        // Delete physical file from disk
        fileStorageService.deleteFile(document.getStoredFileName());

        // Delete entity
        documentRepository.delete(document);
    }

    @Transactional(readOnly = true)
    public Resource getFileResource(Long id, UserPrincipal currentUser) {
        Document document = getDocumentEntityWithPermission(id, currentUser.getId());
        return fileStorageService.loadFileAsResource(document.getStoredFileName());
    }

    @Transactional(readOnly = true)
    public Document getDocumentEntity(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
    }

    @Transactional
    public DocumentResponse reprocessWithAi(Long id, UserPrincipal currentUser) {
        Document document = getDocumentEntityWithPermission(id, currentUser.getId());

        // If text wasn't extracted yet, extract now
        if (document.getExtractedText() == null || document.getExtractedText().isBlank()) {
            String text = pdfExtractionService.extractText(document.getFilePath(), document.getFileName());
            document.setExtractedText(text);
        }

        AiAnalysisResponse analysis = aiService.analyzeDocument(document.getExtractedText(), document.getFileName());
        document.setCategory(analysis.getCategory());
        document.setSummary(analysis.getSummary());
        try {
            document.setKeyEntitiesJson(objectMapper.writeValueAsString(analysis.getExtractedMetadata()));
        } catch (Exception e) {
            document.setKeyEntitiesJson("{}");
        }
        document.setAiProcessed(true);
        document.setUpdatedAt(Instant.now());

        return DocumentResponse.fromEntity(documentRepository.save(document));
    }

    @Transactional(readOnly = true)
    public AiSearchResponse searchDocumentsWithAi(String query, UserPrincipal currentUser) {
        List<Document> matchedDocs = documentRepository.searchByOwnerAndQuery(currentUser.getId(), query.trim());
        if (matchedDocs.isEmpty()) {
            // fallback: check all user docs
            matchedDocs = documentRepository.findByOwnerIdOrderByCreatedAtDesc(currentUser.getId());
        }

        String aiAnswer = aiService.answerNaturalLanguageQuery(query, matchedDocs);

        List<DocumentResponse> docResponses = matchedDocs.stream()
                .limit(5)
                .map(DocumentResponse::fromEntity)
                .collect(Collectors.toList());

        return new AiSearchResponse(query, aiAnswer, docResponses);
    }

    @Transactional(readOnly = true)
    public DocumentStatsResponse getUserStats(UserPrincipal currentUser) {
        long totalDocs = documentRepository.countByOwnerId(currentUser.getId());
        long aiProcessed = documentRepository.countByOwnerIdAndAiProcessedTrue(currentUser.getId());
        Long totalSize = documentRepository.sumFileSizeByOwnerId(currentUser.getId());
        long totalShares = shareRepository.findByDocumentOwnerId(currentUser.getId()).size();

        return new DocumentStatsResponse(totalDocs, aiProcessed, totalSize != null ? totalSize : 0L, totalShares);
    }

    private Document getDocumentEntityWithPermission(Long docId, Long userId) {
        Document document = documentRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + docId));

        if (!document.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Access denied: You are not the owner of this document.");
        }

        return document;
    }
}
