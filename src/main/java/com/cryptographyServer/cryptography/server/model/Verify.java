package com.cryptographyServer.cryptography.server.model;

import java.util.Base64;

public class Verify {

    private String keyId;
    private Base64 data;
    private Base64 signature;

    public Verify() {
    }

    public Verify(String keyId, Base64 data, Base64 signature) {
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

    public Base64 getData() {
        return data;
    }

    public void setData(Base64 data) {
        this.data = data;
    }

    public Base64 getSignature() {
        return signature;
    }

    public void setSignature(Base64 signature) {
        this.signature = signature;
    }
}
