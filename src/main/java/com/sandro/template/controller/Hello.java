package com.sandro.template.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class Hello {


    @RequestMapping("/")
    String home() {
        log.info("home() has been called");
        return "Hello World!";
    }

}
