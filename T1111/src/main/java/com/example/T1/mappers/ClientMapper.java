package com.example.T1.mappers;

import com.example.T1.dto.ClientDto;
import com.example.T1.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public Client dtoToModel(ClientDto clientDto){
        Client client = new Client();

        client.setFirstName(clientDto.getFirst());
        client.setLastName(clientDto.getLast());
        client.setMiddleName(clientDto.getMiddle());

        return client;
    }
}
