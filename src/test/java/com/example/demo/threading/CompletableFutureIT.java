package com.example.demo.threading;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.example.demo.threading.ThreadingUtils.SLEEP_TIME;
import static com.example.demo.threading.ThreadingUtils.sleep;

public class CompletableFutureIT {

    /**
     *
     * This tutorial is a guide to the functionality and use cases of the CompletableFuture
     * class that was introduced as a Java 8 Concurrency API improvement.
     *
     * The Future interface was added in Java 5 to serve as a result of an asynchronous computation,
     * but it did not have any methods to combine these computations or handle possible errors.
     *
     * Java 8 introduced the CompletableFuture class. Along with the Future interface, it also
     * implemented the CompletionStage interface. This interface defines the contract for an
     * asynchronous computation step that we can combine with other steps.
     *
     * Common Interfaces:
     *
     *  - Supplier -> -> Output arguments (Kind of Callable)
     *  - Consumer -> -> Input arguments
     *  - Function -> Input and Output arguments
     *  - Runnable -> No Input or Output arguments
     *
     */

    @Test
    void simpleCompletableFutureWithSupplierSyncTest() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = CompletableFuture
                .supplyAsync(ThreadingUtils::getFirstStep);
        // Since previous method creates a new thread (async), it is necessary to wait until
        // the thread ends so the task finally ends.
        System.out.println("This is Main Thread");
        System.out.println(completableFuture.get());
    }

    @Test
    void simpleCompletableFutureWithSupplierAsyncTest() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = CompletableFuture
                .supplyAsync(ThreadingUtils::getFirstStep);
        // Since previous method creates a new thread (async), it is necessary to wait until
        // the thread ends so the task finally ends.
        System.out.println("This is Main Thread");
        sleep(SLEEP_TIME);
    }

    @Test
    void chainCompletableFutureWithSupplierSyncTest() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> completableFuture = CompletableFuture
                .supplyAsync(ThreadingUtils::getFirstStep)
                .thenApply(step -> ThreadingUtils.getSecondStep())
                .thenRun(ThreadingUtils::doTask);
        // Since previous method creates a new thread (async), it is necessary to wait until
        // the thread ends so the task finally ends.
        System.out.println("This is Main Thread");
        completableFuture.get();
    }

    @Test
    void combineCompletableFutureWithSupplierSyncTest() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = CompletableFuture
                .supplyAsync(ThreadingUtils::getFirstStep)
                .thenCombine(CompletableFuture.supplyAsync(ThreadingUtils::getSecondStep),
                        (x, y) -> String.format("%s + %s", x, y));
        // Since previous method creates a new thread (async), it is necessary to wait until
        // the thread ends so the task finally ends.
        System.out.println("This is Main Thread");
        System.out.println(completableFuture.get());
    }

    @Test
    void parallelCompletableFutureWithSupplierSyncTest() throws ExecutionException, InterruptedException {
        CompletableFuture<String> firstStep = CompletableFuture.supplyAsync(ThreadingUtils::getFirstStep);
        CompletableFuture<String> secondStep = CompletableFuture.supplyAsync(ThreadingUtils::getSecondStep);
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(firstStep,secondStep);
        // Since previous method creates a new thread (async), it is necessary to wait until
        // the thread ends so the task finally ends.
        System.out.println("This is Main Thread");
        combinedFuture.get();
        // Print the future after waiting for futures to finish
        System.out.println(firstStep.get());
        System.out.println(secondStep.get());
    }

    @Test
    void handleCompletableFutureWithSupplierSyncTest() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> completableFuture = CompletableFuture
                .supplyAsync(ThreadingUtils::getFirstStep)
                // Force an exceptions within the chain
                .thenApply(step -> new RuntimeException("Thrown Error in CompletableFunction"))
                .handle((ex, result) -> System.out.printf("%s - %s%n", ex.getMessage(), result))
                // Finally execute the run task
                .thenRun(ThreadingUtils::doTask);
        // Since previous method creates a new thread (async), it is necessary to wait until
        // the thread ends so the task finally ends.
        System.out.println("This is Main Thread");
        completableFuture.get();
    }

}
