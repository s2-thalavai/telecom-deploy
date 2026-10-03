package com.telecom.plans.service;

import com.telecom.plans.client.BillingGatewayClient;
import com.telecom.plans.dto.TariffPlanRequest;
import com.telecom.plans.dto.TariffPlanResponse;
import com.telecom.plans.entity.TariffPlan;
import com.telecom.plans.exception.DuplicatePlanException;
import com.telecom.plans.exception.PlanNotFoundException;
import com.telecom.plans.repository.TariffPlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TariffPlanServiceImpl implements TariffPlanService {

    private static final Logger log = LoggerFactory.getLogger(TariffPlanServiceImpl.class);

    private final TariffPlanRepository repository;
    private final BillingGatewayClient billingClient;

    public TariffPlanServiceImpl(TariffPlanRepository repository, BillingGatewayClient billingClient) {
        this.repository = repository;
        this.billingClient = billingClient;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "plans", key = "'active'")
    public List<TariffPlanResponse> findActivePlans() {
        log.debug("Cache miss - loading active plans from DB");
        return repository.findByActiveTrueOrderByPriceAsc().stream().map(TariffPlanResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "planByCode", key = "#code")
    public TariffPlanResponse findByCode(String code) {
        log.debug("Cache miss - loading plan {} from DB", code);
        return repository.findByCode(code).map(TariffPlanResponse::from)
                .orElseThrow(() -> new PlanNotFoundException("Plan not found: " + code));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "plans", allEntries = true)
    public TariffPlanResponse create(TariffPlanRequest req) {
        if (repository.existsByCode(req.code())) {
            throw new DuplicatePlanException("Plan already exists: " + req.code());
        }
        TariffPlan plan = new TariffPlan();
        apply(plan, req);
        plan.setCode(req.code());
        TariffPlanResponse saved = TariffPlanResponse.from(repository.save(plan));
        log.info("Created plan {}", saved.code());
        billingClient.syncPlan(saved);       // resilient: never fails the request
        return saved;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "plans", allEntries = true),
            @CacheEvict(cacheNames = "planByCode", key = "#code")})
    public TariffPlanResponse update(String code, TariffPlanRequest req) {
        TariffPlan plan = load(code);
        apply(plan, req);
        log.info("Updated plan {}", code);
        return TariffPlanResponse.from(repository.saveAndFlush(plan));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "plans", allEntries = true),
            @CacheEvict(cacheNames = "planByCode", key = "#code")})
    public void deactivate(String code) {
        TariffPlan plan = load(code);
        plan.setActive(false);
        log.info("Deactivated plan {}", code);
    }

    private TariffPlan load(String code) {
        return repository.findByCode(code)
                .orElseThrow(() -> new PlanNotFoundException("Plan not found: " + code));
    }

    private static void apply(TariffPlan plan, TariffPlanRequest req) {
        plan.setName(req.name());
        plan.setPrice(req.price());
        plan.setDataGb(req.dataGb());
        plan.setValidityDays(req.validityDays());
        plan.setCategory(req.category() == null ? "PREPAID" : req.category());
    }
}
