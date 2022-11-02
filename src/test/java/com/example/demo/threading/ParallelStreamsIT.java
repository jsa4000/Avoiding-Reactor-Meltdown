package com.example.demo.threading;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelStreamsIT {

    /**
     * Java 8 introduced the Stream API that makes it easy to iterate over collections
     * as streams of data. It's also very easy to create streams that execute in parallel
     * and make use of multiple processor cores.
     *
     * Parallel streams make use of the fork-join framework and its common pool of worker
     * threads. The fork-join framework was added to java.util.concurrent in Java 7 to
     * handle task management between multiple threads.
     *
     * Java 7 introduced the fork/join framework. It provides tools to help speed up parallel
     * processing by attempting to use all available processor cores. It accomplishes this
     * through a divide and conquer approach.
     *
     * In practice, this means that the framework first “forks,” recursively breaking the
     * task into smaller independent subtasks until they are simple enough to run asynchronously.
     *
     * After that, the “join” part begins. The results of all subtasks are recursively joined
     * into a single result. In the case of a task that returns void, the program simply waits
     * until every subtask runs.
     *
     * To provide effective parallel execution, the fork/join framework uses a pool of threads
     * called the ForkJoinPool. This pool manages worker threads of type ForkJoinWorkerThread.
     *
     * Configuration:
     *
     *    java.util.concurrent.ForkJoinPool.common.parallelism=4
     */

    @Test
    void simpleSequentialStreamTest() {
        // Creates a list of integer with an operation
        List<Integer> numbers =  IntStream.range(0,10)
                .map(ThreadingUtils::multiply)
                .map(ThreadingUtils::printThread)
                .boxed()
                .collect(Collectors.toList());
        // Print the number computed
        System.out.println(numbers);
    }

    @Test
    void simpleParallelStreamTest() {
        // Creates a list of integer with an operation
        List<Integer> numbers =  IntStream.range(0,10)
                .parallel()
                .map(ThreadingUtils::multiply)
                .map(ThreadingUtils::printThread)
                .boxed()
                .collect(Collectors.toList());
        // Print the number computed
        System.out.println(numbers);
    }

    @Test
    void customForkJoinParallelStreamTest() throws ExecutionException, InterruptedException {
        // Create custom ForkJoinPool, two items in parallel.
        ForkJoinPool customThreadPool = new ForkJoinPool(2);
        Future<List<Integer>> result = customThreadPool.submit(() ->
                IntStream.range(0,10)
                        .parallel()
                        .map(ThreadingUtils::multiply)
                        .map(ThreadingUtils::printThread)
                        .boxed()
                        .collect(Collectors.toList()));
        // Shutdown the ForkJoinPool
        customThreadPool.shutdown();
        // Print the result
        System.out.println(result.get());
    }

    @Test
    void reduceForkJoinParallelStreamTest() throws ExecutionException, InterruptedException {
        // Creates a list of integer
        List<Integer> numbers = IntStream.range(0,10).boxed().collect(Collectors.toList());
        // Create custom ForkJoinPool
        ForkJoinPool customThreadPool = new ForkJoinPool(4);
        Future<Integer> result = customThreadPool.submit(() ->
                numbers.parallelStream().reduce(0, Integer::sum));
        // Shutdown the ForkJoinPool
        customThreadPool.shutdown();
        // Print the result
        System.out.println(result.get());
    }

}
