package com.cryptographyServer.cryptography.server.controller;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.cryptographyServer.cryptography.server.model.Crypto;
import com.cryptographyServer.cryptography.server.model.Decrypt;
import com.cryptographyServer.cryptography.server.model.Verify;
import org.json.*;
import com.cryptographyServer.cryptography.server.services.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
public class KeyController {

    @Autowired
    private KeyService keyService;


    @GetMapping(path = "/")
    public String getAllKeys() throws JSONException {
        return this.keyService.getKeyService();
    }

    @GetMapping("/generate")
    public String generateKeys() throws JSONException {
        return keyService.generateKeyService();
    }

    @PostMapping("/encrypt")
    public String encrypt(@RequestBody Crypto jsonData) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        return keyService.encrypt(jsonData);
    }

    @PostMapping(value = "/decrypt")
    public String decrypt(@RequestBody Decrypt jsonData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, JSONException, InvalidKeySpecException {
        return keyService.decrypt(jsonData);
    }

    @PostMapping("/sign")
    public String sign(@RequestBody Crypto jsonData) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, InvalidKeySpecException {
        return keyService.sign(jsonData);
    }

    @PostMapping("/verify")
    public boolean verify(@RequestBody Verify jsonData) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        return keyService.verify(jsonData);
    }
}