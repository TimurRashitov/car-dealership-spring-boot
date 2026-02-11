package de.ait.repository;

import de.ait.model.CandidateDocumentOs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateDocumentOsRepository extends JpaRepository<CandidateDocumentOs, Long> {

    List<CandidateDocumentOs> findAllByCandidateEmail(String email);

    List<CandidateDocumentOs> findAll();

    void deleteAllByCandidateEmail(String email);
}
