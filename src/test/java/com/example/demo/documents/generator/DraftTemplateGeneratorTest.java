package com.example.demo.documents.generator;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@Import(DraftDocumentGenerator.class)
class DraftTemplateGeneratorTest {

    @Autowired
    private DraftDocumentGenerator generator;

    @Test
    void getTypeMustReturnOk() {
        final String expectedType = DraftDocumentGenerator.DOCUMENT_TYPE;

        String result = generator.getType();

        assertNotNull(result);
        assertEquals(expectedType,result);
    }

    @Test
    void generate() {
        final Document document = getDocument();
        final Template expectedTemplate = getTemplate(document);

        Template result = generator.generate(document);

        assertNotNull(result);
        assertEquals(expectedTemplate.getDocumentId(),result.getDocumentId());
    }

    private Template getTemplate(Document document) {
        return DraftTemplate.builder()
                .documentId(document.getDocumentId())
                .releaseDate(OffsetDateTime.now())
                .build();
    }

    private Document getDocument() {
        return Document.builder()
                .documentId(UUID.randomUUID().toString())
                .type("DRAFT")
                .releaseDate(OffsetDateTime.now())
                .build();
    }

}