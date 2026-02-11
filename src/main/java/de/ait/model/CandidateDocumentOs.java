package de.ait.model;

import de.ait.enums.CandidateDocType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_documents_os")
@Getter
@Setter
@NoArgsConstructor
public class CandidateDocumentOs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_email", nullable = false)
    private String candidateEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false)
    private CandidateDocType docType;

    @Column(name = "original_filename", nullable = false)
    private String originalFileName;

    @Column(name = "stored_filename", nullable = false)
    private String storedFilename;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long size;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public CandidateDocumentOs(String candidateEmail,
                               CandidateDocType docType,
                               String originalFileName,
                               String storedFilename,
                               String contentType,
                               Long size,
                               String storagePath,
                               LocalDateTime createdAt) {
        this.candidateEmail = candidateEmail;
        this.docType = docType;
        this.originalFileName = originalFileName;
        this.storedFilename = storedFilename;
        this.contentType = contentType;
        this.size = size;
        this.storagePath = storagePath;
        this.createdAt = createdAt;
    }
}
