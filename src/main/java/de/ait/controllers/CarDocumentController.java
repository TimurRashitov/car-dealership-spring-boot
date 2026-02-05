package de.ait.controllers;
import de.ait.enums.CarDocumentType;
import de.ait.model.CarDocumentOs;
import de.ait.service.CarDocumentsOsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
@Slf4j
public class CarDocumentController {

    private final CarDocumentsOsService service;

    @PostMapping(value = "/{carId}/documents/os", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CarDocumentOs> uploadCarDocument(@PathVariable Long carId,
                                                           @RequestParam CarDocumentType docType,
                                                           @RequestPart("file") MultipartFile file){
        CarDocumentOs saved = service.uploadCarDocument(carId, docType, file);
        log.info("Car document with id {} saved", saved.getId());
        return  ResponseEntity.status(HttpStatus.CREATED).body(saved);

    }

    @GetMapping("/{carId}/documents/os")
    public ResponseEntity<List<CarDocumentOs>> getAllCarDocuments(@PathVariable Long carId){
        return ResponseEntity.ok(service.getAllCarDocument(carId));
    }

    @GetMapping("/documents/os/{documentId}/download")
    public ResponseEntity<FileSystemResource> downloadCarDocument(@PathVariable Long documentId){
        Path path = service.getDocumentPath(documentId);
        FileSystemResource resource = new FileSystemResource(path);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
