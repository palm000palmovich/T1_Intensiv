package com.example.T1.component;

import com.example.T1.enums.AccountStatus;
import com.example.T1.enums.TransactionStatus;
import com.example.T1.enums.Type;
import com.example.T1.model.Account;
import com.example.T1.model.Client;
import com.example.T1.model.Transaction;
import com.example.T1.repository.ClientRepository;
import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
public class DataGenerator {
    private final ClientRepository clientRepository;
    private final Faker faker = new Faker();

    private final List<AccountStatus> listOfAccStatus = List.of(AccountStatus.ARRESTED, AccountStatus.BLOCKED,
            AccountStatus.CLOSED, AccountStatus.OPEN);
    private final List<TransactionStatus> listOfTranStatus = List.of(TransactionStatus.ACCEPTED, TransactionStatus.REJECTED,
            TransactionStatus.BLOCKED, TransactionStatus.CANCELLED,
            TransactionStatus.REQUESTED);
    private final Random random = new Random();

    private Logger logger = LoggerFactory.getLogger(DataGenerator.class);

    public DataGenerator(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @PostConstruct
    public void generateData(){
        int extremum = 60;

        if (clientRepository.count() < extremum){
            logger.info("БД пуста. Происходит заполнение.");
            for (int i = 0; i < extremum; ++i){

                Client client = new Client();
                client.setFirstName(faker.name().firstName());
                client.setLastName(faker.name().lastName());
                client.setMiddleName(faker.name().nameWithMiddle());
                client.setClientId(i+1L);

                Account account = new Account();
                account.setType(i % 2 == 0 ? Type.CREDIT : Type.DEBIT);
                account.setBalance(faker.number().randomNumber(6, true));
                account.setAccountId(i+1L);
                account.setStatus(listOfAccStatus.get(random.nextInt(4)));
                account.setFrozenAmount(random.nextLong(account.getBalance()));
                client.addAccount(account);

                Transaction transaction = new Transaction();
                transaction.setValue(faker.number().randomNumber(4, true));
                transaction.setTimestamp(LocalDateTime.now().plusMinutes(i));
                transaction.setTransactionId(i+1L);
                transaction.setStatus(listOfTranStatus.get(random.nextInt(5)));
                account.addTransaction(transaction);


                clientRepository.save(client);
            }
            logger.info("Данные успешно сгенерированы.");
        } else{
            logger.info("БД уже содержит данные.");
        }
    }
}
