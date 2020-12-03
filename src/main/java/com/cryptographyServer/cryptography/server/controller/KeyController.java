package com.cryptographyServer.cryptography.server.controller;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.cryptographyServer.cryptography.server.entity.KeyEntity;
import com.cryptographyServer.cryptography.server.model.Crypto;
import com.cryptographyServer.cryptography.server.model.Decrypt;
import com.cryptographyServer.cryptography.server.model.Verify;
import com.cryptographyServer.cryptography.server.repository.KeyRepository;
import org.json.*;
import com.cryptographyServer.cryptography.server.services.KeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RestController
@RequestMapping(path = "/api")
public class KeyController {

    @Autowired
    private KeyService keyService;

    @Autowired
    private KeyRepository keyRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public Iterable<KeyEntity> getListOfKeyEntity() {
        return keyRepository.findAll();
    }

    public KeyEntity getListOfKeyEntityByUniqueID(String uniqueID) {
        return keyRepository.findByUniqueID(uniqueID);
    }

    @GetMapping(path = "/")
    public String getAllKeys() throws JSONException {
        StringBuilder result = new StringBuilder("KeyId, publicKey \n");
//        for (Map.Entry<String, KeyPair> m : keyService.getMap().entrySet()) {
//            result.append(m.getKey()).append(", ").
//                    append(keyService.encodeKey(m.getValue().getPublic())).append(" \n");
//        }

        Iterator itr = getListOfKeyEntity().iterator();

        while (itr.hasNext()) {
            KeyEntity element = (KeyEntity) itr.next();
            result.append(element.getUniqueID()).append(", ").
                    append(element.getPublicKey()).append(" \n");
        }

        JSONArray toJSONArray = CDL.toJSONArray(result.toString());
        logger.debug("View all keys generated....");
        return String.valueOf(toJSONArray);
    }

    @PostMapping("/generate")
    public String generateKeys() throws JSONException {
        // Initialization of key pair for encryption and decryption.
        KeyPair keyPair = keyService.getKeyPair();

        logger.debug("generate pair key....");

        KeyService.uniqueID = UUID.randomUUID().toString();
//        keyService.addKey(KeyService.uniqueID, keyPair);
        keyRepository.save(
                new KeyEntity(KeyService.uniqueID,
                        keyService.encodeKey(keyPair.getPrivate()),
                        keyService.encodeKey(keyPair.getPublic())));

        logger.debug("saved key in DB....");

        JSONObject result = new JSONObject();
        result.put("keyId", KeyService.uniqueID);

        return String.valueOf(result);
    }

    @PostMapping("/encrypt")
    public String encrypt(@RequestBody Crypto jsonData) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
//        String keyId = jsonData.get("keyId"), data = jsonData.get("data");
        String keyId = jsonData.getKeyId();
        String data = jsonData.getData();
        // Save the data
        KeyService.data = data;

        // Encrypt plain as a cipher.
        String encryptData = keyService.encrypt(getListOfKeyEntityByUniqueID(keyId), data);

        JSONObject result = new JSONObject();
        result.put("EncryptData", encryptData);

        logger.debug("encrypted the data....");

        return String.valueOf(result);
    }

    @PostMapping(value = "/decrypt")
    public String decrypt(@RequestBody Decrypt jsonData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, JSONException, InvalidKeySpecException {

//        String keyId = jsonData.get("keyId"), encryptedData = jsonData.get("encryptedData");
        String keyId = jsonData.getKeyId();
        String encryptedData = jsonData.getEncryptedData();

        // replaces all occurrences of " " to "+"
        encryptedData = encryptedData.replace(" ", "+");

        // Decrypt cipher to original plain.
        String decryptResult = keyService.decrypt(encryptedData, getListOfKeyEntityByUniqueID(keyId));

        JSONObject result = new JSONObject();
        result.put("DecryptData", decryptResult);

        logger.debug("decrypted the encrypted data....");

        return String.valueOf(result);
    }

    @PostMapping("/sign")
    public String sign(@RequestBody Crypto jsonData) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, InvalidKeySpecException {

//        String keyId = jsonData.get("keyId"), data = jsonData.get("data");
        String keyId = jsonData.getKeyId();
        String data = jsonData.getData();

        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(keyService.decodePrivateKey(getListOfKeyEntityByUniqueID(keyId).getPrivateKey()));
        privateSignature.update(data.getBytes(UTF_8));
        byte[] signature = privateSignature.sign();

        logger.debug("sign is success....");

        return Base64.getEncoder().encodeToString(signature);
    }

    @PostMapping("/verify")
    public boolean verify(@RequestBody Verify jsonData) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {

        String keyId = jsonData.getKeyId(), data = jsonData.getData().toString(), signature = jsonData.getSignature().toString();
        Signature publicSignature = Signature.getInstance("SHA256withRSA");

        publicSignature.initVerify(keyService.decodePublicKey(getListOfKeyEntityByUniqueID(keyId).getPublicKey()));
        publicSignature.update(data.getBytes(UTF_8));
        byte[] signatureBytes = Base64.getDecoder().decode(signature);

        logger.debug("verify is success....");

        return publicSignature.verify(signatureBytes);
    }
}