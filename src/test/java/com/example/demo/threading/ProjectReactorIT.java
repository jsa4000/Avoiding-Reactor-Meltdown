package com.example.demo.threading;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static com.example.demo.threading.ThreadingUtils.SLEEP_TIME;
import static com.example.demo.threading.ThreadingUtils.sleep;

public class ProjectReactorIT {

    /**
     *
     * Reactor Core is a Java 8 library that implements the reactive programming model.
     * It's built on top of the Reactive Streams specification, a standard for building
     * reactive applications.
     *
     * From the background of non-reactive Java development, going reactive can be quite
     * a steep learning curve. This becomes more challenging when comparing it to the
     * Java 8 Stream API, as they could be mistaken for being the same high-level abstractions.
     *
     */

    @Test
    void simpleSequentialTest() {
        // Perform the first operation
        String firstStep = ThreadingUtils.getFirstStep();
        // Perform the second operation
        String secondStep = ThreadingUtils.getSecondStep();
        // Perform the third Operation
        ThreadingUtils.doTask();
        // Print the result
        System.out.printf("%s - %s%n", firstStep,secondStep);
    }

    @Test
    void simpleReactiveAsyncTest() {
        // Compose the Reactive Flow
        Mono.fromCallable(ThreadingUtils::getFirstStep)
                .map(str -> ThreadingUtils.getSecondStep())
                .doFinally(str -> ThreadingUtils.doTask())
                .subscribe(System.out::println);
        // Sleep until the thread stops
        sleep(SLEEP_TIME);
    }

    @Test
    void simpleReactiveSyncTest() {
        // Compose the Reactive Flow
        Mono<String> result = Mono.fromCallable(ThreadingUtils::getFirstStep)
                .map(str -> ThreadingUtils.getSecondStep())
                .doFinally(str -> ThreadingUtils.doTask());
        // Similar to subscribe but blocks the main thread
        System.out.println(Objects.requireNonNull(result.block()));
    }

    @Test
    void simpleReactiveNonBlockingSyncTest() throws ExecutionException, InterruptedException {
        // Compose the Reactive Flow with NonBlocking tasks
        Mono<String> result = Mono.just("example")
                .flatMap(str -> ThreadingUtils.getFirstStepAsync())
                .flatMap(str -> ThreadingUtils.getSecondStepAsync())
                .doFinally(str -> ThreadingUtils.doTask());
        // Similar to subscribe but blocks the main thread
        System.out.println(result.toFuture().get());
        // Sleep until the thread stops
        sleep(SLEEP_TIME);
    }
}
