package com.work.cashier.security;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtil
{

    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final String SECRET_KEY = "1234567890123456";
    
    public static boolean isBase64(String input)
    {
        if (input == null || input.trim().isEmpty()) return false;

        try
        {
            byte[] decoded = Base64.getDecoder().decode(input.trim());
            return decoded.length > IV_LENGTH_BYTE;
        }

        catch (IllegalArgumentException e) { return false; }
    }

    public static String encrypt(String data) throws Exception
    {
        if (data == null || data.trim().isEmpty())
            throw new IllegalArgumentException("Data to encrypt cannot be null or empty");
        

        byte[] iv = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(iv);
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        byte[] encryptedIvAndText = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, encryptedIvAndText, 0, iv.length);
        System.arraycopy(encrypted, 0, encryptedIvAndText, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(encryptedIvAndText);
    }

    public static String decrypt(String encryptedData) throws Exception
    {

        if (encryptedData == null || encryptedData.trim().isEmpty())
            throw new IllegalArgumentException("Encrypted data cannot be null or empty");

        encryptedData = encryptedData.trim();

        if (!isBase64(encryptedData))
            throw new IllegalArgumentException("Invalid Base64 format for encrypted data: " + encryptedData);

        byte[] decode;

        try
        {
            decode = Base64.getDecoder().decode(encryptedData);
        } 
        catch (IllegalArgumentException e)
        {
            throw new IllegalArgumentException("Failed to decode Base64 data: " + encryptedData, e);
        }

        if (decode.length <= IV_LENGTH_BYTE)
            throw new IllegalArgumentException("Encrypted data too short, expected at least " + (IV_LENGTH_BYTE + 1) + " bytes but got " + decode.length);

        byte[] iv = new byte[IV_LENGTH_BYTE];
        System.arraycopy(decode, 0, iv, 0, iv.length);
        int encryptedSize = decode.length - IV_LENGTH_BYTE;
        byte[] encryptedBytes = new byte[encryptedSize];
        System.arraycopy(decode, IV_LENGTH_BYTE, encryptedBytes, 0, encryptedSize);
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
        byte[] decrypted = cipher.doFinal(encryptedBytes);

        return new String(decrypted, StandardCharsets.UTF_8);
    }
}