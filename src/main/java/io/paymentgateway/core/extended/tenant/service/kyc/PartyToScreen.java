package io.paymentgateway.core.extended.tenant.service.kyc;

import io.paymentgateway.core.domain.enumeration.CountryCode;
import java.time.LocalDate;

/** A natural person (director / beneficial owner) to be checked against watchlists. */
public record PartyToScreen(String fullName, LocalDate dateOfBirth, CountryCode nationality) {}
