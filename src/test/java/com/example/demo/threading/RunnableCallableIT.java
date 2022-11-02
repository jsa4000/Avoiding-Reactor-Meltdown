package com.example.demo.threading;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.example.demo.threading.ThreadingUtils.*;

public class RunnableCallableIT {

    public static final int N_THREADS = 4;

    /**
     *
     *  Since Java's early days, multithreading has been a major aspect of the language.
     *  Runnable is the core interface provided for representing multithreaded tasks, and
     *  Java 1.5 provided Callable as an improved version of Runnable.
     *
     *  Both interfaces are designed to represent a task that can be run by multiple threads.
     *  We can run Runnable tasks using the Thread class or ExecutorService, whereas we can only
     *  run Callables using the latter.
     *
     *  In a callable interface that basically throws a checked exception and returns some results.
     *  This is one of the major differences between the upcoming Runnable interface where no value
     *  is being returned. In this interface, it simply computes a result else throws an exception
     *  if unable to do so.
     */

    @Test
    void runnableSyncUsingFunctionalInterfaceTest() {
        // Implementing Runnable FunctionalInterface
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                firstStep();
            }
        };
        // Invoke the run method from Runnable interface
        runnable.run();
    }

    @Test
    void runnableSyncWithLambdaTest() {
        // Create Lambda/Anonymous function that implements Runnable default method
        Runnable runnable = () -> firstStep();
        // Invoke the run method from Runnable interface
        runnable.run();
    }

    @Test
    void runnableAsyncTest() {
        // Create Lambda/Anonymous function that implements Runnable default method
        Runnable runnable = () -> firstStep();
        // Create a Thread from Runnable
        Thread thread = new Thread(runnable);
        // Use start method to start a new thread. This will no block the main thread.
        thread.start();
        // Since previous method creates a new thread (async), it is necessary to wait until
        // the thread ends so the task finally ends.
        System.out.println("This is Main Thread");
        sleep(SLEEP_TIME);
    }

    @Test
    void runnableAsyncAndWaitTest() throws InterruptedException {
        // Create Lambda/Anonymous function that implements Runnable default method
        Runnable runnable = () -> firstStep();
        // Create a Thread from Runnable
        Thread thread = new Thread(runnable);
        // Use start method to start a new thread
        thread.start();
        // Wait until the task end. This will block the main thread.
        System.out.println("This is Main Thread");
        thread.join(Long.MAX_VALUE, TimeUnit.NANOSECONDS.ordinal());
    }

    @Test
    void runnableAsyncWithExecutorServiceTest() throws Exception {
        // Create Lambda/Anonymous function that implements Runnable default method
        Runnable runnable = () -> firstStep();
        // Create executorService with a SingleThreadExecutor
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        // Submit the Task to the ExecutorService
        executorService.submit(runnable);
        // Shutdown the Executor so new tasks cannot be added.
        executorService.shutdown();
        // Wait until all the task ends. This will block the MainThread
        System.out.println("This is Main Thread");
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

    }

    @Test
    void runnableMultipleAsyncWithExecutorServiceTest() throws Exception {
        // Create Lambda/Anonymous function that implements Runnable default method
        List<Runnable> tasks = List.of(ThreadingUtils::firstStep, ThreadingUtils::secondStep);
        // Create executorService with a pool of threads
        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        // For each task submit to the ExecutorService
        tasks.forEach(executorService::submit);
        // Shutdown the Executor so new tasks cannot be added.
        executorService.shutdown();
        // Wait until all the task ends. This will block the MainThread
        System.out.println("This is Main Thread");
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    @Test
    void callableSyncUsingFunctionalInterfaceTest() throws Exception {
        // Implementing Callable FunctionalInterface
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getFirstStep();
            }
        };
        // Invoke the run method from Callable interface
        System.out.println(callable.call());
    }

    @Test
    void callableSyncWithLambdaTest() throws Exception {
        // Create Lambda/Anonymous function that implements Callable default method
        Callable<String> callable = () -> getFirstStep();
        // Invoke the run method from Callable interface
        System.out.println(callable.call());
    }

    @Test
    void callableAsyncWithLambdaTest() throws Exception {
        // Create Lambda/Anonymous function that implements Callable default method
        Callable<String> callable = () -> getFirstStep();
        // Callable function cannot be used directly using Thread class.
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        // Submit a task to the executor Service that return a future with the result
        Future<String> future = executorService.submit(callable);
        // Shutdown the Executor so new tasks cannot be added.
        executorService.shutdown();
        // Wait until all the task ends. This will block the MainThread
        System.out.println("This is Main Thread");
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        System.out.println(future);
    }

    @Test
    void callableMultipleAsyncWithLambdaTest() throws Exception {
        // Create Lambda/Anonymous function that implements Callable default method
        List<Callable<String>> tasks = List.of(ThreadingUtils::getFirstStep, ThreadingUtils::getSecondStep);
        // Callable function cannot be used directly using Thread class.
        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        // Submit a task to the executor Service that return a future with the result
        List<Future<String>> futures = tasks.stream()
                .map(task -> executorService.submit(task))
                .collect(Collectors.toList());
        // Shutdown the Executor so new tasks cannot be added.
        executorService.shutdown();
        // Wait until all the task ends. This will block the MainThread
        System.out.println("This is Main Thread");
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        futures.forEach(System.out::println);
    }

    @Test
    void callableMultipleSyncWithLambdaTest() throws Exception {
        // Create Lambda/Anonymous function that implements Callable default method
        List<Callable<String>> tasks = List.of(ThreadingUtils::getFirstStep, ThreadingUtils::getSecondStep);
        // Callable function cannot be used directly using Thread class.
        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        // Submit a task to the executor Service that return a future with the result. Blocking
        List<Future<String>> futures = executorService.invokeAll(tasks,Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        // Shutdown the Executor so new tasks cannot be added.
        executorService.shutdown();
        // Wait until all the task ends. This will block the MainThread
        System.out.println("This is Main Thread");
        futures.forEach(System.out::println);
    }


}
