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

describe('TenantDirector e2e test', () => {
  const tenantDirectorPageUrl = '/tenant-director';
  let username: string;
  let password: string;
  const tenantDirectorSample = {
    fullName: 'lawful crooked',
    dateOfBirth: '2026-09-21',
    nationality: 'NG',
    identificationType: 'DRIVERS_LICENSE',
    identificationNumber: 'yuck sans',
  };

  let tenantDirector;
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
        legalBusinessName: 'dash swiftly boohoo',
        businessRegistrationNumber: 'quirkily barring',
        taxIdentificationNumber: 'pro until consequently',
        operatingJurisdiction: 'ZA',
        status: 'PENDING_REVIEW',
        kycStatus: 'PENDING',
        riskScore: 30861,
        createdAt: '2026-09-20T21:59:32.078Z',
        activatedAt: '2026-09-21T11:31:28.039Z',
      },
    }).then(({ body }) => {
      corporateTenant = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/tenant-directors+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/tenant-directors').as('postEntityRequest');
    cy.intercept('DELETE', '/api/tenant-directors/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/corporate-tenants', {
      statusCode: 200,
      body: [corporateTenant],
    });
  });

  afterEach(() => {
    if (tenantDirector) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/tenant-directors/${tenantDirector.id}`,
      }).then(() => {
        tenantDirector = undefined;
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

  it('TenantDirectors menu should load TenantDirectors page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('tenant-director');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TenantDirector').should('exist');
    cy.location('pathname').should('eq', tenantDirectorPageUrl);
  });

  describe('TenantDirector page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(tenantDirectorPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TenantDirector page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${tenantDirectorPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('TenantDirector');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', tenantDirectorPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/tenant-directors',
          body: {
            ...tenantDirectorSample,
            tenant: corporateTenant,
          },
        }).then(({ body }) => {
          tenantDirector = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/tenant-directors+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/tenant-directors?page=0&size=20>; rel="last",<http://localhost/api/tenant-directors?page=0&size=20>; rel="first"',
              },
              body: [tenantDirector],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(tenantDirectorPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TenantDirector page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('tenantDirector');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', tenantDirectorPageUrl);
      });

      it('edit button click should load edit TenantDirector page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TenantDirector');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', tenantDirectorPageUrl);
      });

      it('edit button click should load edit TenantDirector page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TenantDirector');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', tenantDirectorPageUrl);
      });

      it('last delete button click should delete instance of TenantDirector', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('tenantDirector').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', tenantDirectorPageUrl);

        tenantDirector = undefined;
      });
    });
  });

  describe('new TenantDirector page', () => {
    beforeEach(() => {
      cy.visit(tenantDirectorPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TenantDirector');
    });

    it('should create an instance of TenantDirector', () => {
      cy.get(`[data-cy="fullName"]`).type('rich');
      cy.get(`[data-cy="fullName"]`).should('have.value', 'rich');

      cy.get(`[data-cy="dateOfBirth"]`).type('2026-09-20');
      cy.get(`[data-cy="dateOfBirth"]`).blur();
      cy.get(`[data-cy="dateOfBirth"]`).should('have.value', '2026-09-20');

      cy.get(`[data-cy="nationality"]`).select('US');

      cy.get(`[data-cy="identificationType"]`).select('NATIONAL_ID');

      cy.get(`[data-cy="identificationNumber"]`).type('accountability restructure');
      cy.get(`[data-cy="identificationNumber"]`).should('have.value', 'accountability restructure');

      cy.get(`[data-cy="tenant"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        tenantDirector = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', tenantDirectorPageUrl);
    });
  });
});
