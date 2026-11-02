package com.example.invoicing.pricing;

import java.math.BigDecimal;

public interface ShippingPricingStrategy {

    BigDecimal calculatePrice(BigDecimal baseRatePerKm,
                              int kilometers);
}
