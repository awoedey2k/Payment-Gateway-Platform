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

describe('TenantWallet e2e test', () => {
  const tenantWalletPageUrl = '/tenant-wallet';
  let username: string;
  let password: string;
  const tenantWalletSample = { currencyCode: 'GHS', availableBalance: 16211, lockedBalance: 2993.89 };

  let tenantWallet;
  let corporateTenant;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/corporate-tenants',
      body: {
        legalBusinessName: 'outrun',
        businessRegistrationNumber: 'vacation',
        taxIdentificationNumber: 'close powerfully',
        operatingJurisdiction: 'KE',
        status: 'REJECTED',
        kycStatus: 'REJECTED',
        riskScore: 29959,
        createdAt: '2026-09-21T08:29:17.084Z',
        activatedAt: '2026-09-21T01:55:53.486Z',
      },
    }).then(({ body }) => {
      corporateTenant = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/tenant-wallets+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/tenant-wallets').as('postEntityRequest');
    cy.intercept('DELETE', '/api/tenant-wallets/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/corporate-tenants', {
      statusCode: 200,
      body: [corporateTenant],
    });
  });

  afterEach(() => {
    if (tenantWallet) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/tenant-wallets/${tenantWallet.id}`,
      }).then(() => {
        tenantWallet = undefined;
      });
    }
  });

  afterEach(() => {
    if (corporateTenant) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/corporate-tenants/${corporateTenant.id}`,
      }).then(() => {
        corporateTenant = undefined;
      });
    }
  });

  it('TenantWallets menu should load TenantWallets page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('tenant-wallet');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TenantWallet').should('exist');
    cy.location('pathname').should('eq', tenantWalletPageUrl);
  });

  describe('TenantWallet page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(tenantWalletPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TenantWallet page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${tenantWalletPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('TenantWallet');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', tenantWalletPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/tenant-wallets',
          body: {
            ...tenantWalletSample,
            tenant: corporateTenant,
          },
        }).then(({ body }) => {
          tenantWallet = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/tenant-wallets+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/tenant-wallets?page=0&size=20>; rel="last",<http://localhost/api/tenant-wallets?page=0&size=20>; rel="first"',
              },
              body: [tenantWallet],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(tenantWalletPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TenantWallet page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('tenantWallet');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', tenantWalletPageUrl);
      });

      it('edit button click should load edit TenantWallet page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TenantWallet');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', tenantWalletPageUrl);
      });

      it('edit button click should load edit TenantWallet page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TenantWallet');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', tenantWalletPageUrl);
      });

      it('last delete button click should delete instance of TenantWallet', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('tenantWallet').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', tenantWalletPageUrl);

        tenantWallet = undefined;
      });
    });
  });

  describe('new TenantWallet page', () => {
    beforeEach(() => {
      cy.visit(tenantWalletPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TenantWallet');
    });

    it('should create an instance of TenantWallet', () => {
      cy.get(`[data-cy="currencyCode"]`).type('OMR');
      cy.get(`[data-cy="currencyCode"]`).should('have.value', 'OMR');

      cy.get(`[data-cy="availableBalance"]`).type('30157.46');
      cy.get(`[data-cy="availableBalance"]`).should('have.value', '30157.46');

      cy.get(`[data-cy="lockedBalance"]`).type('30740.84');
      cy.get(`[data-cy="lockedBalance"]`).should('have.value', '30740.84');

      cy.get(`[data-cy="tenant"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        tenantWallet = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', tenantWalletPageUrl);
    });
  });
});
