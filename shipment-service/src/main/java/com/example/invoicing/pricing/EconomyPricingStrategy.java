package com.example.invoicing.pricing;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EconomyPricingStrategy implements  ShippingPricingStrategy {

    private static final BigDecimal ECONOMY_MULTIPLIER =
            new BigDecimal("0.8");

    @Override
    public BigDecimal calculatePrice(
            BigDecimal baseRatePerKm,
            int kilometers) {

        return baseRatePerKm
                .multiply(BigDecimal.valueOf(kilometers))
                .multiply(ECONOMY_MULTIPLIER);
    }
}
