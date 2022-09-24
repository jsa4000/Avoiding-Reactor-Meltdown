package com.example.demo.benchmark;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Optional;

public class ContextReactorBT {

    static final String HTTP_CORRELATION_ID = "reactive.http.library.correlationId";

    @Test
    public void contextWriteMustReturnOk() {
        String key = "message";
        Mono<String> source = Mono.just("Hello")
                .flatMap(s -> Mono.deferContextual(ctx ->
                        Mono.just(s + " " + ctx.get(key))))
                .contextWrite(ctx -> ctx.put(key, "World"));

        StepVerifier.create(source)
                .expectNext("Hello World")
                .verifyComplete();
    }

    @Test
    public void contextWriteTooHighMustReturnOk() {
        String key = "message";
        Mono<String> source = Mono.just("Hello")
                .contextWrite(ctx -> ctx.put(key, "World")) // The context propagates upstream, not downstream
                .flatMap( s -> Mono.deferContextual(ctx ->
                        Mono.just(s + " " + ctx.getOrDefault(key, "Stranger"))));

        StepVerifier.create(source)
                .expectNext("Hello Stranger")
                .verifyComplete();
    }

    @Test
    public void contextWriteMultipleTimesMustReturnOk() {
        String key = "message";
        Mono<String> source = Mono.deferContextual(ctx -> Mono.just("Hello " + ctx.get(key)))
                .contextWrite(ctx -> ctx.put(key, "Reactor"))
                .contextWrite(ctx -> ctx.put(key, "World"));

        StepVerifier.create(source)
                .expectNext("Hello Reactor")
                .verifyComplete();
    }

    @Test
    public void contextWriteOverridesMustReturnOk() {
        String key = "message";
        Mono<String> r = Mono.deferContextual(ctx -> Mono.just("Hello " + ctx.get(key)))
                .contextWrite(ctx -> ctx.put(key, "Reactor"))
                .flatMap( s -> Mono.deferContextual(ctx ->
                        Mono.just(s + " " + ctx.get(key))))
                .contextWrite(ctx -> ctx.put(key, "World"));

        StepVerifier.create(r)
                .expectNext("Hello Reactor World")
                .verifyComplete();
    }

    @Test
    public void contextWriteWithinFlatMapMustReturnOk() {
        String key = "message";
        Mono<String> source = Mono.just("Hello")
                .flatMap( s -> Mono.deferContextual(ctxView -> Mono.just(s + " " + ctxView.get(key))))
                .flatMap( s -> Mono.deferContextual(ctxView -> Mono.just(s + " " + ctxView.get(key)))
                        .contextWrite(ctx -> ctx.put(key, "Reactor")))
                .contextWrite(ctx -> ctx.put(key, "World"));

        StepVerifier.create(source)
                .expectNext("Hello World Reactor")
                .verifyComplete();
    }

    @Test
    public void contextForLibraryReactivePut() {
        Mono<String> put = doPut("www.example.com", Mono.just("Walter"))
                .contextWrite(Context.of(HTTP_CORRELATION_ID, "2-j3r9afaf92j-afkaf"))
                .filter(t -> t.getT1() < 300)
                .map(Tuple2::getT2);

        StepVerifier.create(put)
                .expectNext("PUT <Walter> sent to www.example.com" +
                        " with header X-Correlation-ID = 2-j3r9afaf92j-afkaf")
                .verifyComplete();
    }

    private Mono<Tuple2<Integer, String>> doPut(String url, Mono<String> data) {
        Mono<Tuple2<String, Optional<Object>>> dataAndContext = data.zipWith(Mono.deferContextual(c ->
                        Mono.just(c.getOrEmpty(HTTP_CORRELATION_ID))));

        return dataAndContext.<String>handle((dac, sink) -> {
                    if (dac.getT2().isPresent()) {
                        sink.next("PUT <" + dac.getT1() + "> sent to " + url +
                                " with header X-Correlation-ID = " + dac.getT2().get());
                    }
                    else {
                        sink.next("PUT <" + dac.getT1() + "> sent to " + url);
                    }
                    sink.complete();
                })
                .map(msg -> Tuples.of(200, msg));
    }

}
