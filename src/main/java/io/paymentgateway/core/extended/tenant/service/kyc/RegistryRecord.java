package io.paymentgateway.core.extended.tenant.service.kyc;

/** What the government registry holds for a registration number. {@code registeredName} is null when not found. */
public record RegistryRecord(RegistryStatus status, String registeredName) {}
