package com.cryptographyServer.cryptography.server.controller;

import com.cryptographyServer.cryptography.server.CryptographyServerApplication;
import com.cryptographyServer.cryptography.server.services.KeyService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CryptographyServerApplication.class)
class HomeControllerTest {

    @Autowired
    private KeyService keyService;

    @Test
    void CryptographyServerTest() {
        KeyService.uniqueID = UUID.randomUUID().toString();
        String text = "Lets go!";

        // Initialization of key pair for encryption and decryption.
        KeyPair keyPair = keyService.getKeyPair();
        keyService.addKey(KeyService.uniqueID, keyPair);
        try {
            // Get public key from the key pair.
            PublicKey pubKey = keyPair.getPublic();
            // Get private key from the key pair.
            PrivateKey privKey = keyPair.getPrivate();
            // Try to encode public key as a string.
            String pubKeyStr = keyService.encodeKey(pubKey);
            // Assertion of 'pubKey' and the public key decoded by 'pubKeyStr'.
            Assert.assertEquals(pubKey, keyService.decodePublicKey(pubKeyStr));
            // Try to encode private key as a string.
            String privKeyStr = keyService.encodeKey(privKey);
            // Assertion of 'privKey' and the private key decoded by 'privKeyStr'.
            Assert.assertEquals(privKey, keyService.decodePrivateKey(privKeyStr));
            // Encrypt data as a cipher.
            String encryptedData = keyService.encrypt(KeyService.uniqueID, text);
            // Decrypt cipher to original plain.
            String decryptData = keyService.decrypt(encryptedData, KeyService.uniqueID);
            // Assertion of 'plain' and 'decryptResult'.
            Assert.assertEquals(text, decryptData);
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}