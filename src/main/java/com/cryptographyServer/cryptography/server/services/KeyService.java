package com.cryptographyServer.cryptography.server.services;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.annotation.PostConstruct;

import com.cryptographyServer.cryptography.server.entity.KeyEntity;
import org.springframework.stereotype.Component;

@Component
public class KeyService {
    public static String uniqueID;

    public static String data;

//    public HashMap<String, KeyPair> map = new HashMap<>();

    private static KeyPairGenerator keyPairGenerator = null;

    private final SecureRandom random = new SecureRandom();

    public KeyPair getKeyPair() {
        return keyPairGenerator.generateKeyPair();
    }

    public String encrypt(KeyEntity keyEntity, String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        cipher.init(Cipher.ENCRYPT_MODE, decodePublicKey(keyEntity.getPublicKey()));
        byte[] cipherContent = cipher.doFinal(data.getBytes());

        return Base64.getEncoder().encodeToString(cipherContent);
    }

    public String decrypt(String encryptedData, KeyEntity keyEntity) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, decodePrivateKey(keyEntity.getPrivateKey()));
        byte[] cipherContentBytes = Base64.getDecoder().decode(encryptedData.getBytes());
        byte[] decryptedContent = cipher.doFinal(cipherContentBytes);

        return new String(decryptedContent);
    }

    public String encodeKey(Key key) {
        byte[] keyBytes = key.getEncoded();
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    public PublicKey decodePublicKey(String keyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(spec);
        return key;
    }

    public PrivateKey decodePrivateKey(String keyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        return key;
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

//    public HashMap<String, KeyPair> getMap() {
//        return map;
//    }

//    public KeyPair getKeyPair(String id) {
//        return map.get(id);
//    }

//    public void addKey(String id, KeyPair KeyPair) {
//        getMap().put(id, KeyPair);
//    }

}