package com.example.T1.controllers;

import com.example.T1.dto.ClientDto;
import com.example.T1.model.Client;
import com.example.T1.services.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/client")
public class ClientController {

    @Autowired
    private ClientService clientService;
    private Logger logger = LoggerFactory.getLogger(ClientController.class);


    @PostMapping
    public ResponseEntity<ClientDto> saveClient(@Valid @RequestBody ClientDto clientDto){
        logger.info("Полученное значение: {}", clientDto.toString());
        try{
            clientService.createClient(clientDto);
            return ResponseEntity.ok(clientDto);
        } catch(Exception ex){
            logger.error("В методе saveClient произошла ошибка: {}", ex.getMessage(), ex);

            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/{id}")
    public Client findClientById(@PathVariable("id") Long id){
        return clientService.getClient(id);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Client> editClient(@PathVariable("id") Long userId,
                                             @Valid @RequestBody ClientDto clientDto){
        Client client = clientService.updateClient(userId, clientDto);

        if (client == null){
           return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(client);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Client> deleteClientById(@PathVariable("id") Long id){
        Client client = clientService.deleteClient(id);

        if (client == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(client);
    }

}