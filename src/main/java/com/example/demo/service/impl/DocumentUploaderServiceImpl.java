package com.example.demo.service.impl;

import com.example.demo.documents.model.Template;
import com.example.demo.service.DocumentUploaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.example.demo.util.Sleep.sleep;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentUploaderServiceImpl implements DocumentUploaderService {

    private final Long SLEEP_TIME = 2000L;

    @Override
    public Boolean upload(Template template) {
        sleep(SLEEP_TIME);
        return true;
    }
}
