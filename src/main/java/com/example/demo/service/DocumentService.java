package com.example.demo.service;

import com.example.demo.model.Document;
import com.example.demo.service.exception.DocumentNotFoundException;

import java.util.List;

public interface DocumentService {

    Document create(Document document);

    Document getById(String documentId) throws DocumentNotFoundException;

    void deleteById(String documentId) throws DocumentNotFoundException;

    List<Document> getAll();

}
