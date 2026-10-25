package com.example.invoicing.pricing;

import com.example.shipment.model.Shipment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StandardPricingStrategy implements ShippingPricingStrategy {

    private static final BigDecimal STANDARD_MULTIPLIER =
            new BigDecimal("1.0");

    @Override
    public BigDecimal calculatePrice(
            BigDecimal baseRatePerKm,
            int kilometers) {

        return baseRatePerKm
                .multiply(BigDecimal.valueOf(kilometers))
                .multiply(STANDARD_MULTIPLIER);
    }
}
