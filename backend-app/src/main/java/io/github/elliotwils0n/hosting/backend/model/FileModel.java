package io.github.elliotwils0n.hosting.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileModel {

    private String filename;
    private byte[] content;

}
