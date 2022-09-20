package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Document {

    @NotNull
    @NotBlank()
    @Size(max = 64)
    private String documentId;

    @NotNull
    @NotBlank()
    @Size(max = 64)
    private String type;

    @NotNull
    private OffsetDateTime releaseDate;

}
