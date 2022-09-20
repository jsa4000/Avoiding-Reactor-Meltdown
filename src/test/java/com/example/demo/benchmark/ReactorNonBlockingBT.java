package com.example.demo.benchmark;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.utils.Time.timed;

/**
 *  Set CPU limits used by JVM: -XX:ActiveProcessorCount=1
 *
 */
@SpringBootTest
public class ReactorNonBlockingBT {

    private final Long SLEEP_TIME = 2000L;
    private final String GREETING_TEMPLATE = "Greetings %s!!";

    private final String GOODBYE_TEMPLATE = "Goodbye %s!!";

    private final Scheduler DEFAULT_SCHEDULER = Schedulers.boundedElastic();

    //private final Scheduler DEFAULT_SCHEDULER = Schedulers.single();

    private final List<String> NAMES = List.of("Araceli","Santiago","Javier","Alberto","Alvaro","Ana","Inés",
            "Mateo","Diana","Christopher","Cristina","Alejandro","Ana","Victor","Raquel");

    @Test
    public void nonBlockingWithSleepTest() {
        Flux<?> flux = Flux.fromIterable(NAMES)
                .flatMap(this::getGreetingAsync)
                .log();
        timed(() -> flux.subscribe(System.out::println));
        sleep(2000L);
    }

    @Test
    public void nonBlockingWithBlockTest() {
        Flux<?> flux = Flux.fromIterable(NAMES)
                .flatMap(this::getGreetingAsync)
                .log();
        timed(flux::blockLast);
    }

    @Test
    public void blockingWithBlockTest() {
        Flux<?> flux = Flux.fromIterable(NAMES)
                .flatMap(name -> Mono.just(this.getGreeting(name)))
                .log();
        timed(flux::blockLast);
    }

    @Test
    public void nonBlockingWithToIterableTest() {
        Flux<?> flux = Flux.fromIterable(NAMES)
                .flatMap(this::getGreetingAsync)
                .log();
        timed(flux::toIterable)
                .forEach(System.out::println);
    }

    /**
     * * Test Performances:
     *
     *   With -XX:ActiveProcessorCount=1 and Schedulers.boundedElastic() => Time Elapsed 8052
     *   With -XX:ActiveProcessorCount=8 and Schedulers.boundedElastic() => Time Elapsed 4041
     *   With -XX:ActiveProcessorCount=1 and Schedulers.single() => Time Elapsed 60166
     *   With -XX:ActiveProcessorCount=8 and Schedulers.single() => Time Elapsed 60166
     *
     */
    @Test
    public void nonBlockingFlowWithBlockTest() {
        Flux<?> flux = Flux.fromIterable(NAMES)
                .flatMap(this::getGreetingAsync)
                .flatMap(this::getGoodbyeAsync)
                .log();
        timed(flux::blockLast);
    }

    /**
     * * Test Performances:
     *
     *   With -XX:ActiveProcessorCount=1 => Time Elapsed 32059
     *   With -XX:ActiveProcessorCount=8 => Time Elapsed 8023
     *
     */
    @Test
    public void nonBlockingFlowWithParallelStreamsTest() {
        timed(() -> NAMES.stream().parallel()
                .map(this::getGreeting)
                .map(this::getGoodbye)
                .collect(Collectors.toList()));
    }

    private Mono<String> getGreetingAsync(String name) {
        // Both 'publishOn' and 'subscribeOn' are similar.
        // Depending on its position the scheduler applies differently after of before.

        //return Mono.just(name)
        //        .publishOn(DEFAULT_SCHEDULER)
        //        .map(this::getGreeting);

        return Mono.fromCallable(() -> getGreeting(name))
                .subscribeOn(DEFAULT_SCHEDULER);
    }

    private Mono<String> getGoodbyeAsync(String name) {
        return Mono.fromCallable(() -> getGoodbye(name))
                .subscribeOn(DEFAULT_SCHEDULER);
    }

    private String getGreeting(String name) {
        sleep(SLEEP_TIME);
        return String.format(GREETING_TEMPLATE, name);
    }

    private String getGoodbye(String name) {
        sleep(SLEEP_TIME);
        return String.format(GOODBYE_TEMPLATE, name);
    }

    private void sleep(Long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
