package io.github.elliotwils0n.hosting.backend;

import io.github.elliotwils0n.hosting.backend.service.implementation.EncryptionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
@ActiveProfiles("test")
public class EncryptionServiceTest {

    @Autowired
    private EncryptionService encryptionService;

    @Test
    public void keyEncryption() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // given
        String key = "12345678abcdefgh";

        // when
        byte[] encryptedKey = encryptionService.encryptKey(key.getBytes(StandardCharsets.UTF_8));
        byte[] decryptedKey = encryptionService.decryptKey(encryptedKey);

        //then
        Assertions.assertEquals(key, new String(decryptedKey, StandardCharsets.UTF_8));
    }

    @Test
    public void whenValidDecryptionKeyProvided_decryptContent() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //given
        String content = "the quick brown fox jumped over the lazy dog";
        String key = "12345678abcdefgh";

        // when
        byte[] encryptedContent = encryptionService.encrypt(content.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
        byte[] decryptedContent = encryptionService.decrypt(encryptedContent, key.getBytes(StandardCharsets.UTF_8));

        // then
        Assertions.assertEquals(content, new String(decryptedContent, StandardCharsets.UTF_8));
    }

    @Test
    public void whenInvalidDecryptionKeyProvided_doNotDecryptContent() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //given
        String content = "the quick brown fox jumped over the lazy dog";
        String key = "12345678abcdefgh";
        String invalidKey = "abcdefgh12345678";

        // when
        byte[] encryptedContent = encryptionService.encrypt(content.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));

        // then
        Assertions.assertThrows(Exception.class, () -> encryptionService.decrypt(encryptedContent, invalidKey.getBytes(StandardCharsets.UTF_8)));
    }
}
