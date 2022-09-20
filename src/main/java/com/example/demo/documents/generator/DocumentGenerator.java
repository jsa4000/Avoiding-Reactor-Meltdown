package com.example.demo.documents.generator;

import com.example.demo.documents.model.Template;
import com.example.demo.model.Document;

public interface DocumentGenerator {

    String getType();

    Template generate(Document document);

}
