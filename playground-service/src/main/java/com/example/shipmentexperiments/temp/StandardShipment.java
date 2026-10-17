package com.example.shipmentexperiments.temp;


import java.math.BigDecimal;
import java.util.UUID;

public class StandardShipment extends AbstractShipment {

    private UUID id;
    private BigDecimal weight;

    public StandardShipment(UUID id, BigDecimal weight) {
        super(id, weight);
    }

    public void printSomething()
    {
        System.out.println("StandardShipment");
    }


    public BigDecimal calculatePrice() {

        return weight.multiply(BigDecimal.TEN);
    }
}

