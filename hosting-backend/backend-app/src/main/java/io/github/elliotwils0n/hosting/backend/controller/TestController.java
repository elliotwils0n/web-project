package io.github.elliotwils0n.hosting.backend.controller;

import io.github.elliotwils0n.hosting.backend.model.ServerMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/test/")
public class TestController {

    @GetMapping("/get")
    public ResponseEntity<ServerMessage> test(Principal principal){
        return ResponseEntity.ok(new ServerMessage(HttpStatus.OK.value(), String.format("Hey %s, api works!", principal.getName())));
    }
    
}
