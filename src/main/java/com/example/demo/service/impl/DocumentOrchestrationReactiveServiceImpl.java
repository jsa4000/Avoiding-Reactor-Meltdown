package com.example.demo.service.impl;

import com.example.demo.model.Document;
import com.example.demo.service.DocumentOrchestrationService;
import com.example.demo.service.ReactiveDocumentManagerService;
import com.example.demo.service.ReactiveDocumentService;
import com.example.demo.service.ReactiveDocumentUploaderService;
import com.example.demo.service.exception.DocumentNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.Profiles.REACTIVE;

@Slf4j
@Service
@Profile(REACTIVE)
@RequiredArgsConstructor
public class DocumentOrchestrationReactiveServiceImpl implements DocumentOrchestrationService {

    private final ReactiveDocumentService documentService;

    private final ReactiveDocumentUploaderService documentUploaderService;

    private final ReactiveDocumentManagerService documentManagerService;

    @Override
    public Document create(Document document) {
        return createAsync(document).block();
    }

    private Mono<Document> createAsync(Document document) {
        return documentManagerService.generate(document)
                .flatMap(documentUploaderService::upload)
                .then(documentService.create(document));
    }

    @Override
    public List<Document> create(List<Document> documents) {
        return Flux.fromIterable(documents)
                .flatMap(this::createAsync)
                .toStream().collect(Collectors.toList());
    }

    @Override
    public Document getById(String documentId) throws DocumentNotFoundException {
        return documentService.getById(documentId).block();
    }

    @Override
    public void deleteById(String documentId) throws DocumentNotFoundException {
        documentService.deleteById(documentId);
    }

    @Override
    public List<Document> getAll() {
        return documentService.getAll().toStream().collect(Collectors.toList());
    }
}
