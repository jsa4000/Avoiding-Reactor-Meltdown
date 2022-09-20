package com.example.demo.documents;

import com.example.demo.config.DocumentsConfiguration;
import com.example.demo.documents.exceptions.DocumentManagerException;
import com.example.demo.documents.generator.DefaultDocumentGenerator;
import com.example.demo.documents.generator.DraftDocumentGenerator;
import com.example.demo.documents.generator.SignedDocumentGenerator;
import com.example.demo.documents.model.DraftTemplate;
import com.example.demo.documents.model.Template;
import com.example.demo.model.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@Import({DocumentsConfiguration.class, DefaultDocumentGenerator.class, DefaultDocumentGenerator.class,
        DraftDocumentGenerator.class})
class DocumentManagerIT {

    @Autowired
    private DocumentManager documentManager;

    @Test
    void generateDefaultTemplateMustReturnOk() {
        Document document = getDocument(DefaultDocumentGenerator.DOCUMENT_TYPE);
        Template expectedTemplate = getTemplate(document);

        Template result = documentManager.generate(document);

        assertNotNull(result);
        assertEquals(expectedTemplate.getDocumentId(),result.getDocumentId());
    }

    @Test
    void generateDraftTemplateMustReturnOk() {
        Document document = getDocument(DraftDocumentGenerator.DOCUMENT_TYPE);
        Template expectedTemplate = getDraftTemplate(document);

        Template result = documentManager.generate(document);

        assertNotNull(result);
        assertEquals(expectedTemplate.getDocumentId(),result.getDocumentId());
    }

    @Test
    void generateSignedTemplateMustReturnError() {
        Document document = getDocument(SignedDocumentGenerator.DOCUMENT_TYPE);

        assertThrows(DocumentManagerException.class, () -> documentManager.generate(document));
    }

    private Template getDraftTemplate(Document document) {
        return DraftTemplate.builder()
                .documentId(document.getDocumentId())
                .releaseDate(OffsetDateTime.now())
                .build();
    }

    private Template getTemplate(Document document) {
        return Template.builder()
                .documentId(document.getDocumentId())
                .releaseDate(OffsetDateTime.now())
                .build();
    }

    private Document getDocument(String type) {
        return Document.builder()
                .documentId(UUID.randomUUID().toString())
                .type(type)
                .releaseDate(OffsetDateTime.now())
                .build();
    }
}