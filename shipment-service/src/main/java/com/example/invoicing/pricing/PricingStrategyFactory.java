package com.example.invoicing.pricing;

import com.example.shipment.model.ShippingType;
import org.springframework.stereotype.Component;

@Component
public class PricingStrategyFactory {

    private final EconomyPricingStrategy economy;
    private final StandardPricingStrategy standard;
    private final ExpressPricingStrategy express;

    public PricingStrategyFactory(
            EconomyPricingStrategy economy,
            StandardPricingStrategy standard,
            ExpressPricingStrategy express) {

        this.economy = economy;
        this.standard = standard;
        this.express = express;
    }

    public ShippingPricingStrategy getStrategy(ShippingType type) {

        return switch (type) {
            case ECONOMY -> economy;
            case STANDARD -> standard;
            case EXPRESS -> express;
        };
    }
}