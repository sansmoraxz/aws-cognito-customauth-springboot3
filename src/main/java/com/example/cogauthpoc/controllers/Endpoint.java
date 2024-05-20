package com.example.cogauthpoc.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Endpoint {
    @GetMapping("/")
    public String hello() {
        return "Hello, world!";
    }
}
