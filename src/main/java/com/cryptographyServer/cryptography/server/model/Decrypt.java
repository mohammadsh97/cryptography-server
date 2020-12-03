package com.cryptographyServer.cryptography.server.model;

import java.util.Base64;

public class Decrypt {
    private String keyId;
    private String encryptedData;

    public Decrypt() {
    }

    public Decrypt(String keyId, String encryptedData) {
        this.keyId = keyId;
        this.encryptedData = encryptedData;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }
}
