package com.documentlocker.repository;

import com.documentlocker.model.DocumentShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentShareRepository extends JpaRepository<DocumentShare, Long> {

    Optional<DocumentShare> findByShareToken(String shareToken);

    List<DocumentShare> findByDocumentId(Long documentId);

    List<DocumentShare> findByDocumentOwnerId(Long ownerId);

    List<DocumentShare> findBySharedWithEmail(String sharedWithEmail);

    void deleteByDocumentId(Long documentId);
}
