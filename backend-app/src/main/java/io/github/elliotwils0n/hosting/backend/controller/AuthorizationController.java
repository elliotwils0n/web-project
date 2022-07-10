package io.github.elliotwils0n.hosting.backend.controller;

import io.github.elliotwils0n.hosting.backend.model.Credentials;
import io.github.elliotwils0n.hosting.backend.model.TokenPair;
import io.github.elliotwils0n.hosting.backend.service.implementation.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthorizationController {

    @Autowired
    private AuthorizationService authorizationService;

    @PostMapping("/generateToken")
    public ResponseEntity<TokenPair> generateToken(@RequestBody Credentials credentials) {
        TokenPair tokenPair = authorizationService.generateTokenPair(credentials);

        return ResponseEntity.ok(tokenPair);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<TokenPair> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String refreshToken) {
        TokenPair tokenPair = authorizationService.refreshTokens(refreshToken.replace("Bearer ", ""));

        return ResponseEntity.ok(tokenPair);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok().build();
    }

}
