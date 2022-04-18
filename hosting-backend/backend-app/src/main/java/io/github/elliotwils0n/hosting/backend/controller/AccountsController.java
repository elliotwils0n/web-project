package io.github.elliotwils0n.hosting.backend.controller;

import io.github.elliotwils0n.hosting.backend.model.Credentials;
import io.github.elliotwils0n.hosting.backend.model.ServerMessage;
import io.github.elliotwils0n.hosting.backend.service.implementation.AccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin("http://localhost:4200")
public class AccountsController {

    @Autowired
    private AccountsService accountsService;

    @PostMapping("/create")
    public ResponseEntity<ServerMessage> createAccount(@RequestBody Credentials credentials) {
        accountsService.createAccount(credentials);

        return ResponseEntity.ok(new ServerMessage(HttpStatus.OK.value(), "Account created successfully."));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ServerMessage> deleteAccount(Principal principal) {
        accountsService.deleteAccount(UUID.fromString(principal.getName()));

        return ResponseEntity.ok(new ServerMessage(HttpStatus.OK.value(), "Account deleted successfully."));
    }

}
