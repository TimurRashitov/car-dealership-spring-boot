package de.ait.controllers;

import de.ait.enums.CandidateDocType;
import de.ait.model.CandidateDocumentOs;
import de.ait.service.CandidateDocumentsOsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
@Slf4j
public class CandidateDocumentController {

    private final CandidateDocumentsOsService candidateDocumentsOsService;

    @PostMapping(value = "/{candidateEmail}/documents/os", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CandidateDocumentOs> uploadCandidateDocument(@PathVariable String candidateEmail,
                                                                       @RequestParam CandidateDocType docType,
                                                                       @RequestPart("file")MultipartFile file) {
        CandidateDocumentOs saved = candidateDocumentsOsService.uploadCandidateDocumentOs(candidateEmail, docType, file);
        log.info("Candidate document type {} from email {} saved", saved.getDocType(), saved.getCandidateEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{candidateEmail}/documents/os")
    public ResponseEntity<List<CandidateDocumentOs>> getAllCandidateDocumentsByEmail(@PathVariable String candidateEmail){
        log.info("Fetching documents for candidate: {}", candidateEmail);
        return ResponseEntity.ok(candidateDocumentsOsService.getAllCandidateDocumentsByEmail(candidateEmail));
    }

    @GetMapping("/documents/os/all")
    public ResponseEntity<List<CandidateDocumentOs>> getAllCandidateDocuments(){
        log.info("Fetching all documents from the database");
        return ResponseEntity.ok(candidateDocumentsOsService.getAllCandidateDocuments());
    }

    @GetMapping("/documents/os/{docId}/download")
    public ResponseEntity<FileSystemResource> downloadCandidateDoc(@PathVariable Long docId){
        Path path = candidateDocumentsOsService.getCandidateDocumentPath(docId);
        FileSystemResource resource = new FileSystemResource(path);

        if (!resource.exists()) {
            log.error("File not found on disk: {}", path);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @DeleteMapping("documents/os/{docId}")
    public ResponseEntity<Void> deleteCandidateDoc(@PathVariable Long docId) {
        log.info("Request to delete document with id: {}", docId);
        candidateDocumentsOsService.deleteCandidateDocById(docId);
        return ResponseEntity.noContent().build(); // Возвращает статус 204
    }

    @Operation(summary = "Delete all documents for candidate email")
    @DeleteMapping("/{candidateEmail}/documents/os")
    public ResponseEntity<Void> deleteAllCandidateDocsByEmail(@PathVariable String candidateEmail){
        log.info("Request to delete all documents for candidate: {}", candidateEmail);
        candidateDocumentsOsService.deleteCandidateDocByEmail(candidateEmail);
        return ResponseEntity.noContent().build();
    }


}
