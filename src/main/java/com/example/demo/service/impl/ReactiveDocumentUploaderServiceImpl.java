package com.example.demo.service.impl;

import com.example.demo.documents.model.Template;
import com.example.demo.service.DocumentUploaderService;
import com.example.demo.service.ReactiveDocumentUploaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReactiveDocumentUploaderServiceImpl implements ReactiveDocumentUploaderService {

    private Scheduler DEFAULT_SCHEDULER = Schedulers.boundedElastic();

    private final DocumentUploaderService documentUploaderService;

    @Override
    public Mono<Boolean> upload(Template template) {
        return Mono.fromCallable(() -> documentUploaderService.upload(template))
                .subscribeOn(DEFAULT_SCHEDULER);
    }
}
