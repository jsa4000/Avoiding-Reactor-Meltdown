package com.example.demo.config;

import com.github.mkopylec.charon.configuration.CharonConfigurer;
import com.github.mkopylec.charon.forwarding.interceptors.rewrite.RequestHostHeaderRewriterConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.github.mkopylec.charon.configuration.CharonConfigurer.charonConfiguration;
import static com.github.mkopylec.charon.configuration.RequestMappingConfigurer.requestMapping;
import static com.github.mkopylec.charon.forwarding.RestTemplateConfigurer.restTemplate;
import static com.github.mkopylec.charon.forwarding.TimeoutConfigurer.timeout;
import static com.github.mkopylec.charon.forwarding.interceptors.rewrite.RegexRequestPathRewriterConfigurer.regexRequestPathRewriter;
import static com.github.mkopylec.charon.forwarding.interceptors.rewrite.RequestServerNameRewriterConfigurer.requestServerNameRewriter;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;

@Configuration
public class ReverseProxyConfiguration {

/*

curl -kv "http:/localhost:8080/k6/pi.php?decimals=3"
curl -k "http:/localhost:8080/dummy/api/v1/employees" | jq .
curl -k --request POST "http:/localhost:8080/dummy/api/v1/create" --data-raw '{"name":"test","salary":"123","age":"23"}'

*/

    @Bean
    public CharonConfigurer charonConfigurer() {
        return charonConfiguration()
                .set(restTemplate().set(timeout().connection(ofSeconds(1)).read(ofMinutes(10)).write(ofMinutes(10))))
                .set(RequestHostHeaderRewriterConfigurer.requestHostHeaderRewriter())
                .add(requestMapping("k6")
                        .pathRegex("/k6/.*")
                        .set(regexRequestPathRewriter()
                                .paths("/k6/(?<path>.*)", "/<path>"))
                        .set(requestServerNameRewriter().outgoingServers("https://test.k6.io")))
                .add(requestMapping("dummy")
                        .pathRegex("/dummy/.*")
                        .set(regexRequestPathRewriter()
                                .paths("/dummy/(?<path>.*)", "/<path>"))
                        .set(requestServerNameRewriter().outgoingServers("https://dummy.restapiexample.com")));
    }

}
