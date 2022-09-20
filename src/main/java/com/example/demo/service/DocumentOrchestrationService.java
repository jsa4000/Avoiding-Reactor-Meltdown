package com.example.demo.service;

import com.example.demo.model.Document;
import com.example.demo.service.exception.DocumentNotFoundException;

import java.util.List;

public interface DocumentOrchestrationService {

    Document create(Document document);

    List<Document> create(List<Document> documents);

    Document getById(String documentId) throws DocumentNotFoundException;

    void deleteById(String documentId) throws DocumentNotFoundException;

    List<Document> getAll();

}
