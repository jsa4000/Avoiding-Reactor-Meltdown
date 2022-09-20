package com.example.demo.documents.generator;

import com.example.demo.documents.model.DraftTemplate;
import com.example.demo.documents.model.Template;
import com.example.demo.model.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DraftDocumentGenerator implements DocumentGenerator{

    public static final String DOCUMENT_TYPE = "DRAFT";

    public static final String DEFAULT_NOTE = "Default";

    @Override
    public String getType() {return DOCUMENT_TYPE;}

    @Override
    public Template generate(Document document) {
        return DraftTemplate.builder()
                .note(DEFAULT_NOTE)
                .documentId(document.getDocumentId())
                .releaseDate(OffsetDateTime.now())
                .build();
    }
}
