package com.example.demo.config;

import com.example.demo.documents.DocumentManager;
import com.example.demo.documents.generator.DocumentGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DocumentsConfiguration {

    @Bean
    public DocumentManager documentManager(List<DocumentGenerator> generators) {
        return new DocumentManager(generators);
    }
}
