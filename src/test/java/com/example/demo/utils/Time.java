package com.example.demo.utils;

import java.util.function.Supplier;

public class Time {

    public static <T> T timed(Supplier<T> function) {
        long start = System.currentTimeMillis();
        T result = function.get();
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.printf("Time Elapsed %d%n", timeElapsed);
        return result;
    }

}
