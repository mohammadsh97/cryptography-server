package com.cryptographyServer.cryptography.server.model;

import java.util.Base64;

public class Verify {

    private String keyId;
    private String data;
    private String signature;

    public Verify() {
    }

    public Verify(String keyId, String data, String signature) {
        this.keyId = keyId;
        this.data = data;
        this.signature = signature;
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
