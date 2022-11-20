package com.cryptographyServer.cryptography.server.model;

import java.util.Base64;

public class Verify extends KeyId {

    private String data;
    private String signature;

    public Verify() {
    }

    public Verify(String keyId, String data, String signature) {
        setKeyId(keyId);
        this.data = data;
        this.signature = signature;
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
