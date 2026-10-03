package com.telecom.plans.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record TariffPlanRequest(
        @NotBlank @Size(max = 30) @Pattern(regexp = "[A-Z0-9]+") String code,
        @NotBlank @Size(max = 100) String name,
        @NotNull @DecimalMin("1.00") BigDecimal price,
        @PositiveOrZero int dataGb,
        @Positive int validityDays,
        @Pattern(regexp = "PREPAID|POSTPAID") String category) {
}
