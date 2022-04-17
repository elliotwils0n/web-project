package io.github.elliotwils0n.hosting.backend.service.implementation;

import io.github.elliotwils0n.hosting.backend.dto.FileDto;
import io.github.elliotwils0n.hosting.backend.entity.AccountEntity;
import io.github.elliotwils0n.hosting.backend.entity.FileEntity;
import io.github.elliotwils0n.hosting.backend.model.FileModel;
import io.github.elliotwils0n.hosting.backend.repository.FilesRepository;
import io.github.elliotwils0n.hosting.backend.service.FilesServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilesService implements FilesServiceInterface {

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private FilesRepository filesRepository;

    @Autowired
    private EncryptionService encryptionService;

    @Value("${files.root.directory}")
    private String rootDirectory;

    @Override
    public void saveFile(UUID accountId, MultipartFile file) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String userDirectory = getUserDirectory(accountId);

        FileEntity fileEntity = saveFileInfoInDatabase(accountId, file.getOriginalFilename());
        String filepath = String.format("%s/%s.enc", userDirectory, fileEntity.getId());

        Files.createFile(Path.of(filepath));
        byte[] encryptedFile = encryptionService.encrypt(file.getBytes());
        Files.write(Path.of(filepath), encryptedFile);
    }

    @Override
    public FileModel getFile(UUID accountId, Long fileId) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        AccountEntity accountEntity = accountsService.findByUUID(accountId);
        FileEntity fileEntity = filesRepository.findByIdAndAccount(fileId, accountEntity).orElseThrow(FileLinkedToAccountNotFoundException::new);
        String filepath = String.format("%s/%s/%s.enc", rootDirectory, accountId, fileId);
        byte [] encryptedFile = Files.readAllBytes(Path.of(filepath));

        return new FileModel(fileEntity.getOriginalFilename(), encryptionService.decrypt(encryptedFile));
    }

    @Override
    public List<FileDto> getAllAccountFiles(UUID accountId) {
        AccountEntity account = accountsService.findByUUID(accountId);
        return filesRepository.findAllByAccount(account).stream()
                .map(file -> new FileDto(file.getId(), file.getUploadedAt(), file.getOriginalFilename()))
                .collect(Collectors.toList());
    }

    private FileEntity saveFileInfoInDatabase(UUID accountId, String originalFilename) {
        AccountEntity account = accountsService.findByUUID(accountId);
        FileEntity fileEntity = new FileEntity(account, originalFilename);
        return filesRepository.save(fileEntity);
    }

    private String getUserDirectory(UUID accountId) throws IOException {
        String userDirectory = String.format("%s/%s", rootDirectory, accountId);
        if (!Files.exists(Path.of(userDirectory))) {
            Files.createDirectory(Path.of(userDirectory));
        }
        return userDirectory;
    }

}
