package com.cryptographyServer.cryptography.server.model;

import java.util.Base64;

public class Decrypt extends KeyId {
    private String encryptedData;

    public Decrypt() {
    }

    public Decrypt(String keyId, String encryptedData) {
        setKeyId(keyId);
        this.encryptedData = encryptedData;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }
}
