package com.example.demo.threading;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

public class ThreadingUtils {

    public static final long SLEEP_TIME = 1000L;

    private static final Scheduler DEFAULT_SCHEDULER = Schedulers.boundedElastic();

    public static void doTask() {
        System.out.println("doTask Start");
        sleep(SLEEP_TIME);
        System.out.println("doTask Stop");
    }

    public static Mono<Object> doTaskAsync() {
        return Mono.fromRunnable(ThreadingUtils::doTask)
                .subscribeOn(DEFAULT_SCHEDULER);
    }

    public static void firstStep() {
        System.out.println("firstStep Start");
        sleep(SLEEP_TIME);
        System.out.println("firstStep Finished");
    }

    public static void secondStep() {
        System.out.println("secondStep Start");
        sleep(SLEEP_TIME);
        System.out.println("secondStep Finished");
    }

    public static String getFirstStep() {
        firstStep();
        return "getFirstStep";
    }

    public static Mono<String> getFirstStepAsync() {
        return Mono.fromCallable(ThreadingUtils::getFirstStep)
                .subscribeOn(DEFAULT_SCHEDULER);
    }

    public static String getSecondStep() {
        secondStep();
        return "getSecondStep";
    }

    public static Mono<String> getSecondStepAsync() {
        return Mono.fromCallable(ThreadingUtils::getSecondStep)
                .subscribeOn(DEFAULT_SCHEDULER);
    }

    public static Integer multiply(Integer number) {
        sleep(SLEEP_TIME);
        return number * 2;
    }

    public static Integer printThread(Integer number) {
        System.out.println(number + " " + Thread.currentThread().getName());
        return number;
    }

    public static Duration getMaxTimeout() {
        return Duration.ofMillis(Double.valueOf(SLEEP_TIME + (SLEEP_TIME * 0.2)).longValue());
    }

    public static void sleep(Long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
