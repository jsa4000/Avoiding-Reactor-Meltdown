package com.example.demo.benchmark;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.LongStream;

import static com.example.demo.utils.Time.timed;

/**
 *  Set CPU limits used by JVM: -XX:ActiveProcessorCount=2
 *
 */
@SpringBootTest
public class ProjectReactorBT {

    @Test
    public void mono_simple_test() {
        Mono<String> mono = Mono.just("This is a test").log();
        mono.subscribe(System.out::println);
    }

    @Test
    public void mono_simple_timed_test() {
        Mono<String> mono = Mono.just("This is a test").log();
        timed(() -> mono.subscribe(System.out::println));
    }

    @Test
    public void mono_exception_test() {
        Mono<?> mono = Mono.just("This is a test")
                .then(Mono.error(new Exception("This is an exception")))
                .log();
        mono.subscribe(System.out::println, Throwable::printStackTrace);
    }

    @Test
    public void flux_simple_test() {
        Flux<String> flux = Flux.just("First","Second","Third").log();
        flux.subscribe(System.out::println);
    }

    @Test
    public void flux_iterable_test() {
        List<String> list = List.of("First","Second","Third");
        Flux<String> flux = Flux.fromIterable(list).log();
        flux.subscribe(System.out::println);
    }

    @Test
    public void flux_flatmap_test() {
        List<String> list = List.of("First","Second","Third");
        Flux<?> flux = Flux.fromIterable(list)
                .flatMap(str -> {
                    System.out.printf("Do something with %s%n", str);
                    return Mono.just(str);
                })
                .log();
        flux.subscribe(System.out::println);
    }

    @Test
    public void flux_exception_test() {
        List<String> list = List.of("First","Second","Third");
        Flux<?> flux = Flux.fromIterable(list)
                .flatMap(str -> {
                    System.out.printf("Do something with %s$n", str);
                    int index = (int)(list.size() / 2.0);
                    if (list.get(index).equals(str)) {
                        return Mono.error(new Exception(String.format("The element is equals to %s", str)));
                    }
                    return Mono.just(str);
                })
                .log();
        flux.subscribe(System.out::println, Throwable::printStackTrace);
    }

    @Test
    public void flux_non_parallel_test() {
        Map<String, Long> map = Map.of("First", 3000L,"Second", 2000L, "Third", 1000L);
        Flux<?> flux = Flux.fromIterable(map.keySet())
                .flatMap(key -> {
                    System.out.printf("Wait %d seconds with %s%n", map.get(key), key);
                    System.out.println("ReactorTests.test.flatmap " + Thread.currentThread());
                    return sleep(map.get(key));
                })
                .log();
        timed(() -> flux.subscribe(i -> {
            System.out.println(i);
            System.out.println("ReactorTests.test.subscribe " + Thread.currentThread());
        }));
    }

    @Test
    public void flux_runOn_parallel_with_sleep_test() {
        Map<String, Long> map = Map.of("First", 3000L,"Second", 2000L, "Third", 1000L);
        Flux<?> flux = Flux.fromIterable(map.keySet())
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(key -> {
                    System.out.printf("Wait %d seconds with %s%n", map.get(key), key);
                    System.out.println("ReactorTests.test.flatmap " + Thread.currentThread());
                    return sleep(map.get(key));
                })
                .sequential()
                .log();
        timed(() -> flux.subscribe(i -> {
            System.out.println(i);
            System.out.println("ReactorTests.test.subscribe " + Thread.currentThread());
        }));
        sleep(6000L);
    }

    @Test
    public void flux_publishOn_parallel_with_sleep_test() {
        Map<String, Long> map = Map.of("First", 3000L,"Second", 2000L, "Third", 1000L);
        Flux<?> flux = Flux.fromIterable(map.keySet())
                .publishOn(Schedulers.parallel())
                .map(key -> {
                    System.out.printf("Wait %d seconds with %s%n", map.get(key), key);
                    System.out.println("ReactorTests.test.flatmap " + Thread.currentThread());
                    return sleep(map.get(key));
                })
                .log();
        timed(() -> flux.subscribe(i -> {
            System.out.println(i);
            System.out.println("ReactorTests.test.subscribe " + Thread.currentThread());
        }));
        sleep(6000L);
    }

    @Test
    public void flux_publisherOn_parallel_with_sleep_test() {
        Map<String, Long> map = Map.of("First", 3000L,"Second", 2000L, "Third", 1000L);
        Flux<?> flux = Flux.fromIterable(map.keySet())
                .subscribeOn(Schedulers.parallel())
                .map(key -> {
                    System.out.printf("Wait %d seconds with %s%n", map.get(key), key);
                    System.out.println("ReactorTests.test.flatmap " + Thread.currentThread());
                    return sleep(map.get(key));
                })
                .log();
        timed(() -> flux.subscribe(i -> {
            System.out.println(i);
            System.out.println("ReactorTests.test.subscribe " + Thread.currentThread());
        }));
        sleep(6000L);
    }

    @Test
    public void flux_new_context_non_parallel_test() {
        Map<String, Long> map = Map.of("First", 3000L,"Second", 2000L, "Third", 1000L);
        Flux<?> flux = Flux.fromIterable(map.keySet())
                //.subscribeOn(Schedulers.boundedElastic())
                .flatMap(key -> {
                    System.out.printf("Wait %d seconds with %s%n", map.get(key), key);
                    System.out.println("ReactorTests.test.flatmap " + Thread.currentThread());
                    return sleepContext(map.get(key));
                })
                .log();
        timed(() -> flux.subscribe(i -> {
            System.out.println(i);
            System.out.println("ReactorTests.test.subscribe " + Thread.currentThread());
        }));
    }

