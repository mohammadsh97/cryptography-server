package com.cryptographyServer.cryptography.server.model;

public class Input extends KeyId {
    private Byte data;

    public Input() {
    }

    public Input(String keyId, Byte data) {
        setKeyId(keyId);
        this.data = data;
    }

    public Byte getData() {
        return data;
    }

    public void setData(Byte data) {
        this.data = data;
    }
}
