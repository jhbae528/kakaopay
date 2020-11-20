package com.kakopay.payments.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloRestController {
    @GetMapping("/helloRest")
    public String hello(){
        return "hellow bae rest";

    }
}
