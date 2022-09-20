package com.example.demo.service;

import com.example.demo.documents.model.Template;
import com.example.demo.model.Document;
import reactor.core.publisher.Mono;

public interface ReactiveDocumentManagerService {

    Mono<Template> generate(Document document);

}
