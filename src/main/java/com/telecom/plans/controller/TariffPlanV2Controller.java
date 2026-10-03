package com.telecom.plans.controller;

import com.telecom.plans.dto.TariffPlanV2Response;
import com.telecom.plans.service.TariffPlanService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * v2 API behind a feature flag: enabled in dev/qa/staging, dark in prod until the
 * release decision. Turning it off is a config change, not a redeploy of old code.
 */
@RestController
@RequestMapping("/api/v2/plans")
@ConditionalOnProperty(name = "telecom.features.v2-api-enabled", havingValue = "true")
public class TariffPlanV2Controller {

    private final TariffPlanService service;

    public TariffPlanV2Controller(TariffPlanService service) {
        this.service = service;
    }

    @GetMapping
    public List<TariffPlanV2Response> activePlans() {
        return service.findActivePlans().stream().map(TariffPlanV2Response::from).toList();
    }
}
