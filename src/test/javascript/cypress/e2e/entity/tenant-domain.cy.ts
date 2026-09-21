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

describe('TenantDomain e2e test', () => {
  const tenantDomainPageUrl = '/tenant-domain';
  let username: string;
  let password: string;
  const tenantDomainSample = { customDomain: 'outside ah', isVerified: true };

  let tenantDomain;
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
        legalBusinessName: 'obnoxiously youthfully',
        businessRegistrationNumber: 'so saw',
        taxIdentificationNumber: 'minus nor prime',
        operatingJurisdiction: 'US',
        status: 'SUSPENDED',
        kycStatus: 'PENDING',
        riskScore: 9048,
        createdAt: '2026-09-21T11:28:32.416Z',
        activatedAt: '2026-09-21T14:32:58.820Z',
      },
    }).then(({ body }) => {
      corporateTenant = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/tenant-domains+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/tenant-domains').as('postEntityRequest');
    cy.intercept('DELETE', '/api/tenant-domains/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/corporate-tenants', {
      statusCode: 200,
      body: [corporateTenant],
    });
  });

  afterEach(() => {
    if (tenantDomain) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/tenant-domains/${tenantDomain.id}`,
      }).then(() => {
        tenantDomain = undefined;
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

  it('TenantDomains menu should load TenantDomains page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('tenant-domain');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TenantDomain').should('exist');
    cy.location('pathname').should('eq', tenantDomainPageUrl);
  });

  describe('TenantDomain page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(tenantDomainPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TenantDomain page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${tenantDomainPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('TenantDomain');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', tenantDomainPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/tenant-domains',
          body: {
            ...tenantDomainSample,
            tenant: corporateTenant,
          },
        }).then(({ body }) => {
          tenantDomain = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/tenant-domains+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/tenant-domains?page=0&size=20>; rel="last",<http://localhost/api/tenant-domains?page=0&size=20>; rel="first"',
              },
              body: [tenantDomain],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(tenantDomainPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TenantDomain page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('tenantDomain');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', tenantDomainPageUrl);
      });

      it('edit button click should load edit TenantDomain page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TenantDomain');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', tenantDomainPageUrl);
      });

      it('edit button click should load edit TenantDomain page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TenantDomain');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', tenantDomainPageUrl);
      });

      it('last delete button click should delete instance of TenantDomain', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('tenantDomain').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', tenantDomainPageUrl);

        tenantDomain = undefined;
      });
    });
  });

  describe('new TenantDomain page', () => {
    beforeEach(() => {
      cy.visit(tenantDomainPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TenantDomain');
    });

    it('should create an instance of TenantDomain', () => {
      cy.get(`[data-cy="customDomain"]`).type('than');
      cy.get(`[data-cy="customDomain"]`).should('have.value', 'than');

      cy.get(`[data-cy="supportedLocales"]`).type('where however');
      cy.get(`[data-cy="supportedLocales"]`).should('have.value', 'where however');

      cy.get(`[data-cy="isVerified"]`).should('not.be.checked');
      cy.get(`[data-cy="isVerified"]`).click();
      cy.get(`[data-cy="isVerified"]`).should('be.checked');

      cy.get(`[data-cy="tenant"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        tenantDomain = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', tenantDomainPageUrl);
    });
  });
});
