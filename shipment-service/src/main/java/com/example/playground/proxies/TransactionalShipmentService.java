package com.example.playground.proxies;

public class TransactionalShipmentService implements ShipmentService {

    private final ShipmentService target;

    public TransactionalShipmentService(ShipmentService target) {
        this.target = target;
    }
    // SO THIS CLASS ACTS AS A PROXY BECAUSE we are calling updateShipment() here in TransactionalShipmentService (it implements ShipmentService, but updateShipment() is overriden)
    // and only THEN Calling the ShipmentService - ShipmentServiceImpl ?
    @Override
    public void updateShipment() {  // THIS IS THIS ADDITIONAL LAYER?
        System.out.println("BEGIN");

        try {
            target.updateShipment();  // T

            System.out.println("COMMIT");
        } catch (RuntimeException e) {
            System.out.println("ROLLBACK");
            throw e;
        }
    }
}