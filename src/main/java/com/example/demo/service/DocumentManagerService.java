package com.example.demo.service;

import com.example.demo.documents.model.Template;
import com.example.demo.model.Document;

public interface DocumentManagerService {

    Template generate(Document document);

}
