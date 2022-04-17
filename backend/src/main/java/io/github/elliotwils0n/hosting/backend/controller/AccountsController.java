package io.github.elliotwils0n.hosting.backend.controller;

import io.github.elliotwils0n.hosting.backend.model.Credentials;
import io.github.elliotwils0n.hosting.backend.model.ServerMessage;
import io.github.elliotwils0n.hosting.backend.service.implementation.AccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountsController {

    @Autowired
    private AccountsService accountsService;

    @PostMapping("/create")
    public ResponseEntity<ServerMessage> createAccount(@RequestBody Credentials credentials) {
        accountsService.createAccount(credentials);

        return ResponseEntity.ok(new ServerMessage(HttpStatus.OK.toString(), "Account created."));
    }

}
