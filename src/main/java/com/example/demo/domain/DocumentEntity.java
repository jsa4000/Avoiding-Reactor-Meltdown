package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "DocumentTemplate")
public class DocumentEntity {

    @Id
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
