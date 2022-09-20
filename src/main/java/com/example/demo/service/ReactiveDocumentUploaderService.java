package com.example.demo.service;

import com.example.demo.documents.model.Template;
import reactor.core.publisher.Mono;

public interface ReactiveDocumentUploaderService {

    Mono<Boolean> upload(Template template);

}
