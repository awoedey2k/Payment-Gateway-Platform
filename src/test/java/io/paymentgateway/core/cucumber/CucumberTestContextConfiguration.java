package io.paymentgateway.core.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import io.paymentgateway.core.IntegrationTest;
import io.paymentgateway.core.security.AuthoritiesConstants;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;

@CucumberContextConfiguration
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
public class CucumberTestContextConfiguration {}
