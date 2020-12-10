package com.cryptographyServer.cryptography.server.model;

import java.util.Base64;

public class Crypto {
    private String keyId;
    private String data;

    public Crypto() {
    }

    public Crypto(String keyId, String data) {
        this.keyId = keyId;
        this.data = data;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
