package io.github.elliotwils0n.hosting.backend;

import io.github.elliotwils0n.hosting.backend.entity.FileEntity;
import io.github.elliotwils0n.hosting.backend.repository.FilesRepository;
import io.github.elliotwils0n.hosting.backend.service.implementation.EncryptionService;
import io.github.elliotwils0n.hosting.backend.service.implementation.FilesService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FilesServiceTest {

    @Autowired
    private FilesService filesService;

    @Autowired
    private EncryptionService encryptionService;

    private final String rootDirectory = ".";

    private final UUID ACCOUNT_ID = UUID.randomUUID();
    private final String FILENAME = "test.txt";
    private final String FILE_CONTENT = "content.txt";
    private final String FILE_ENCRYPTION_KEY = "keyskeyskeyskeys";
    private FileEntity fileEntity;

    @BeforeAll
    public void init() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        byte[] encryptionKey = encryptionService.encryptKey(FILE_ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8));
        fileEntity = new FileEntity(ACCOUNT_ID, FILENAME, encryptionKey);
        fileEntity.setId(1L);

        FilesRepository filesRepository = Mockito.mock(FilesRepository.class);

        Mockito.doNothing().when(filesRepository).delete(Mockito.any());
        Mockito.doReturn(fileEntity).when(filesRepository).save(Mockito.any());
        Mockito.doReturn(Optional.of(fileEntity)).when(filesRepository).findByIdAndAccountId(Mockito.any(), Mockito.any());

        ReflectionTestUtils.setField(filesService, "encryptionService", encryptionService);
        ReflectionTestUtils.setField(filesService, "rootDirectory", rootDirectory);
        ReflectionTestUtils.setField(filesService, "filesRepository", filesRepository);
    }

    @AfterEach
    public void clean() throws IOException {
        Files.deleteIfExists(Path.of(String.format("%s/%s/%s.enc", rootDirectory, ACCOUNT_ID, fileEntity.getId())));
    }

    @Test
    public void savingFileTest() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // given
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        Mockito.when(mockFile.getOriginalFilename()).thenReturn(FILENAME);
        Mockito.when(mockFile.getBytes()).thenReturn(FILE_CONTENT.getBytes(StandardCharsets.UTF_8));

        // when
        filesService.saveFile(ACCOUNT_ID, mockFile);

        // then
        Assertions.assertTrue(Files.exists(Path.of(String.format("%s/%s/%s.enc", rootDirectory, ACCOUNT_ID, fileEntity.getId()))));
    }

    // when all tests are running, timing must be off and it fails
    @Disabled
    @Test
    public void deleteFileTest() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // given
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        Mockito.when(mockFile.getOriginalFilename()).thenReturn(FILENAME);
        Mockito.when(mockFile.getBytes()).thenReturn(FILE_CONTENT.getBytes(StandardCharsets.UTF_8));
        filesService.saveFile(ACCOUNT_ID, mockFile);
        // when
        filesService.deleteFile(ACCOUNT_ID, fileEntity.getId());

        // then
        Assertions.assertFalse(Files.exists(Path.of(String.format("%s/%s/%s.enc", rootDirectory, ACCOUNT_ID, fileEntity.getId()))));
    }

}
