package com.example.demo.benchmark;


import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorHandlingBT {

    private final Scheduler DEFAULT_SCHEDULER = Schedulers.boundedElastic();

    private final String EMPTY_STRING = "";

    @Test
    public void monoTestMustReturnOk() {
        Integer expectedValue = 1;
        Mono<String> source = Mono.fromCallable(() -> getStrNumber(expectedValue))
                .log();
        StepVerifier.create(source)
                .expectNext(expectedValue.toString())
                .expectComplete()
                .verify();
    }

    @Test
    public void monoTestWithSchedulerMustReturnOk() {
        Integer expectedValue = 1;
        Mono<String> source = Mono.fromCallable(() -> getStrNumber(expectedValue))
                .subscribeOn(DEFAULT_SCHEDULER)
                .log();
        StepVerifier.create(source)
                .expectNext(expectedValue.toString())
                .expectComplete()
                .verify();
    }

    @Test
    public void fluxTestMustReturnOk() {
        List<Integer> expectedValues = List.of(1,2);
        Flux<String> source = Flux.fromIterable(expectedValues)
                .flatMap(number -> Mono.just(getStrNumber(number)))
                .log();
        StepVerifier.create(source)
                .expectNextSequence(expectedValues.stream().map(Object::toString).collect(Collectors.toList()))
                .expectComplete()
                .verify();
    }

    @Test
    public void fluxTestMustReturnException() {
        List<Integer> expectedValues = List.of(1,2);
        Flux<String> source = Flux.fromIterable(expectedValues)
                .concatWith(Mono.error(new IllegalArgumentException()))
                .flatMap(number -> Mono.just(getStrNumber(number)))
                .log();
        StepVerifier.create(source)
                .expectNextCount(expectedValues.size())
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    public void fluxTestWithSchedulerMustReturnOk() {
        List<Integer> expectedValues = List.of(1,2);
        Flux<String> source = Flux.fromIterable(expectedValues)
                .subscribeOn(DEFAULT_SCHEDULER)
                .flatMap(number -> Mono.just(getStrNumber(number)))
                .log();
        StepVerifier.create(source)
                .expectNextSequence(expectedValues.stream().map(Object::toString).collect(Collectors.toList()))
                .expectComplete()
                .verify();
    }

    @Test
    public void fluxTestWithSchedulerMustReturnException() {
        List<Integer> expectedValues = List.of(1,2);
        Flux<String> source = Flux.fromIterable(expectedValues)
                .concatWith(Mono.error(new IllegalArgumentException()))
                .subscribeOn(DEFAULT_SCHEDULER)
                .flatMap(number -> Mono.just(getStrNumber(number)))
                .log();
        StepVerifier.create(source)
                .expectNextCount(expectedValues.size())
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    public void fluxStreamTestMustReturnOk() {
        List<Integer> expectedValues = List.of(1,2);
        List<String> source = Flux.fromIterable(expectedValues)
                .flatMap(number -> Mono.just(getStrNumber(number)))
                .log()
                .toStream().collect(Collectors.toList());
        assertNotNull(source);
        assertEquals(expectedValues.size(),source.size());
    }

    @Test
    public void fluxStreamTestWithSchedulerMustReturnOk() {
        List<Integer> expectedValues = List.of(1,2);
        List<String> source = Flux.fromIterable(expectedValues)
                .concatWith((Flux.fromIterable(List.of(3))))
                .subscribeOn(DEFAULT_SCHEDULER)
                .flatMap(number -> Mono.just(getStrNumber(number)))
                .log()
                .toStream().collect(Collectors.toList());
        assertNotNull(source);
        assertEquals(expectedValues.size() + 1,source.size());
    }

    @Test
    public void fluxStreamTestWithSchedulerMustReturnException() {
        List<Integer> expectedValues = List.of(1,2);
        Flux<String> source = Flux.fromIterable(expectedValues)
                .concatWith(Mono.error(new IllegalArgumentException()))
                .concatWith(Flux.fromIterable(List.of(3)))
                .subscribeOn(DEFAULT_SCHEDULER)
                .flatMap(number -> Mono.just(getStrNumber(number)))
                //.onErrorStop() // By default stops if an exception is thrown
                .log();
        assertThrows(IllegalArgumentException.class,() -> source.toStream().collect(Collectors.toList()));
    }

    @Test
    public void fluxStreamTestWithSchedulerWithRetryMustReturnException() {
        List<Integer> expectedValues = List.of(1,2);
        Flux<String> source = Flux.fromIterable(expectedValues)
                .concatWith(Mono.error(new IllegalArgumentException()))
                .concatWith(Flux.fromIterable(List.of(3)))
                .subscribeOn(DEFAULT_SCHEDULER)
                .flatMap(number -> Mono.just(getStrNumber(number)))
                //.onErrorStop() // By default stops if an exception is thrown
                .retry(3) // It retries the whole flux again not the failed one. Do it in separate Mono
                .log();
        assertThrows(IllegalArgumentException.class,() -> source.toStream().collect(Collectors.toList()));
    }

    @Test
    public void fluxStreamTestWithSchedulerWithErrorMapMustReturnOk() {
        List<Integer> expectedValues = List.of(1,2);
        Flux<String> source = Flux.fromIterable(expectedValues)
                .concatWith(Mono.error(new IllegalArgumentException()))
                .concatWith(Flux.fromIterable(List.of(3)))
                .subscribeOn(DEFAULT_SCHEDULER)
                .flatMap(number -> Mono.just(getStrNumber(number)))
                .onErrorMap(ex -> new IllegalArgumentException("Exception With message"))
                .log();
        assertThrows(IllegalArgumentException.class,() -> source.toStream().collect(Collectors.toList()));
    }

    @Test
    // https://nurkiewicz.com/2021/08/onerrorcontinue-reactor.html
    public void fluxStreamTestWithSchedulerWithErrorReturnMustReturnOk() {
        List<Integer> expectedValues = List.of(1,2);
        List<String> source = Flux.fromIterable(expectedValues)
                .concatWith(Mono.error(new IllegalArgumentException()))
                .concatWith(Flux.fromIterable(List.of(3)))
                .subscribeOn(DEFAULT_SCHEDULER)
                .flatMap(number -> Mono.just(getStrNumber(number)))
                .doOnError(IllegalArgumentException.class, ex -> System.out.println("Error: IllegalArgumentException"))
                .onErrorReturn(IllegalArgumentException.class, "4")
                .log()
                .toStream().collect(Collectors.toList());
        assertNotNull(source);
        assertEquals(expectedValues.size() + 1,source.size());
    }

    @Test
    public void fluxStreamTestWithSchedulerWithErrorResumeMustReturnOk() {
        List<Integer> expectedValues = List.of(1,2);
        List<String> source = Flux.fromIterable(expectedValues)
                .concatWith(Mono.error(new IllegalArgumentException()))
                .concatWith(Flux.fromIterable(List.of(3)))
                .subscribeOn(DEFAULT_SCHEDULER)
                .flatMap(number -> Mono.just(getStrNumber(number)))
                .doOnError(IllegalArgumentException.class, ex -> System.out.println("Error: IllegalArgumentException"))
                .onErrorResume(IllegalArgumentException.class, ex -> Mono.just("4"))
                .log()
                .toStream().collect(Collectors.toList());
        assertNotNull(source);
        assertEquals(expectedValues.size() + 1,source.size());
    }

    private String getStrNumber(Integer number) {
        return number != null ? number.toString() : EMPTY_STRING;
    }

}
