package com.example.demo.service.impl;

import com.example.demo.documents.DocumentManager;
import com.example.demo.documents.model.Template;
import com.example.demo.model.Document;
import com.example.demo.service.DocumentManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.example.demo.util.Sleep.sleep;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentManagerServiceImpl implements DocumentManagerService {

    private final Long SLEEP_TIME = 2000L;
    private final DocumentManager documentManager;

    @Override
    public Template generate(Document document) {
        sleep(SLEEP_TIME);
        return documentManager.generate(document);
    }
}
