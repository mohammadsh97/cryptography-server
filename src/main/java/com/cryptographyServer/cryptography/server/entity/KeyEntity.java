package com.cryptographyServer.cryptography.server.entity;

import javax.persistence.*;

@Entity
@Table
public class KeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;
    @Column
    private String uniqueID;

    @Column(columnDefinition = "TEXT")
    private String privateKey;
    @Column(columnDefinition = "TEXT")
    private String publicKey;

    public KeyEntity() {

    }

    public KeyEntity(String uniqueID, String privateKey, String publicKey) {
        this.uniqueID = uniqueID;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
