package com.example.demo.benchmark;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.utils.Time.timed;

@SpringBootTest
public class ParallelStreamsBT {

    @Test
    public void parallel_streams_simple_test() {
        List<String> list = List.of("First","Second","Third");
        timed(() -> { list.stream().parallel().forEach(System.out::println); return null; });
    }

    @Test
    public void parallel_streams_sleep_test() {
        List<Long> list = List.of(3000L,2000L,1000L);
        timed(() -> { list.stream().parallel().forEach(this::sleep); return null; });
    }

    @Test
    public void parallel_streams_map_test() {
        List<Long> list = List.of(3000L,2000L,1000L);
        timed(() -> list.stream().parallel().map(i -> {
                sleep(i);
                return i;
        }).collect(Collectors.toList()));
    }

    @Test
    public void parallel_streams_map2_test() {
        List<Long> list = List.of(3000L,2000L,1000L);
        List<String> result = timed( () -> list.stream().parallel().map(i -> {
            sleep(i);
            return String.format("My mapped value %s", i);
        }).collect(Collectors.toList()));
        result.forEach(System.out::println);
    }

    private void sleep(Long milliseconds) {
        System.out.printf("Start Sleeping %d%n", milliseconds);
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.printf("Stop Sleeping %d%n", milliseconds);
    }

}
