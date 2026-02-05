package de.ait.repository;

import de.ait.model.ClientDocumentDb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientDocumentDbRepository extends JpaRepository<ClientDocumentDb, Long> {

    List<ClientDocumentDb> findAllByClientEmail(String clientEmail);
}
