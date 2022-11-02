package com.example.demo.threading;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@ExtendWith(SpringExtension.class)
public class ExecutorServiceIT {

    public static final long SLEEP_TIME = 1000L;
    public static final int N_THREADS = 4;

    /**
     * ExecutorService is a JDK API that simplifies running tasks in asynchronous mode.
     * Generally speaking, ExecutorService automatically provides a pool of threads and
     * an API for assigning tasks to it.
     *
     * ExecutorService can be used with Sync and Async operations. In this case you can delegate
     * the execution to another thread separately and keep with the execution. On the other hand,
     * ExecutorService can BLOCK the task and wait until completion to continue wiht the rest of
     * the process.
     */

    @Autowired
    ExecutorService executorService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        ExecutorService executorService() {
            // Create a global Executor to manage the Thread Pool
            return Executors.newFixedThreadPool(N_THREADS);
        }
    }

    @Test
    void runnableAsyncTest() throws Exception {
        // Create Lambda/Anonymous function that implements Runnable default method
        Runnable runnable = () -> doTask();
        // Submit the Task to the ExecutorService
        executorService.submit(runnable);
        // Shutdown the Executor so new tasks cannot be added.
        executorService.shutdown();
        // Wait until all the task ends. This will block the MainThread
        System.out.println("This is Main Thread");
        assertTimeoutPreemptively(getMaxTimeout(SLEEP_TIME ), () ->
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS));
    }

    @Test
    void runnableAsyncTestMustThrowException() throws Exception {
        // Create Lambda/Anonymous function that implements Runnable default method
        Runnable runnable = () -> doTask();
        // Create executorService with a SingleThreadExecutor
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        // Submit the Task to the ExecutorService
        executorService.submit(runnable);
        // DO NOT Shutdown the Executor so new tasks cannot be added.
        // executorService.shutdown();
        // Wait until all the task ends. This will block the MainThread
        // Since the ExecutorService has not been shutdown it will wait until the timeout expires
        System.out.println("This is Main Thread");
        assertThrows(AssertionFailedError.class, () ->
                assertTimeoutPreemptively(getMaxTimeout(SLEEP_TIME ), () ->
                        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)));
    }

    private void doTask() {
        System.out.println("doTask Start");
        sleep(SLEEP_TIME);
        System.out.println("doTask Stop");
    }

    private Duration getMaxTimeout(Long millis) {
        return Duration.ofMillis(Double.valueOf(millis + (millis * 0.2)).longValue());
    }

    private void sleep(Long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
