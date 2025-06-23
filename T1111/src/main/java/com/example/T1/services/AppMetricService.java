package com.example.T1.services;

import com.example.T1.repository.AccountRepository;
import com.example.T1.repository.ClientRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class AppMetricService {
    private final MeterRegistry registry;
    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    private final AtomicLong blockedClients = new AtomicLong(0);
    private final AtomicLong arrestedAccounts = new AtomicLong(0);

    public AppMetricService(MeterRegistry registry,
                             ClientRepository clientRepository,
                             AccountRepository accountRepository) {
        this.registry = registry;
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
    }

    @PostConstruct
    public void initMetrics() {
        Gauge.builder("clients.blocked", blockedClients, AtomicLong::get)
                .description("Количество заблокированных клиентов")
                .register(registry);

        Gauge.builder("accounts.arrested", arrestedAccounts, AtomicLong::get)
                .description("Количество арестованных счетов")
                .register(registry);

        updateMetrics();
    }

    public void updateMetrics() {
        Long blocked = clientRepository.countBlockedClients();
        Long frozen = accountRepository.countFrozenAccounts();

        blockedClients.set(blocked);
        arrestedAccounts.set(frozen);
    }
}
