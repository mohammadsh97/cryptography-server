package com.cryptographyServer.cryptography.server.services;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.annotation.PostConstruct;

import com.cryptographyServer.cryptography.server.model.Crypto;
import com.cryptographyServer.cryptography.server.model.Decrypt;
import com.cryptographyServer.cryptography.server.model.Verify;
import org.json.JSONObject;
import org.slf4j.Logger;
import com.cryptographyServer.cryptography.server.entity.KeyEntity;
import com.cryptographyServer.cryptography.server.repository.KeyRepository;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class KeyService {

    @Autowired
    private KeyRepository keyRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public String uniqueID;

    public String data;

    private static KeyPairGenerator keyPairGenerator = null;

    private final SecureRandom random = new SecureRandom();

    private Iterable<KeyEntity> getListOfKeyEntity() {
        return keyRepository.findAll();
    }

    public KeyEntity getListOfKeyEntityByUniqueID(String uniqueID) {
        return keyRepository.findByUniqueID(uniqueID);
    }

    public KeyPair getKeyPair() {
        return keyPairGenerator.generateKeyPair();
    }

    public String getKeyService() throws JSONException {
        StringBuilder result = new StringBuilder("KeyId, publicKey \n");

        for (KeyEntity element : getListOfKeyEntity()) {
            result.append(element.getUniqueID()).append(", ").
                    append(element.getPublicKey()).append(" \n");
        }

        JSONArray toJSONArray = CDL.toJSONArray(result.toString());
        logger.debug("View all keys generated....");
        return String.valueOf(toJSONArray);
    }

    public String generateKeyService() throws JSONException {
        // Initialization of key pair for encryption and decryption.
        KeyPair keyPair = getKeyPair();

        logger.debug("generate pair key....");

        uniqueID = UUID.randomUUID().toString();
        keyRepository.save(
                new KeyEntity(uniqueID,
                        encodeKey(keyPair.getPrivate()),
                        encodeKey(keyPair.getPublic())));


        logger.debug("saved key in DB....");

        JSONObject result = new JSONObject();
        result.put("keyId", uniqueID);

        return String.valueOf(result);
    }

    public String encryptHelper(KeyEntity keyEntity, String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        cipher.init(Cipher.ENCRYPT_MODE, decodePublicKey(keyEntity.getPublicKey()));
        byte[] cipherContent = cipher.doFinal(data.getBytes());

        return Base64.getEncoder().encodeToString(cipherContent);
    }

    public String encrypt(Crypto jsonData) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        String keyId = jsonData.getKeyId();
        String data = jsonData.getData();
        // Save the data
        this.data = data;

        // Encrypt plain as a cipher.
        String encryptData = encryptHelper(getListOfKeyEntityByUniqueID(keyId), data);

        JSONObject result = new JSONObject();
        result.put("EncryptData", encryptData);

        logger.debug("encrypted the data....");

        return String.valueOf(result);
    }

    public String decryptHelper(String encryptedData, KeyEntity keyEntity) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, decodePrivateKey(keyEntity.getPrivateKey()));
        byte[] cipherContentBytes = Base64.getDecoder().decode(encryptedData.getBytes());
        byte[] decryptedContent = cipher.doFinal(cipherContentBytes);

        return new String(decryptedContent);
    }

    public String decrypt(Decrypt jsonData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, JSONException, InvalidKeySpecException {
        String keyId = jsonData.getKeyId();
        String encryptedData = jsonData.getEncryptedData();

        // replaces all occurrences of " " to "+"
        encryptedData = encryptedData.replace(" ", "+");

        // Decrypt cipher to original plain.
        String decryptResult = decryptHelper(encryptedData, getListOfKeyEntityByUniqueID(keyId));

        JSONObject result = new JSONObject();
        result.put("DecryptData", decryptResult);

        logger.debug("decrypted the encrypted data....");

        return String.valueOf(result);
    }

    public String sign(Crypto jsonData) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, InvalidKeySpecException {
        String keyId = jsonData.getKeyId();
        String data = jsonData.getData();

        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(decodePrivateKey(getListOfKeyEntityByUniqueID(keyId).getPrivateKey()));
        privateSignature.update(data.getBytes(UTF_8));
        byte[] signature = privateSignature.sign();

        JSONObject result = new JSONObject();
        result.put("sign", Base64.getEncoder().encodeToString(signature));

        logger.debug("sign is success....");

        return String.valueOf(result);
    }

    public boolean verify(Verify jsonData) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {

        String keyId = jsonData.getKeyId(), data = jsonData.getData(), signature = jsonData.getSignature();
        Signature publicSignature = Signature.getInstance("SHA256withRSA");

        publicSignature.initVerify(decodePublicKey(getListOfKeyEntityByUniqueID(keyId).getPublicKey()));
        publicSignature.update(data.getBytes(UTF_8));
        byte[] signatureBytes = Base64.getDecoder().decode(signature);

        logger.debug("verify is success....");

        return publicSignature.verify(signatureBytes);
    }

    public String encodeKey(Key key) {
        byte[] keyBytes = key.getEncoded();
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    public PublicKey decodePublicKey(String keyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    public PrivateKey decodePrivateKey(String keyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
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
}