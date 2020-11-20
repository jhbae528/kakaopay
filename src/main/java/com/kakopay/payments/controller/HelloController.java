package com.kakopay.payments.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HelloController {

    @GetMapping("/hello")
    @ResponseBody
    public String hello(){
        return "hello";
    }

    @RequestMapping(value = "/helloJson")
    @ResponseBody
    public List<Hello> helloJson(){
        Hello hello = new Hello();
        hello.message = "class hello";

        Hello hello2 = new Hello();
        hello2.message = "class hello2";
        List<Hello> list = new ArrayList<Hello>();
        list.add(hello);
        list.add(hello2);
        return list;
    }


    @Getter
    @Setter
    public class Hello {
        private String message;
    }
}
