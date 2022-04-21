package io.github.elliotwils0n.hosting.backend.service.implementation;

import io.github.elliotwils0n.hosting.backend.dto.FileDto;
import io.github.elliotwils0n.hosting.backend.entity.FileEntity;
import io.github.elliotwils0n.hosting.backend.model.AccountDeletedEvent;
import io.github.elliotwils0n.hosting.backend.model.FileModel;
import io.github.elliotwils0n.hosting.backend.repository.FilesRepository;
import io.github.elliotwils0n.hosting.backend.service.FilesServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilesService implements FilesServiceInterface {

    @Autowired
    private FilesRepository filesRepository;

    @Autowired
    private EncryptionService encryptionService;

    @Value("${files.root.directory}")
    private String rootDirectory;

    @Override
    public void saveFile(UUID accountId, MultipartFile file) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        prepareUserDirectory(accountId);

        FileEntity fileEntity = saveFileInfoInDatabase(accountId, file.getOriginalFilename());
        String filepath = String.format("%s/%s/%s.enc", rootDirectory, accountId, fileEntity.getId());

        Files.createFile(Path.of(filepath));
        byte[] encryptedFile = encryptionService.encrypt(file.getBytes(), encryptionService.decryptKey(fileEntity.getEncryptionKey()));
        Files.write(Path.of(filepath), encryptedFile);
    }

    @Override
    public FileModel getFile(UUID accountId, Long fileId) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        FileEntity fileEntity = filesRepository.findByIdAndAccountId(fileId, accountId).orElseThrow(FileLinkedToAccountNotFoundException::new);
        String filepath = String.format("%s/%s/%s.enc", rootDirectory, accountId, fileId);
        byte [] encryptedFile = Files.readAllBytes(Path.of(filepath));

        return new FileModel(fileEntity.getOriginalFilename(), encryptionService.decrypt(encryptedFile, encryptionService.decryptKey(fileEntity.getEncryptionKey())));
    }

    @Override
    @Transactional
    public void deleteFile(UUID accountId, Long fileId) throws IOException {
        FileEntity fileToDelete = filesRepository.findByIdAndAccountId(fileId, accountId).orElseThrow(FileLinkedToAccountNotFoundException::new);

        String filepath = String.format("%s/%s/%s.enc", rootDirectory, accountId, fileId);
        Files.deleteIfExists(Path.of(filepath));
        filesRepository.delete(fileToDelete);
    }

    @Override
    public List<FileDto> getAllAccountFiles(UUID accountId) {
        return filesRepository.findAllByAccountIdOrderByUploadedAtDesc(accountId).stream()
                .map(file -> new FileDto(file.getId(), file.getUploadedAt(), file.getOriginalFilename()))
                .collect(Collectors.toList());
    }

    @EventListener
    public void onAccountDeletion(AccountDeletedEvent accountDeletedEvent) throws IOException {
        for (FileDto file : accountDeletedEvent.getAccountFiles()) {
            String filepath = String.format("%s/%s/%s.enc", rootDirectory, accountDeletedEvent.getAccountId(), file.getFileId());
            Files.deleteIfExists(Path.of(filepath));
        }
        String userDirectory = String.format("%s/%s", rootDirectory, accountDeletedEvent.getAccountId());
        Files.deleteIfExists(Path.of(userDirectory));
    }

    private FileEntity saveFileInfoInDatabase(UUID accountId, String originalFilename) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String plainKey = getRandomString();
        byte[] encryptionKey = encryptionService.encryptKey(plainKey.getBytes(StandardCharsets.UTF_8));
        FileEntity fileEntity = new FileEntity(accountId, originalFilename, encryptionKey);
        return filesRepository.save(fileEntity);
    }

    private void prepareUserDirectory(UUID accountId) throws IOException {
        String userDirectory = String.format("%s/%s", rootDirectory, accountId);
        if (!Files.exists(Path.of(userDirectory))) {
            Files.createDirectory(Path.of(userDirectory));
        }
    }

    private String getRandomString() {
        return RandomStringUtils.random(16, 0, 0, true, true, null, new SecureRandom());
    }

}
