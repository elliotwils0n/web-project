package io.github.elliotwils0n.hosting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileDto {

    private Long fileId;
    private LocalDateTime uploadedAt;
    private String originalFilename;

}
