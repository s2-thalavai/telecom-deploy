package com.telecom.plans.service;

import com.telecom.plans.dto.TariffPlanRequest;
import com.telecom.plans.dto.TariffPlanResponse;

import java.util.List;

public interface TariffPlanService {
    List<TariffPlanResponse> findActivePlans();
    TariffPlanResponse findByCode(String code);
    TariffPlanResponse create(TariffPlanRequest request);
    TariffPlanResponse update(String code, TariffPlanRequest request);
    void deactivate(String code);
}
