package com.example.demo.service.impl;

import com.example.demo.model.Document;
import com.example.demo.service.DocumentService;
import com.example.demo.service.ReactiveDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReactiveDocumentServiceImpl implements ReactiveDocumentService {

    private Scheduler DEFAULT_SCHEDULER = Schedulers.boundedElastic();

    private final DocumentService documentService;


    @Override
    public Mono<Document> create(Document document) {
        return Mono.fromCallable(() -> documentService.create(document))
                .subscribeOn(DEFAULT_SCHEDULER);
    }

    @Override
    public Mono<Document> getById(String documentId) {
        return Mono.fromCallable(() -> documentService.getById(documentId))
                .subscribeOn(DEFAULT_SCHEDULER);
    }

    @Override
    public Mono<Object> deleteById(String documentId) {
        return Mono.fromRunnable(() -> documentService.deleteById(documentId))
                .subscribeOn(DEFAULT_SCHEDULER);
    }

    @Override
    public Flux<Document> getAll() {
        return Flux.fromIterable(documentService.getAll())
                .subscribeOn(DEFAULT_SCHEDULER);
    }
}
