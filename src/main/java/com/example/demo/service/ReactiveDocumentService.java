package com.example.demo.service;

import com.example.demo.model.Document;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveDocumentService {

    Mono<Document> create(Document document);

    Mono<Document> getById(String documentId);

    Mono<Object> deleteById(String documentId);

    Flux<Document> getAll();

}
