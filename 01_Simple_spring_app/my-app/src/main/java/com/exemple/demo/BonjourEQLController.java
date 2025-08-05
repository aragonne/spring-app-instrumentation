package com.exemple.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BonjourEQLController {

    @GetMapping("/bonjour-eql")
    public String direBonjourEQL() {
        return "Bonjour EQL, Voici Spring!";
    }
}