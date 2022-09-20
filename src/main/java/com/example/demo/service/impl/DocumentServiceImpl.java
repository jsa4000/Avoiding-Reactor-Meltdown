package com.example.demo.service.impl;

import com.example.demo.model.Document;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.service.DocumentService;
import com.example.demo.service.exception.DocumentNotFoundException;
import com.example.demo.service.mapper.DocumentServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    private final DocumentServiceMapper documentServiceMapper;

    @Override
    @Transactional
    public Document create(Document document) {
        return documentServiceMapper.fromEntity(documentRepository.save(documentServiceMapper.toEntity(document)));
    }

    @Override
    public Document getById(String documentId) throws DocumentNotFoundException {
        return documentServiceMapper.fromEntity(documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found")));
    }

    @Override
    public void deleteById(String documentId) throws DocumentNotFoundException {
        documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found"));
        documentRepository.deleteById(documentId);
    }

    @Override
    public List<Document> getAll() {
        return StreamSupport.stream(documentRepository.findAll().spliterator(),false)
                .map(documentServiceMapper::fromEntity)
                .collect(Collectors.toList());
    }
}
