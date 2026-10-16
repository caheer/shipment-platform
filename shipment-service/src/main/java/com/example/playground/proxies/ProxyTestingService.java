package com.example.playground.proxies;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ProxyTestingService {

    public void processShipment() {
        updateDatabase();
    }

    @Transactional
    public void updateDatabase() {
        System.out.println("UPDATING");
    }
}