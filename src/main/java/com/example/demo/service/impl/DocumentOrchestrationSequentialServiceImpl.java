package com.example.demo.service.impl;

import com.example.demo.model.Document;
import com.example.demo.service.DocumentManagerService;
import com.example.demo.service.DocumentOrchestrationService;
import com.example.demo.service.DocumentService;
import com.example.demo.service.DocumentUploaderService;
import com.example.demo.service.exception.DocumentNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.Profiles.SEQUENTIAL;

@Slf4j
@Service
@Profile(SEQUENTIAL)
@RequiredArgsConstructor
public class DocumentOrchestrationSequentialServiceImpl implements DocumentOrchestrationService {

    private final DocumentService documentService;

    private final DocumentUploaderService documentUploaderService;

    private final DocumentManagerService documentManagerService;

    @Override
    public Document create(Document document) {
        documentUploaderService.upload(documentManagerService.generate(document));
        return documentService.create(document);
    }

    @Override
    public List<Document> create(List<Document> documents) {
        return documents.stream()
                .map(this::create)
                .collect(Collectors.toList());
    }

    @Override
    public Document getById(String documentId) throws DocumentNotFoundException {
        return documentService.getById(documentId);
    }

    @Override
    public void deleteById(String documentId) throws DocumentNotFoundException {
        documentService.deleteById(documentId);
    }

    @Override
    public List<Document> getAll() {
        return documentService.getAll();
    }
}
