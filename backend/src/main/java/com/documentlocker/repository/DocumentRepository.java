package com.documentlocker.repository;

import com.documentlocker.model.Document;
import com.documentlocker.model.DocumentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    List<Document> findByOwnerIdAndCategoryOrderByCreatedAtDesc(Long ownerId, DocumentCategory category);

    Optional<Document> findByIdAndOwnerId(Long id, Long ownerId);

    @Query("SELECT d FROM Document d WHERE d.owner.id = :ownerId AND (" +
           "LOWER(d.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.fileName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.tags) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY d.createdAt DESC")
    List<Document> searchByOwnerAndQuery(@Param("ownerId") Long ownerId, @Param("query") String query);

    long countByOwnerId(Long ownerId);

    long countByOwnerIdAndAiProcessedTrue(Long ownerId);

    @Query("SELECT COALESCE(SUM(d.fileSize), 0) FROM Document d WHERE d.owner.id = :ownerId")
    Long sumFileSizeByOwnerId(@Param("ownerId") Long ownerId);
}
