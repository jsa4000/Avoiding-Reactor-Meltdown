package com.example.demo.benchmark;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

public class ReactorWorkflow {

    private final Scheduler DEFAULT_SCHEDULER = Schedulers.boundedElastic();

    private final String EMPTY_STRING = "";

    @Test
    public void monoNumberToStringTestMustReturnOk() {
        Integer expectedValue = 1;
        Mono<String> source = Mono.fromCallable(() -> getStrNumber(expectedValue))
                .log();
        StepVerifier.create(source)
                .expectNext(expectedValue.toString())
                .expectComplete()
                .verify();
    }

    @Test
    public void monoStringToNumberTestMustReturnOk() {
        String expectedValue = "1";
        Mono<Integer> source = Mono.fromCallable(() -> getIntNumber(expectedValue))
                .log();
        StepVerifier.create(source)
                .expectNext(Integer.parseInt(expectedValue))
                .expectComplete()
                .verify();
    }

    @Test
    public void monoWorkflowWithoutThenTestMustReturnOk() {
        Integer expectedValue = 1;
        Mono<Integer> source = Mono.fromCallable(() -> getStrNumber(expectedValue)) // Main publisher
                .flatMap(number -> Mono.fromCallable(() -> getIntNumber(number)))
                .log();
        StepVerifier.create(source)
                .expectNext(expectedValue)
                .expectComplete()
                .verify();
    }


    @Test
    public void monoWorkflowWithThenTestMustReturnOk() {
        Integer initialValue = 1;
        String expectedValue = "Final task";
        Mono<String> source = Mono.fromCallable(() -> getStrNumber(initialValue)) // Main publisher
                .flatMap(number -> Mono.fromCallable(() -> getIntNumber(number)))
                .then(Mono.just("Final task"))// After the completion Mono Type changes to String
                .log();
        StepVerifier.create(source)
                .expectNext(expectedValue)
                .expectComplete()
                .verify();
    }

    private String getStrNumber(Integer number) {
        return number != null ? number.toString() : EMPTY_STRING;
    }

    private Integer getIntNumber(String strNumber) {
        return strNumber != null ? Integer.parseInt(strNumber) : Integer.MIN_VALUE;
    }

}
