package com.kakopay.payments.api.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @GetMapping("/helloRest")
    public String hello(){
        return "hellow bae rest";

    }
}
