package com.example.demo.documents;

import com.example.demo.documents.exceptions.DocumentManagerException;
import com.example.demo.documents.generator.DocumentGenerator;
import com.example.demo.documents.model.Template;
import com.example.demo.model.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DocumentManager {

    private final List<DocumentGenerator> generators;

    public Template generate(Document document) {
        DocumentGenerator generator = getGenerator(document);
        return generator.generate(document);
    }

    private DocumentGenerator getGenerator(Document document) {
        return generators.stream().filter(x -> x.getType().equals(document.getType()))
                .findFirst().orElseThrow(() -> new DocumentManagerException("Generator not found"));
    }

}
