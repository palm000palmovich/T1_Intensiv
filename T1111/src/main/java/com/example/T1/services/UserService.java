package com.example.T1.services;

import com.example.T1.component.RedisCacheUtils;
import com.example.T1.dto.RegisterDto;
import com.example.T1.dto.UserFullInfo;
import com.example.T1.enums.ClientStatus;
import com.example.T1.enums.Type;
import com.example.T1.enums.UserRoles;
import com.example.T1.exceptions.UserAlreadyRegisteredException;
import com.example.T1.exceptions.UserNotFoundException;
import com.example.T1.model.Account;
import com.example.T1.model.Client;
import com.example.T1.model.User;
import com.example.T1.repository.AccountRepository;
import com.example.T1.repository.ClientRepository;
import com.example.T1.repository.UserRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
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
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisCacheUtils redisCacheUtils;
    @Value("${spring.cache.redis.time-to-lived}")
    private Long limitTime;

    public UserService(UserRepository userRepository,
                       ClientRepository clientRepository,
                       AccountRepository accountRepository,
                       PasswordEncoder passwordEncoder,
                       RedisCacheUtils redisCacheUtils){
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisCacheUtils = redisCacheUtils;
    }

    @Override
    public User loadUserByUsername(String userName) {
        String fullKey = "userInfo::" + userName;
        UserFullInfo userFullInfo;
        Client usersClient;
        //Сперва ищем в кеше по ключу userInfo::{userName}
        if (redisCacheUtils.hasKey(fullKey)){
            logger.info("Пользователь есть в кеше.");
            userFullInfo = redisCacheUtils.getValue(fullKey, UserFullInfo.class);
            logger.info("Найденный в кеше юзер: {}", userFullInfo.toString());
            usersClient = clientRepository.findById(userFullInfo.getClientPrimaryKey())
                    .orElseThrow(() -> new UsernameNotFoundException(userName));
            User loggedUser = new User();
            loggedUser.setId(userFullInfo.getUserPrimaryId());
            loggedUser.setUserName(userFullInfo.getUserName());
            loggedUser.setPassword(userFullInfo.getPassword());
            loggedUser.setRole(userFullInfo.getRole());
            loggedUser.setClient(usersClient);

            return loggedUser;
        }
        User foundUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UserNotFoundException(userName));
        usersClient = foundUser.getClient();
        //Кеширование
        cacheUsersFullInfo(foundUser, usersClient);

        return foundUser;
    }


    @Transactional
    public User registerNewUser(RegisterDto registerDto){
        logger.info("Попытка регистрации нового пользователя: {}",
                registerDto.toString());

        //  Если уже есть такой чел,
        if (redisCacheUtils.hasKey("userInfo::" + registerDto.getUsername()) ||
                userRepository.findByUserName(registerDto.getUsername()).isPresent()){
            throw new UserAlreadyRegisteredException(registerDto.getUsername());
        }

        Account account = new Account();
        account.setType(Type.DEBIT);
        account.setBalance(100000L);
        Optional<Account> lastAccount = accountRepository.getLastAccount();
        if (lastAccount.isPresent()){
            account.setAccountId(lastAccount.get().getAccountId());
        }else{account.setAccountId(1L);}

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

        Client client1 = clientRepository.save(client);
        logger.info("Новый клиент успешно сохранен.");

        User user = new User();
        user.setUserName(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRole(UserRoles.USER);
        user.setClient(client1);
        User savedUser = userRepository.save(user);
        logger.info("Новый юзер успешно сохранен");

        //Кеширование
        cacheUsersFullInfo(savedUser, client1);

        return savedUser;
    }

    private void cacheUsersFullInfo(User savedUser, Client client){
        //Кеширование
        UserFullInfo userFullInfo = new UserFullInfo();
        userFullInfo.setUserPrimaryId(savedUser.getId());
        userFullInfo.setUserName(savedUser.getUserName());
        userFullInfo.setPassword(savedUser.getPassword()); //Тут хеш
        userFullInfo.setRole(savedUser.getRole());
        userFullInfo.setClientPrimaryKey(client.getId());


        logger.info("Попытка кеширования полной инфы по юзеру...");
        String fullKey = "userInfo::" + userFullInfo.getUserName();
        try{
            redisCacheUtils.putValue(fullKey, (Object) userFullInfo, limitTime);
            logger.info("Полная инфа по юзеру успешно кеширована.");
        } catch (Exception exep){
            logger.error("Ошибка кеширования: {}", exep.getMessage());
        }
    }
}
