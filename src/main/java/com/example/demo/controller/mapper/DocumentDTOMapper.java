package com.example.demo.controller.mapper;

import com.example.demo.controller.dto.DocumentDTO;
import com.example.demo.model.Document;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentDTOMapper {

    DocumentDTO toDTO(Document model);

    Document fromDTO(DocumentDTO dto);
}
