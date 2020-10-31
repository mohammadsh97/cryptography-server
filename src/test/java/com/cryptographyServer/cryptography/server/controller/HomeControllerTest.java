package com.cryptographyServer.cryptography.server.controller;

import com.cryptographyServer.cryptography.server.services.KeyService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@WebMvcTest(KeyController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void decrypt() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/decrypt/{keyId}");
        MvcResult result = mvc.perform(request).andReturn();
        // Assertion of 'data' and 'decryptDataResult'.
        Assert.assertEquals(KeyService.data, result.getResponse().getContentAsString());
    }
}