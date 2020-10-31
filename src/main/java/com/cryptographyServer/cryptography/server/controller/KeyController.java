package com.cryptographyServer.cryptography.server.controller;

import java.security.*;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import com.cryptographyServer.cryptography.server.entity.KeyEntity;
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

    @GetMapping(path = "/")
    public String getAllKeys() throws JSONException {
        StringBuilder result = new StringBuilder("KeyId, publicKey \n");
        for (Map.Entry<String, KeyPair> m : keyService.getMap().entrySet()) {
            result.append(m.getKey()).append(", ").
                    append(keyService.encodeKey(m.getValue().getPublic())).append(" \n");
        }

        JSONArray toJSONArray = CDL.toJSONArray(result.toString());

        return String.valueOf(toJSONArray);
    }

    @PostMapping("/generate")
    public String generateKeys() throws JSONException {
        // Initialization of key pair for encryption and decryption.
        KeyPair keyPair = keyService.getKeyPair();
        KeyService.uniqueID = UUID.randomUUID().toString();
        keyService.addKey(KeyService.uniqueID, keyPair);
        keyRepository.save(
                new KeyEntity(KeyService.uniqueID,
                        keyService.encodeKey(keyPair.getPrivate()),
                        keyService.encodeKey(keyPair.getPublic())));

        JSONObject result = new JSONObject();
        result.put("KeyId", KeyService.uniqueID);

        return String.valueOf(result);
    }

    @PostMapping("/encrypt")
    public String encrypt(@RequestBody Map<String, String> jsonData) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        String keyId = jsonData.get("keyId"), data = jsonData.get("data");

        // Save the data
        KeyService.data = data;

        // Encrypt plain as a cipher.
        String encryptData = keyService.encrypt(keyId, data);

        JSONObject result = new JSONObject();
        result.put("EncryptData", encryptData);

        return String.valueOf(result);
    }

    @PostMapping(value = "/decrypt")
    public String decrypt(@RequestBody Map<String, String> jsonData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, JSONException {

        String keyId = jsonData.get("keyId"), encryptedData = jsonData.get("encryptedData");

        // replaces all occurrences of " " to "+"
        encryptedData = encryptedData.replace(" ", "+");

        // Decrypt cipher to original plain.
        String decryptResult = keyService.decrypt(encryptedData, keyId);

        JSONObject result = new JSONObject();
        result.put("DecryptData", decryptResult);

        return String.valueOf(result);
    }

    @PostMapping("/sign")
    public String sign(@RequestBody Map<String, String> jsonData) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {

        String keyId = jsonData.get("keyId"), data = jsonData.get("data");
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(keyService.getKeyPair(keyId).getPrivate());
        privateSignature.update(data.getBytes(UTF_8));
        byte[] signature = privateSignature.sign();
        return Base64.getEncoder().encodeToString(signature);
    }

    @PostMapping("/verify")
    public boolean verify(@RequestBody Map<String, String> jsonData) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        String keyId = jsonData.get("keyId"), data = jsonData.get("data"), signature = jsonData.get("signature");
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(keyService.getKeyPair(keyId).getPublic());
        publicSignature.update(data.getBytes(UTF_8));
        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        return publicSignature.verify(signatureBytes);
    }
}
