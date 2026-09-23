import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('TenantFeeConfig e2e test', () => {
  const tenantFeeConfigPageUrl = '/tenant-fee-config';
  let username: string;
  let password: string;
  // const tenantFeeConfigSample = {"fixedFee":27129.47,"percentageFee":31133.08,"feeBearer":"MERCHANT","isActive":true};

  let tenantFeeConfig;
  // let corporateTenant;
  // let countryPaymentMethod;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/corporate-tenants',
      body: {"legalBusinessName":"polite ingratiate","businessRegistrationNumber":"produce inwardly","taxIdentificationNumber":"and excluding","operatingJurisdiction":"NG","status":"CLOSED","kycStatus":"NOT_STARTED","riskScore":31483,"createdAt":"2026-09-21T13:31:29.848Z","activatedAt":"2026-09-21T06:30:27.038Z"},
    }).then(({ body }) => {
      corporateTenant = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/country-payment-methods',
      body: {"minTxnAmount":19744.12,"maxTxnAmount":21683.32,"supportsRecurring":true,"supportsInstantRefund":false,"isActive":false},
    }).then(({ body }) => {
      countryPaymentMethod = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/tenant-fee-configs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/tenant-fee-configs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/tenant-fee-configs/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/corporate-tenants', {
      statusCode: 200,
      body: [corporateTenant],
    });

    cy.intercept('GET', '/api/country-payment-methods', {
      statusCode: 200,
      body: [countryPaymentMethod],
    });

  });
   */

  afterEach(() => {
    if (tenantFeeConfig) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/tenant-fee-configs/${tenantFeeConfig.id}`,
      }).then(() => {
        tenantFeeConfig = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (corporateTenant) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/corporate-tenants/${corporateTenant.id}`,
      }).then(() => {
        corporateTenant = undefined;
      });
    }
    if (countryPaymentMethod) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/country-payment-methods/${countryPaymentMethod.id}`,
      }).then(() => {
        countryPaymentMethod = undefined;
      });
    }
  });
   */

  it('TenantFeeConfigs menu should load TenantFeeConfigs page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('tenant-fee-config');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TenantFeeConfig').should('exist');
    cy.location('pathname').should('eq', tenantFeeConfigPageUrl);
  });

  describe('TenantFeeConfig page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(tenantFeeConfigPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TenantFeeConfig page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${tenantFeeConfigPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('TenantFeeConfig');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', tenantFeeConfigPageUrl);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/tenant-fee-configs',
          body: {
            ...tenantFeeConfigSample,
            tenant: corporateTenant,
            countryPaymentMethod: countryPaymentMethod,
          },
        }).then(({ body }) => {
          tenantFeeConfig = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/tenant-fee-configs+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/tenant-fee-configs?page=0&size=20>; rel="last",<http://localhost/api/tenant-fee-configs?page=0&size=20>; rel="first"',
              },
              body: [tenantFeeConfig],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(tenantFeeConfigPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(tenantFeeConfigPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details TenantFeeConfig page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('tenantFeeConfig');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', tenantFeeConfigPageUrl);
      });

      it('edit button click should load edit TenantFeeConfig page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TenantFeeConfig');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', tenantFeeConfigPageUrl);
      });

      it('edit button click should load edit TenantFeeConfig page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TenantFeeConfig');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', tenantFeeConfigPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of TenantFeeConfig', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('tenantFeeConfig').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', tenantFeeConfigPageUrl);

        tenantFeeConfig = undefined;
      });
    });
  });

  describe('new TenantFeeConfig page', () => {
    beforeEach(() => {
      cy.visit(tenantFeeConfigPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TenantFeeConfig');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of TenantFeeConfig', () => {
      cy.get(`[data-cy="fixedFee"]`).type('745.89');
      cy.get(`[data-cy="fixedFee"]`).should('have.value', '745.89');

      cy.get(`[data-cy="percentageFee"]`).type('3680.96');
      cy.get(`[data-cy="percentageFee"]`).should('have.value', '3680.96');

      cy.get(`[data-cy="capAmount"]`).type('14491.35');
      cy.get(`[data-cy="capAmount"]`).should('have.value', '14491.35');

      cy.get(`[data-cy="feeBearer"]`).select('MERCHANT');

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(`[data-cy="tenant"]`).select(1);
      cy.get(`[data-cy="countryPaymentMethod"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        tenantFeeConfig = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', tenantFeeConfigPageUrl);
    });
  });
});
