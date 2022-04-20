package io.github.elliotwils0n.hosting.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileDto {

    private Long fileId;
    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    private LocalDateTime uploadedAt;
    private String originalFilename;

}
