package com.cryptographyServer.cryptography.server.controller;

import com.cryptographyServer.cryptography.server.CryptographyServerApplication;
import com.cryptographyServer.cryptography.server.entity.KeyEntity;
import com.cryptographyServer.cryptography.server.repository.KeyRepository;
import com.cryptographyServer.cryptography.server.services.KeyService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.security.*;
import java.util.UUID;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CryptographyServerApplication.class)
class HomeControllerTest {

    @Autowired
    private KeyService keyService;

    @Autowired
    private KeyController keyController;

    @Autowired
    private KeyRepository repository;

    @Test
    void CryptographyServerTest() throws Exception {
        KeyService.uniqueID = UUID.randomUUID().toString();
        String text = "Lets go!";


        // Initialization of key pair for encryption and decryption.
        KeyPair keyPair = initialization();

        // Get public key from the key pair.
        PublicKey pubKey = keyPair.getPublic();
        // Get private key from the key pair.
        PrivateKey privKey = keyPair.getPrivate();

        // Try to encode public key as a string.
        String pubKeyStr = keyService.encodeKey(pubKey);

        // Assertion of 'pubKey' and the public key decoded by 'pubKeyStr'.
        assertPublicKey(pubKey, keyService.decodePublicKey(pubKeyStr));

        // Try to encode private key as a string.
        String privKeyStr = keyService.encodeKey(privKey);

        // Assertion of 'privKey' and the private key decoded by 'privKeyStr'.
        assertPrivateKey(privKey, keyService.decodePrivateKey(privKeyStr));

        // save to DB
        saveHibernate(privKeyStr, pubKeyStr);

        // check if the hibernate save success
        checkHibernate(KeyService.uniqueID, privKeyStr, pubKeyStr);

        // Encrypt data as a cipher.
        String encryptedData = keyService.encrypt(repository.findByUniqueID(KeyService.uniqueID), text);

        // Decrypt cipher to original plain.
        String decryptData = keyService.decrypt(encryptedData, repository.findByUniqueID(KeyService.uniqueID));

        // Assertion of 'plain' and 'decryptResult'.
        assertEqualsStr(text, decryptData);
    }


    // Initialization of key pair for encryption and decryption.
    private KeyPair initialization() {
        KeyPair keyPair = keyService.getKeyPair();
        return keyPair;
    }

    // Assertion of 'privKey'
    private void assertPrivateKey(PrivateKey key, PrivateKey key2) {
        Assert.assertEquals(key, key2);
    }

    // Assertion of 'pubKey'
    private void assertPublicKey(PublicKey key, PublicKey key2) {
        Assert.assertEquals(key, key2);
    }

    // save to DB
    private void saveHibernate(String privateKey, String publicKey) {
        repository.save(new KeyEntity(KeyService.uniqueID, privateKey, publicKey));
    }

    // assert equal String
    private void assertEqualsStr(String str, String str2) {
        Assert.assertEquals(str, str2);
    }

    // check hibernate
    private void checkHibernate(String uniqueID, String privateKey, String publicKey) throws Exception {
        KeyEntity keyEntity = keyController.getListOfKeyEntityByUniqueID(uniqueID);
        if (keyEntity != null) {
            Assert.assertEquals(keyEntity.getPrivateKey(), privateKey);
            Assert.assertEquals(keyEntity.getPublicKey(), publicKey);
        } else throw new Exception("Error, the DB is null");
    }
}