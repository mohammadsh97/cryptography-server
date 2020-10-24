package com.cryptographyServer.cryptography.server.controller;

import java.security.*;
import java.util.Base64;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.json.*;
import com.cryptographyServer.cryptography.server.services.CipherUtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import static java.nio.charset.StandardCharsets.UTF_8;

@RestController
@RequestMapping(path = "/api")
public class HomeController {

    @Autowired
    private CipherUtilityService cipherUtilityService;

    @GetMapping(path = "/")
    public String getAllKeys() throws JSONException {
        StringBuilder result = new StringBuilder("KeyId, publicKey \n");
        for (Map.Entry<Integer, KeyPair> m : cipherUtilityService.getMap().entrySet()) {
            result.append(m.getKey()).append(", ").
                    append(cipherUtilityService.encodeKey(m.getValue().getPublic())).append(" \n");
        }

        JSONArray toJSONArray = CDL.toJSONArray(result.toString());

        return String.valueOf(toJSONArray);
    }

    @PostMapping("/generate")
    public String generateKeys() throws JSONException {
        // Initialization of key pair for encryption and decryption.
        KeyPair keyPair = cipherUtilityService.getKeyPair();

        cipherUtilityService.addKey(CipherUtilityService.id, keyPair);

        String json = "KeyId \n" +
                CipherUtilityService.id;

        JSONArray result = CDL.toJSONArray(json);

        CipherUtilityService.id++;
        return String.valueOf(result);
    }

    @PostMapping("/encrypt/{keyId}")
    public String encrypt(@PathVariable("keyId") String KeyId, @RequestParam(value = "data") String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, JSONException {

        // Encrypt plain as a cipher.
        String encryptData = cipherUtilityService.encrypt(KeyId, data);

        String json = "encryptData \n" +
                encryptData;

        JSONArray result = CDL.toJSONArray(json);
        return String.valueOf(result);
    }

    @PostMapping(value = "/decrypt/{keyId}")
    public String decrypt(@RequestParam(value = "data") String encryptedData, @PathVariable("keyId") String KeyId) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, JSONException {

        encryptedData = encryptedData.replace(" ", "+");//replaces all occurrences of " " to "+"

        // Decrypt cipher to original plain.
        String decryptResult = cipherUtilityService.decrypt(encryptedData, KeyId);

        String json = "decrypt key \n" +
                decryptResult;

        JSONArray result = CDL.toJSONArray(json);
        return String.valueOf(result);
    }

    @PostMapping("/sign/{keyId}")
    public String sign(@RequestParam(value = "data") String data, @PathVariable("keyId") int id) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {

        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(cipherUtilityService.getKeyPair(id).getPrivate());
        privateSignature.update(data.getBytes(UTF_8));

        byte[] signature = privateSignature.sign();

        return Base64.getEncoder().encodeToString(signature);
    }

    @PostMapping("/verify/{keyId}")
    public boolean verify(@RequestParam(value = "data") String data, @PathVariable("keyId") int id, @RequestParam(value = "signature") String signature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        Signature publicSignature = Signature.getInstance("SHA256withRSA");

        publicSignature.initVerify(cipherUtilityService.getKeyPair(id).getPublic());
        publicSignature.update(data.getBytes(UTF_8));

        byte[] signatureBytes = Base64.getDecoder().decode(signature);

        return publicSignature.verify(signatureBytes);
    }
}
