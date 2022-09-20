package com.example.demo.controller;

import com.example.demo.controller.dto.DocumentDTO;
import com.example.demo.controller.mapper.DocumentDTOMapper;
import com.example.demo.model.Document;
import com.example.demo.service.DocumentOrchestrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentOrchestrationService documentOrchestrationService;

    private final DocumentDTOMapper documentDTOMapper;

    @PostMapping("/documents")
    public ResponseEntity<DocumentDTO> create(@RequestBody DocumentDTO document) {
        return ResponseEntity.ok(documentDTOMapper.toDTO(documentOrchestrationService
                .create(documentDTOMapper.fromDTO(document))));
    }

    @PostMapping("/documents/bulk")
    public ResponseEntity<List<DocumentDTO>> generate(@RequestBody List<DocumentDTO> documents) {
        List<Document> docs = documents.stream()
                .map(documentDTOMapper::fromDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(documentOrchestrationService.create(docs).stream()
                .map(documentDTOMapper::toDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<DocumentDTO> getById(@PathVariable("id") String id) {
        return ResponseEntity.ok(documentDTOMapper.toDTO(documentOrchestrationService.getById(id)));
    }

    @GetMapping("/documents")
    public ResponseEntity<List<DocumentDTO>> getAll() {
        return ResponseEntity.ok(documentOrchestrationService.getAll().stream()
                .map(documentDTOMapper::toDTO).collect(Collectors.toList()));
    }

}
