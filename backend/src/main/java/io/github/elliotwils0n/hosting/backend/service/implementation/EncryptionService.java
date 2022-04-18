package io.github.elliotwils0n.hosting.backend.service.implementation;

import io.github.elliotwils0n.hosting.backend.service.EncryptionServiceInterface;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class EncryptionService implements EncryptionServiceInterface {

    private final String masterKey;
    private final int GCM_IV_LENGTH;
    private final SecureRandom secureRandom;
    private final byte[] associatedData;

    public EncryptionService(@Value("${files.encryption.master.key}") String masterKey) {
        this.masterKey = masterKey;
        this.GCM_IV_LENGTH = 12;
        this.secureRandom = new SecureRandom();
        this.associatedData = "ProtocolVersion1".getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] encrypt(byte[] plainContent, byte[] encryptionKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);
        SecretKeySpec encryptionKeySpec = new SecretKeySpec(encryptionKey, "AES");
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
    public byte[] decrypt(byte[] encryptedContent, byte[] encryptionKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec encryptionKeySpec = new SecretKeySpec(encryptionKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        AlgorithmParameterSpec gcmIv = new GCMParameterSpec(128, encryptedContent, 0, GCM_IV_LENGTH);
        cipher.init(Cipher.DECRYPT_MODE, encryptionKeySpec, gcmIv);
        cipher.updateAAD(associatedData);
        return cipher.doFinal(encryptedContent, GCM_IV_LENGTH, encryptedContent.length - GCM_IV_LENGTH);
    }

    @Override
    public byte[] encryptKey(byte[] plainKey) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return encrypt(plainKey, masterKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public byte[] decryptKey(byte[] encryptedKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return decrypt(encryptedKey, masterKey.getBytes(StandardCharsets.UTF_8));
    }
}
