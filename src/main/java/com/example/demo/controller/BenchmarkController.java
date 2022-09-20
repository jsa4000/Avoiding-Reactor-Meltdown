package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BenchmarkController {
    private final Long SLEEP_TIME = 1000L;

    private final Scheduler DEFAULT_SCHEDULER = Schedulers.boundedElastic();

    @GetMapping("/benchmark/sequential/{count}")
    public ResponseEntity<List<Integer>> sequential(@PathVariable("count") Integer count) {
        return ResponseEntity.ok(numbers(count).stream()
                .map(this::firstProcess)
                .map(this::secondProcess)
                .collect(Collectors.toList()));
    }

    @GetMapping("/benchmark/parallel/{count}")
    public ResponseEntity<List<Integer>> parallel(@PathVariable("count") Integer count) {
        return ResponseEntity.ok(numbers(count).stream().parallel()
                .map(this::firstProcess)
                .map(this::secondProcess)
                .collect(Collectors.toList()));
    }

    @GetMapping("/benchmark/reactive/{count}")
    public ResponseEntity<List<Integer>> reactive(@PathVariable("count") Integer count) {
        return ResponseEntity.ok(Flux.fromIterable(numbers(count))
                .flatMap(this::firstProcessAsync)
                .flatMap(this::secondProcessAsync)
                .toStream().collect(Collectors.toList()));
    }

    private Mono<Integer> firstProcessAsync(Integer number) {
        return Mono.fromCallable(() -> this.firstProcess(number))
                .subscribeOn(DEFAULT_SCHEDULER);
    }

    private Mono<Integer> secondProcessAsync(Integer number) {
        return Mono.fromCallable(() -> this.secondProcess(number))
                .subscribeOn(DEFAULT_SCHEDULER);
    }

    private Integer firstProcess(Integer number) {
        sleep(SLEEP_TIME);
        return number;
    }

    private Integer secondProcess(Integer number) {
        sleep(SLEEP_TIME);
        return number;
    }

    private List<Integer> numbers(Integer count) {
        return IntStream.range(1,count).boxed().collect(Collectors.toList());
    }

    private void sleep(Long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
