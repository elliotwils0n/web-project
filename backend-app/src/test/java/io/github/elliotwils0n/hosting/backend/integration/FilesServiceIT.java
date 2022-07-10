package io.github.elliotwils0n.hosting.backend.integration;

import io.github.elliotwils0n.hosting.backend.entity.AccountEntity;
import io.github.elliotwils0n.hosting.backend.entity.FileEntity;
import io.github.elliotwils0n.hosting.backend.repository.AccountsRepository;
import io.github.elliotwils0n.hosting.backend.repository.FilesRepository;
import io.github.elliotwils0n.hosting.backend.service.implementation.EncryptionService;
import io.github.elliotwils0n.hosting.backend.service.implementation.FilesService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FilesServiceIT {

    @Autowired
    private FilesService filesService;

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private FilesRepository filesRepository;

    private final String FILENAME = "test.txt";
    private final String FILE_CONTENT = "content.txt";
    private final String FILE_ENCRYPTION_KEY = "keyskeyskeyskeys";

    @BeforeAll
    public void init() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException {

        Mockito.mockStatic(Files.class);
        Mockito.when(Files.createFile(Mockito.any())).thenReturn(null);
        Mockito.when(Files.write(Mockito.any(), Mockito.anyCollection())).thenReturn(null);
        Mockito.when(Files.deleteIfExists(Mockito.any())).thenReturn(true);
        Mockito.when(Files.exists(Mockito.any())).thenReturn(true);

        EncryptionService encryptionService = Mockito.mock(EncryptionService.class);
        Mockito.doReturn(FILE_ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8)).when(encryptionService).encryptKey(Mockito.any());
        Mockito.doReturn(FILE_CONTENT.getBytes(StandardCharsets.UTF_8)).when(encryptionService).encrypt(Mockito.any() ,Mockito.any());

        ReflectionTestUtils.setField(filesService, "encryptionService", encryptionService);
    }

    @Test
    @Rollback
    @Transactional
    public void saveToDatabaseTest() throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // given
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        Mockito.when(mockFile.getOriginalFilename()).thenReturn(FILENAME);
        Mockito.when(mockFile.getBytes()).thenReturn(FILE_CONTENT.getBytes(StandardCharsets.UTF_8));
        LocalDateTime before = LocalDateTime.now().minusSeconds(5L);

        AccountEntity account = accountsRepository.save(new AccountEntity("username", "passwordHash"));

        // when
        filesService.saveFile(account.getId(), mockFile);

        // then
        List<FileEntity> fileEntityList = filesRepository.findAll();

        Assertions.assertEquals(1, fileEntityList.size());

        FileEntity fileEntity = fileEntityList.get(0);

        Assertions.assertNotNull(fileEntity.getId());
        Assertions.assertEquals(account.getId(), fileEntity.getAccountId());
        Assertions.assertNotNull(fileEntity.getEncryptionKey());
        Assertions.assertTrue(fileEntity.getUploadedAt().isAfter(before));
        Assertions.assertTrue(fileEntity.getUploadedAt().isBefore(LocalDateTime.now().plusSeconds(1L)));
    }

    @Test
    @Rollback
    @Transactional
    public void deleteFileFromTest() throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //given
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        Mockito.when(mockFile.getOriginalFilename()).thenReturn(FILENAME);
        Mockito.when(mockFile.getBytes()).thenReturn(FILE_CONTENT.getBytes(StandardCharsets.UTF_8));

        AccountEntity account = accountsRepository.save(new AccountEntity("username", "passwordHash"));
        filesService.saveFile(account.getId(), mockFile);
        FileEntity fileEntity = filesRepository.findAll().get(0);

        //when
        filesService.deleteFile(fileEntity.getAccountId(), fileEntity.getId());

        // then
        List<FileEntity> fileEntityList = filesRepository.findAll();

        Assertions.assertEquals(0, fileEntityList.size());
    }

}
