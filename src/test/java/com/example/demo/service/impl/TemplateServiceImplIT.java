package com.example.demo.service.impl;

import com.example.demo.domain.DocumentEntity;
import com.example.demo.model.Document;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.service.exception.DocumentNotFoundException;
import com.example.demo.service.mapper.DocumentServiceMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import({DocumentServiceImpl.class, DocumentServiceMapperImpl.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TemplateServiceImplIT {

    @Autowired
    private DocumentServiceImpl documentServiceImpl;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void createDocumentMustReturnOk() {
        Document expectedDocument = getDocument();

        Document result = documentServiceImpl.create(expectedDocument);

        Optional<DocumentEntity> searchOpt = documentRepository.findById(expectedDocument.getDocumentId());

        assertNotNull(result);
        assertEquals(expectedDocument.getDocumentId(),result.getDocumentId());
        assertTrue(searchOpt.isPresent());
        assertEquals(expectedDocument.getDocumentId(),searchOpt.get().getDocumentId());
    }

    @Test
    void createDocumentMustReturnError() {
        Document expectedDocument = getDocument();
        expectedDocument.setType(null);

        assertThrows(ConstraintViolationException.class, () -> {
                documentServiceImpl.create(expectedDocument);
                entityManager.flush();
                });
    }

    @Test
    void getByIdMustReturnOk() {
        Document expectedDocument = getDocument();

        documentServiceImpl.create(expectedDocument);

        Document result = documentServiceImpl.getById(expectedDocument.getDocumentId());

        assertNotNull(result);
        assertEquals(expectedDocument.getDocumentId(),result.getDocumentId());
        assertEquals(expectedDocument.getDocumentId(),result.getDocumentId());
    }

    @Test
    void getByIdMustReturnError() {
        final String documentId = "idNotFound";
        assertThrows(DocumentNotFoundException.class, () ->
                documentServiceImpl.getById(documentId));
    }

    @Test
    void getAllMustReturnOk() {
        Document expectedDocument1 = getDocument();
        Document expectedDocument2 = getDocument();

        documentServiceImpl.create(expectedDocument1);
        documentServiceImpl.create(expectedDocument2);

        List<Document> result = documentServiceImpl.getAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.stream().filter(item ->
                item.getDocumentId().equals(expectedDocument1.getDocumentId()) ||
                        item.getDocumentId().equals(expectedDocument2.getDocumentId())).count());
    }

    @Test
    void deleteByIdMustReturnOk() {
        Document expectedDocument = getDocument();

        documentServiceImpl.create(expectedDocument);

        documentServiceImpl.deleteById(expectedDocument.getDocumentId());

        Optional<DocumentEntity> searchOpt = documentRepository.findById(expectedDocument.getDocumentId());
        assertTrue(searchOpt.isEmpty());
    }

    @Test
    void deleteByIdMustReturnError() {
        final String documentId = "idNotFound";
        assertThrows(DocumentNotFoundException.class, () ->
                documentServiceImpl.deleteById(documentId));
    }

    private Document getDocument() {
        return Document.builder()
                .documentId(UUID.randomUUID().toString())
                .type("DEFAULT")
                .releaseDate(OffsetDateTime.now())
                .build();
    }
}