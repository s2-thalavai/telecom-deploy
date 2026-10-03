package com.telecom.plans.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "tariff_plans")
public class TariffPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "data_gb", nullable = false)
    private int dataGb;

    @Column(name = "validity_days", nullable = false)
    private int validityDays;

    @Column(nullable = false)
    private boolean active = true;

    @Column(length = 20)
    private String category;          // added in V2 migration

    @Version
    private long version;             // optimistic locking for concurrent updates across pods

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() { createdAt = Instant.now(); }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getDataGb() { return dataGb; }
    public void setDataGb(int dataGb) { this.dataGb = dataGb; }
    public int getValidityDays() { return validityDays; }
    public void setValidityDays(int validityDays) { this.validityDays = validityDays; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
}
