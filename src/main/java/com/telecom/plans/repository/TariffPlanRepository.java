package com.telecom.plans.repository;

import com.telecom.plans.entity.TariffPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TariffPlanRepository extends JpaRepository<TariffPlan, Long> {
    Optional<TariffPlan> findByCode(String code);
    boolean existsByCode(String code);
    List<TariffPlan> findByActiveTrueOrderByPriceAsc();
}
