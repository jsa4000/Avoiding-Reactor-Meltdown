package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import reactor.blockhound.BlockHound;

import static com.example.demo.Profiles.BLOCKHOUND;

@Configuration
@Profile(BLOCKHOUND)
public class BlockHoundConfiguration {

    static {
        BlockHound.install();
    }

}
