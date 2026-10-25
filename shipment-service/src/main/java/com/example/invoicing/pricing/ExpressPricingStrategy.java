package com.example.invoicing.pricing;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ExpressPricingStrategy implements ShippingPricingStrategy {

    private static final BigDecimal EXPRESS_SHIPPING_EXTRA =
            new BigDecimal("10.00");

    private static final BigDecimal EXPRESS_MULTIPLIER =
            new BigDecimal("1.50");

    @Override
    public BigDecimal calculatePrice(
            BigDecimal baseRatePerKm,
            int kilometers) {

        return baseRatePerKm
                .multiply(BigDecimal.valueOf(kilometers))
                .multiply(EXPRESS_MULTIPLIER)
                .add(EXPRESS_SHIPPING_EXTRA);
    }
}