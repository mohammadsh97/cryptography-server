package com.cryptographyServer.cryptography.server.model;

import java.util.Base64;

public class Crypto extends KeyId {
    private String data;

    public Crypto() {
    }

    public Crypto(String keyId, String data) {
        setKeyId(keyId);
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
