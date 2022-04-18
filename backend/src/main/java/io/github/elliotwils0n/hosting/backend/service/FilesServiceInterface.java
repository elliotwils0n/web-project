package io.github.elliotwils0n.hosting.backend.service;

import io.github.elliotwils0n.hosting.backend.dto.FileDto;
import io.github.elliotwils0n.hosting.backend.infrastructure.GenericServerException;
import io.github.elliotwils0n.hosting.backend.model.FileModel;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

public interface FilesServiceInterface {

    void saveFile(UUID accountId, MultipartFile file) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;

    FileModel getFile(UUID accountId, Long fileId) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;

    void deleteFile(UUID accountId, Long fileId) throws IOException;

    List<FileDto> getAllAccountFiles(UUID accountId);


    class FileLinkedToAccountNotFoundException extends GenericServerException {
        public FileLinkedToAccountNotFoundException() {
            super("File by given ID not found for account.");
        }
    }
}
