package io.github.elliotwils0n.hosting.backend.controller;

import io.github.elliotwils0n.hosting.backend.dto.FileDto;
import io.github.elliotwils0n.hosting.backend.model.FileModel;
import io.github.elliotwils0n.hosting.backend.model.ServerMessage;
import io.github.elliotwils0n.hosting.backend.service.implementation.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.URLConnection;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FilesController {

    @Autowired
    private FilesService filesService;

    @PostMapping("/upload")
    public ResponseEntity<ServerMessage> uploadFile(Principal principal, @RequestAttribute("file") MultipartFile file) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        filesService.saveFile(UUID.fromString(principal.getName()), file);
        return ResponseEntity.ok(new ServerMessage(HttpStatus.OK.value(), "File uploaded successfully"));
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(
            Principal principal,
            @PathVariable("fileId") Long fileId,
            @RequestParam(name = "preview", defaultValue = "false") boolean preview
    ) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        FileModel file = filesService.getFile(UUID.fromString(principal.getName()), fileId);
        String contentDisposition = preview ? String.format("inline; filename=%s", file.getFilename()) : String.format("attachment; filename=%s", file.getFilename());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, URLConnection.guessContentTypeFromName(file.getFilename()))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(file.getContent());
    }

    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<ServerMessage> deleteFile(Principal principal, @PathVariable("fileId") Long fileId) throws IOException {
        filesService.deleteFile(UUID.fromString(principal.getName()), fileId);
        return ResponseEntity.ok(new ServerMessage(HttpStatus.OK.value(), "File deleted successfully."));
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileDto>> getFileList(Principal principal) {
        return ResponseEntity.ok(filesService.getAllAccountFiles(UUID.fromString(principal.getName())));
    }
}
