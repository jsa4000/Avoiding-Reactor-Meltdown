package com.example.demo.service.mapper;

import com.example.demo.domain.DocumentEntity;
import com.example.demo.model.Document;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentServiceMapper {

    DocumentEntity toEntity(Document model);

    Document fromEntity(DocumentEntity entity);
}
