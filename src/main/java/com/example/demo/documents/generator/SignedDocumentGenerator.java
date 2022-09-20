package com.example.demo.documents.generator;

import com.example.demo.documents.model.SignedTemplate;
import com.example.demo.documents.model.Template;
import com.example.demo.model.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignedDocumentGenerator implements DocumentGenerator{

    public static final String DOCUMENT_TYPE = "SIGNED";

    @Override
    public String getType() {return DOCUMENT_TYPE;}

    @Override
    public Template generate(Document document) {
        return SignedTemplate.builder()
                .signedBy(UUID.randomUUID().toString())
                .signedDate(OffsetDateTime.now())
                .documentId(document.getDocumentId())
                .releaseDate(OffsetDateTime.now())
                .build();
    }
}
