package com.example.demo.documents.generator;

import com.example.demo.documents.model.Template;
import com.example.demo.model.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultDocumentGenerator implements DocumentGenerator {

    public static final String DOCUMENT_TYPE = "DEFAULT";

    @Override
    public String getType() {return DOCUMENT_TYPE;}

    @Override
    public Template generate(Document document) {
        return Template.builder()
                .documentId(document.getDocumentId())
                .releaseDate(OffsetDateTime.now())
                .build();
    }
}
