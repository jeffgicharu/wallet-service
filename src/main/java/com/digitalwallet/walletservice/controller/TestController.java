package com.digitalwallet.walletservice.controller;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class TestController {

    @QueryMapping
    public String hello() {
        return "GraphQL endpoint is active!";
    }
}