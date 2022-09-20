package com.example.demo.service.impl;

import com.example.demo.documents.model.Template;
import com.example.demo.model.Document;
import com.example.demo.service.DocumentManagerService;
import com.example.demo.service.ReactiveDocumentManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReactiveDocumentManagerServiceImpl implements ReactiveDocumentManagerService {

    private Scheduler DEFAULT_SCHEDULER = Schedulers.boundedElastic();

    private final DocumentManagerService documentManagerService;

    @Override
    public Mono<Template> generate(Document document) {
        return Mono.fromCallable(() -> documentManagerService.generate(document))
                .subscribeOn(DEFAULT_SCHEDULER);
    }
}
