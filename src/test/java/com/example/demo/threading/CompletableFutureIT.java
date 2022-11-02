package com.example.demo.threading;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

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
    void supplierSyncTest() {
        CompletableFuture completableFuture = new CompletableFuture();

    }

    @Test
    void consumerSyncTest() {

    }

    @Test
    void functionSyncTest() {

    }
}
