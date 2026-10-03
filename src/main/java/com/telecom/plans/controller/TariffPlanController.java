package com.telecom.plans.controller;

import com.telecom.plans.dto.TariffPlanRequest;
import com.telecom.plans.dto.TariffPlanResponse;
import com.telecom.plans.service.TariffPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/plans")
public class TariffPlanController {

    private final TariffPlanService service;

    public TariffPlanController(TariffPlanService service) {
        this.service = service;
    }

    @GetMapping
    public List<TariffPlanResponse> activePlans() {
        return service.findActivePlans();
    }

    @GetMapping("/{code}")
    public TariffPlanResponse byCode(@PathVariable String code) {
        return service.findByCode(code);
    }

    @PostMapping
    public ResponseEntity<TariffPlanResponse> create(@Valid @RequestBody TariffPlanRequest request) {
        TariffPlanResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/plans/" + created.code())).body(created);
    }

    @PutMapping("/{code}")
    public TariffPlanResponse update(@PathVariable String code, @Valid @RequestBody TariffPlanRequest request) {
        return service.update(code, request);
    }

    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable String code) {
        service.deactivate(code);
    }
}