    @Test
    public void flux_runOn_elastic_with_countdown_test() {
        Map<String, Long> map = Map.of("First", 3000L,"Second", 2000L, "Third", 1000L);
        CountDownLatch cdl = new CountDownLatch(map.keySet().size());
        ParallelFlux<?> flux = Flux.fromIterable(map.keySet())
                //.parallel() // It does not work
                .parallel(3)
                //.runOn(Schedulers.single())
                //.runOn(Schedulers.parallel())
                .runOn(Schedulers.boundedElastic()) // Suitalbe for blocking operations
                .flatMap(key -> {
                    System.out.printf("Wait %d seconds with %s%n", map.get(key), key);
                    System.out.println("ReactorTests.test.flatmap " + Thread.currentThread());
                    return sleep(map.get(key));
                })
                .doOnTerminate(cdl::countDown)
                .log();
        flux.subscribe(i -> {
            System.out.println(i);
            System.out.println("ReactorTests.test.subscribe " + Thread.currentThread());
        });
        timed(() -> await(cdl));
    }

    @Test
    @DisplayName("Block Reactive With Elastic Scheduler for I/O")
    public void flux_runOn_elastic_with_block_test() {
        Map<String, Long> map = Map.of("First", 3000L,"Second", 2000L, "Third", 1000L);
        Flux<?> flux = Flux.fromIterable(map.keySet())
                //.parallel() // It does not work
                .parallel()
                //.runOn(Schedulers.single())
                //.runOn(Schedulers.parallel())
                .runOn(Schedulers.boundedElastic()) // Suitalbe for blocking operations
                .flatMap(key -> {
                    System.out.printf("Wait %d seconds with %s%n", map.get(key), key);
                    System.out.println("ReactorTests.test.flatmap " + Thread.currentThread());
                    return sleep(map.get(key));
                })
                .sequential()
                .log();
        timed(flux::blockLast);
    }

    @Test
    @DisplayName("Block Reactive With Elastic Scheduler for I/O (Hi)")
    public void flux_runOn_elastic_with_block_hl_test() {
        Map<String, Long> map = Map.of(
                "First", 4096L, "Second", 2048L, "Third", 1024L,
                "Fourth", 512L, "Fifth", 256L, "Sixth", 128L,
                "Seventh", 64L, "Eight", 3L, "Ninth", 16L);
        Flux<?> flux = Flux.fromIterable(map.keySet())
                //.parallel() // It does not work
                .parallel()
                //.runOn(Schedulers.single())
                //.runOn(Schedulers.parallel())
                .runOn(Schedulers.boundedElastic()) // Suitable for blocking operations
                //.runOn(Schedulers.fromExecutor(executor()))
                .flatMap(key -> {
                    System.out.printf("Wait %d seconds with %s%n", map.get(key), key);
                    System.out.println("ReactorTests.test.flatmap " + Thread.currentThread());
                    return sleep(map.get(key));
                })
                .sequential()
                .log();
        timed(flux::blockLast);
    }

    @Test
    public void flux_flow_test() {
        List<String> list = List.of("First","Second","Third");
        Flux<?> flux = Flux.fromIterable(list)
                .flatMap(str -> {
                    System.out.printf("Running %s Flow%n", str);
                    return flow(str);
                })
                .log();
        timed(() -> flux.subscribe(System.out::println));
    }

    @Test
    public void flux_flow_runOn_elastic_with_block_test() {
        List<String> list = List.of("First","Second","Third");
        Flux<?> flux = Flux.fromIterable(list)
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(str -> {
                    System.out.printf("Running %s Flow%n", str);
                    System.out.println("ReactorTests.test.flatmap " + Thread.currentThread());
                    return flow(str);
                })
                .sequential()
                .log();
        timed(flux::blockLast);
    }

    @Test
    public void flux_flow_defer_elastic_test() {
        List<String> list = List.of("First","Second","Third");
        Flux<?> flux = Flux.fromIterable(list)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(str -> {
                    System.out.printf("Running %s Flow%n", str);
                    System.out.println("ReactorTests.test.flatmap " + Thread.currentThread());
                    return flow(str);
                })
                //.subscribeOn(Schedulers.boundedElastic())
                .log();
        timed(flux::blockLast);
    }

    private Mono<String> flow(String name) {
        return Mono.just(name)
                .then(step("First", name))
                .then(step("Second", name))
                .then(step("Third", name));
    }

    private Mono<String> step(String stepName, String flowName) {
        System.out.printf("- Running step %s:%s in thread %s%n", flowName, stepName, Thread.currentThread());
        return Mono.just(String.format("%s Step for %s", stepName, flowName));
    }

    private Mono<String> stepContext(String stepName, String flowName) {
        System.out.printf("- Running step %s:%s in thread %s%n", flowName, stepName, Thread.currentThread());
        return Mono.just(String.format("%s Step for %s", stepName, flowName))
                // moves to another thread or context (to propagt)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<Long> count(Long number) {
        LongStream.range(number, number + 1000).forEach(System.out::println);
        return Mono.just(number);
    }

    private Mono<Long> sleep(Long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Mono.just(milliseconds);
    }

    private Mono<Long> sleepContext(Long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Mono.just(milliseconds)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private boolean await(CountDownLatch cdl) {
        try {
            cdl.await();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

   private ExecutorService executor() {
       return Executors.newFixedThreadPool(10);
   }
}
