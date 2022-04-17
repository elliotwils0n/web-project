package io.github.elliotwils0n.hosting.backend.service.implementation;

import io.github.elliotwils0n.hosting.backend.service.EncryptionServiceInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;


@Service
public class EncryptionService implements EncryptionServiceInterface {

    private final int GCM_IV_LENGTH;
    private final SecureRandom secureRandom;
    private final SecretKeySpec encryptionKeySpec;
    private final byte[] associatedData;

    public EncryptionService(@Value("${files.encryption.key}") String encryptionKey) {
        this.GCM_IV_LENGTH = 12;
        this.secureRandom = new SecureRandom();
        this.encryptionKeySpec = new SecretKeySpec(encryptionKey.getBytes(), "AES");
        this.associatedData = "ProtocolVersion1".getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] encrypt(byte[] plainContent) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKeySpec, parameterSpec);
        cipher.updateAAD(associatedData);
        byte[] cipherContent = cipher.doFinal(plainContent);

        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherContent.length);
        byteBuffer.put(iv);
        byteBuffer.put(cipherContent);
        return byteBuffer.array();
    }

    @Override
    public byte[] decrypt(byte[] encryptedContent) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        AlgorithmParameterSpec gcmIv = new GCMParameterSpec(128, encryptedContent, 0, GCM_IV_LENGTH);
        cipher.init(Cipher.DECRYPT_MODE, encryptionKeySpec, gcmIv);
        cipher.updateAAD(associatedData);
        return cipher.doFinal(encryptedContent, GCM_IV_LENGTH, encryptedContent.length - GCM_IV_LENGTH);
    }
}
