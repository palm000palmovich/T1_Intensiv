package com.example.T1.controllers;

import com.example.T1.dto.AccountDto;
import com.example.T1.exceptions.UserNotFoundException;
import com.example.T1.model.Account;
import com.example.T1.services.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/accounts")
public class AccountController {
    private final AccountService accountService;
    private Logger logger = LoggerFactory.getLogger(AccountController.class);

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    @PostMapping(path = "/{id}")
    public ResponseEntity<Account> createAccount(@PathVariable("id") Long clientId,
            @Valid @RequestBody AccountDto accountDto) {
        logger.info("Creating account with data: {}", accountDto);

        try {
            Account createdAccount = accountService.createAccount(clientId, accountDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
        } catch (UserNotFoundException ex) {
            logger.error("Error creating account: {}", ex.getMessage(), ex);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/{id}")
    public Account getAcc(@PathVariable("id") Long id){
        return accountService.getAccountById(id);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable("id") Long id,
                                                 @Valid @RequestBody AccountDto accountDto){
        Account updatedAccount = accountService.updateAccount(id, accountDto);

        if (updatedAccount == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Account> deleteAcc(@PathVariable("id") Long id){
        Account deletedAccount = accountService.deleteAccount(id);

        if (deletedAccount == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(deletedAccount);
    }

}