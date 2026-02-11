package de.ait.service;

import de.ait.enums.CandidateDocType;
import de.ait.model.CandidateDocumentOs;
import de.ait.repository.CandidateDocumentOsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateDocumentsOsService {

    private final CandidateDocumentOsRepository candidateDocumentOsRepository;

    @Value("${app.upload.candidate-docs-dir}")
    private String candidateDocsDir;

    public CandidateDocumentOs uploadCandidateDocumentOs(String candidateEmail,
                                                         CandidateDocType docType,
                                                         MultipartFile file) {
        if (candidateEmail == null || candidateEmail.isBlank()) {
            log.error("Invalid candidate email: {}", candidateEmail);
            throw new IllegalArgumentException("Invalid candidate email");
        }

        if (file == null || file.isEmpty()) {
            log.error("File is null or empty");
            throw new IllegalArgumentException("File is empty");
        }

        if (docType == null) {
            throw new IllegalArgumentException("Document type is required");
        }

        Path baseDir = Path.of(candidateDocsDir);

        try {
            Files.createDirectories(baseDir);
            Path candidateEmailDir = baseDir.resolve(candidateEmail);
            Files.createDirectories(candidateEmailDir);
            Path candidateDocTypeDir = candidateEmailDir.resolve(docType.name());
            Files.createDirectories(candidateDocTypeDir);
            /* можно так короче сделать:
            Path targetDir = Path.of(candidateDocsDir)
                             .resolve(candidateEmail)
                             .resolve(docType.name());
            Files.createDirectories(targetDir); */

            String originalFilename = file.getOriginalFilename();

            String storedFilename = UUID.randomUUID() + "_" + originalFilename;

            Path targetPath = candidateDocTypeDir.resolve(storedFilename);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            // file.transferTo(targetPath); еще можно так написать эту строку (через MultipartFile)

            CandidateDocumentOs doc = new CandidateDocumentOs(
                    candidateEmail,
                    docType,
                    originalFilename,
                    storedFilename,
                    file.getContentType(),
                    file.getSize(),
                    targetPath.toString(),
                    LocalDateTime.now());

            CandidateDocumentOs savedDoc = candidateDocumentOsRepository.save(doc);

            log.info("Candidate document with id {} saved", savedDoc.getId());

            return savedDoc;

        } catch (IOException exception) {
            log.error("IO error during file upload to {}: {}", candidateDocsDir, exception.getMessage());
            throw new RuntimeException("Could not store file on disk", exception);
        }
    }


    public List<CandidateDocumentOs> getAllCandidateDocumentsByEmail(String candidateEmail) {
        return candidateDocumentOsRepository.findAllByCandidateEmail(candidateEmail);
    }


    public List<CandidateDocumentOs> getAllCandidateDocuments(){
        return candidateDocumentOsRepository.findAll();
    }


    public Path getCandidateDocumentPath(Long candidateDocumentId) {
        CandidateDocumentOs candidateDocument= candidateDocumentOsRepository.findById(candidateDocumentId).orElseThrow(
                () -> new IllegalArgumentException("Candidate document with id " + candidateDocumentId + " not found")
        );
        return Path.of(candidateDocument.getStoragePath());
    }


    @Transactional // Важно: если что-то пойдет не так, транзакция поможет сохранить целостность
    public void deleteCandidateDocById(Long docId){
        // 1. Находим документ в базе, чтобы узнать путь к файлу
        CandidateDocumentOs doc = candidateDocumentOsRepository.findById(docId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + docId));

        try {
            // 2. Удаляем файл с физического диска
            Path path = Path.of(doc.getStoragePath());
            boolean deleted = Files.deleteIfExists(path);

            if (deleted) {
                log.info("File deleted from disk: {}", path);
            } else {
                log.warn("File not found on disk, but record existed in DB: {}", path);
            }

            // 3. Удаляем запись из базы данных
            candidateDocumentOsRepository.deleteById(docId);
            log.info("Database record with id {} deleted", docId);

        } catch (IOException e) {
            log.error("Could not delete file for document id {}: {}", docId, e.getMessage());
            throw new RuntimeException("Error during file deletion", e);
        }
    }


    @Transactional
    public void deleteCandidateDocByEmail(String candidateEmail) {
        List<CandidateDocumentOs> docEmailAll = candidateDocumentOsRepository.findAllByCandidateEmail(candidateEmail);

        for (CandidateDocumentOs doc: docEmailAll) {
            try {
                Path path = Path.of(doc.getStoragePath());
                Files.deleteIfExists(path);
                log.info("File deleted from disk: {}", path);

            } catch (IOException exception) {
                log.error("Could not delete file for document id: {}", doc.getId(), exception);
                throw new RuntimeException("Error during file deletion", exception);
            }

            candidateDocumentOsRepository.deleteAllByCandidateEmail(candidateEmail);
            log.info("All document records for {} deleted from database", candidateEmail);
        }
    }

}
