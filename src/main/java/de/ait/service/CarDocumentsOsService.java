package de.ait.service;

import de.ait.enums.CarDocumentType;
import de.ait.model.Car;
import de.ait.model.CarDocumentOs;
import de.ait.repository.CarDocumentOsRepository;
import de.ait.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarDocumentsOsService {

    private final CarRepository carRepository;

    private final CarDocumentOsRepository carDocumentOsRepository;

    @Value("${app.upload.car-docs-dir}")
    private String carDocsDir;

    @Value("${app.upload.car-doc-max-size}")
    private Long carDocMaxSize;

    public CarDocumentOs uploadCarDocument(Long carId, CarDocumentType doctype,
                                           MultipartFile file) {

        if (file == null || file.isEmpty()) {
            log.error("File is null or empty");
            throw new IllegalArgumentException("File is empty");
        }

        if(file.getSize() > carDocMaxSize){
            log.error("File size is too big {}file:{}", file.getSize(), file.getOriginalFilename());
            throw new IllegalArgumentException("File size is too big");
        }

        String contentType = file.getContentType();
        if(contentType == null || contentType.isBlank()){
            log.error("File content type is null or empty");
            throw new IllegalArgumentException("File content type is empty");
        }

        boolean allowed =
                contentType != null && (
                        contentType.startsWith("application/pdf") ||
                                contentType.equals("text/plain") ||
                                contentType.equals("image/jpeg") ||
                                contentType.equals("image/png")
                );

        if(!allowed){
            log.error("File content type is not allowed");
            throw new IllegalArgumentException("File content type is not allowed " + contentType);
        }

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car with id " + carId + " not found"));

        Path baseDir = Paths.get(carDocsDir);
        try {
            Files.createDirectories(baseDir);
            Path carDir = baseDir.resolve("car-" + carId);
            Files.createDirectories(carDir);

            String originalFilename = file.getOriginalFilename();

            String storedFilename = UUID.randomUUID() + "_" + originalFilename.toLowerCase();

            Path targetPath = carDir.resolve(storedFilename);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            CarDocumentOs doc = new CarDocumentOs(
                    car,
                    targetPath.toString(),
                    file.getSize(),
                    file.getContentType(),
                    storedFilename,
                    originalFilename,
                    doctype);

            CarDocumentOs savedDoc = carDocumentOsRepository.save(doc);

            log.info("Car document with id {} saved", savedDoc.getId());

            return savedDoc;

        } catch (IOException exception) {
            log.error("Error creating directory {}", carDocsDir, exception);
            throw new RuntimeException("Error creating directory " + carDocsDir, exception);
        } catch (NullPointerException exception) {
            log.error("Error to LowerCase {}", carDocsDir, exception);
            throw new RuntimeException("Error creating directory " + carDocsDir, exception);
        }
    }


    public List<CarDocumentOs> getAllCarDocument(Long carId) {
        return carDocumentOsRepository.findAllByCarId(carId);
    }


    public Path getDocumentPath(Long carDocumentId) {
        CarDocumentOs doc = carDocumentOsRepository.findById(carDocumentId).orElseThrow(
                () -> new IllegalArgumentException("Car document with id " + carDocumentId + " not found")
        );
        return Paths.get(doc.getStoragePath());
    }


}