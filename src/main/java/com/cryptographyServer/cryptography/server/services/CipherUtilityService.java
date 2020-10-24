package com.cryptographyServer.cryptography.server.services;


import java.security.*;
import java.util.Base64;
import java.util.HashMap;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class CipherUtilityService {

    public static int id = 0;

    public HashMap<Integer, KeyPair> map = new HashMap<>();

    private static KeyPairGenerator keyPairGenerator = null;

    private final SecureRandom random = new SecureRandom();

    public KeyPair getKeyPair() {
        return keyPairGenerator.generateKeyPair();
    }

    public String encrypt(String KeyId, String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getKeyPair(Integer.parseInt(KeyId)).getPublic());
        byte[] cipherContent = cipher.doFinal(data.getBytes());

        return Base64.getEncoder().encodeToString(cipherContent);
    }

    public String decrypt(String encryptedData, String id) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, getKeyPair(Integer.parseInt(id)).getPrivate());
        byte[] cipherContentBytes = Base64.getDecoder().decode(encryptedData.getBytes());
        byte[] decryptedContent = cipher.doFinal(cipherContentBytes);

        return new String(decryptedContent);
    }

    public String encodeKey(Key key) {
        byte[] keyBytes = key.getEncoded();
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    @PostConstruct
    private void init() {
        try {
            if (keyPairGenerator == null) keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048, random);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<Integer, KeyPair> getMap() {
        return map;
    }

    public KeyPair getKeyPair(int id) {
        return map.get(id);
    }

    public void addKey(int id, KeyPair KeyPair) {
        getMap().put(id, KeyPair);
    }

}