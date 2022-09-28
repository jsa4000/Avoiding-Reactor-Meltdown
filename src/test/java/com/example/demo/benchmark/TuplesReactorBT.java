package com.example.demo.benchmark;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TuplesReactorBT {

    private final Scheduler DEFAULT_SCHEDULER = Schedulers.boundedElastic();

    @Test
    public void tuplesWithMonoAndFlatMapMustReturnOk() {
        final String expectedFirstValue = "firstValue";
        final String expectedSecondValue = "secondValue";
        Mono<Tuple2<String,String>> source = Mono.just("Hello")
                        .flatMap(s -> Mono.just(Tuples.of(expectedFirstValue,expectedSecondValue)));

        StepVerifier.create(source)
                .expectNext(Tuples.of(expectedFirstValue,expectedSecondValue))
                .verifyComplete();
    }

    @Test
    public void tuplesWithMonoAndMapMustReturnOk() {
        final String expectedFirstValue = "firstValue";
        final String expectedSecondValue = "secondValue";
        Mono<Tuple2<String,String>> source = Mono.just("Hello")
                .map(s -> Tuples.of(expectedFirstValue,expectedSecondValue));

        StepVerifier.create(source)
                .expectNext(Tuples.of(expectedFirstValue,expectedSecondValue))
                .verifyComplete();
    }

    @Test
    public void tuplesWithContextWithFluxMustReturnOk() {
        List<String> items = List.of("1","2","3");
        Flux<Tuple2<String,String>> source = Flux.fromIterable(items)
                .flatMap(item -> step1(item, item))
                .flatMap(item -> step2(item.getT1(), item.getT1()))
                .flatMap(item -> step3(item.getT1(), item.getT1()))
                .subscribeOn(DEFAULT_SCHEDULER);

        List<Tuple2<String,String>> result = source.toStream().collect(Collectors.toList());
        assertEquals(items.size(), result.size());
    }

    private Mono<Tuple2<String,String>> step1(String context, String item) {
        return Mono.just("First Step for")
                .map(x -> Tuples.of(context, String.format("%s %s", x, item)))
                .subscribeOn(DEFAULT_SCHEDULER);
    }

    private Mono<Tuple2<String,String>> step2(String context, String item) {
        return Mono.just("Second Step for")
                .map(x -> Tuples.of(context, String.format("%s %s", x, item)))
                .subscribeOn(DEFAULT_SCHEDULER);
    }

    private Mono<Tuple2<String,String>> step3(String context, String item) {
        return Mono.just("Third Step for")
                .map(x -> Tuples.of(context, String.format("%s %s", x, item)))
                .subscribeOn(DEFAULT_SCHEDULER);
    }

}
