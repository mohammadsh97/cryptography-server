package com.cryptographyServer.cryptography.server.model;

public class Input {
    private String keyId;
    private Byte data;

    public Input() {
    }

    public Input(String keyId, Byte data) {
        this.keyId = keyId;
        this.data = data;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public Byte getData() {
        return data;
    }

    public void setData(Byte data) {
        this.data = data;
    }
}
