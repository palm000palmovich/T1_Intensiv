package com.example.T1.services;

import com.example.T1.dto.RegisterDto;
import com.example.T1.enums.UserRoles;
import com.example.T1.model.Client;
import com.example.T1.model.User;
import com.example.T1.repository.ClientRepository;
import com.example.T1.repository.UserRepository;
import jakarta.transaction.Transactional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       ClientRepository clientRepository,
                       PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    }

    //TODO сделать кеширование и не быть мудаком по отношению к постгре
    @Transactional
    public User registerNewUser(RegisterDto registerDto){
        logger.info("Регистрация нового пользователя: {}",
                registerDto.getFirstName() + " " + registerDto.getMiddleName() +
                        " " + registerDto.getLastName());
        Client client = new Client();
        client.setFirstName(registerDto.getFirstName());
        client.setLastName(registerDto.getLastName());
        client.setMiddleName(registerDto.getMiddleName());
        Optional<Client> lastClient = clientRepository.getLastClient();
        if (lastClient.isPresent()){
            client.setClientId(lastClient.get().getClientId() + 1);
            //TODO сделать так, чтобы id последнего клиента лежал в кеше
        } else{
            client.setClientId(1L);
        }

        clientRepository.save(client);
        logger.info("Новый клиент успешно сохранен.");

        User user = new User();
        user.setUserName(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRole(UserRoles.USER);
        user.setClient(client);

        return userRepository.save(user);
    }
}
