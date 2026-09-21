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

describe('CorporateTenant e2e test', () => {
  const corporateTenantPageUrl = '/corporate-tenant';
  let username: string;
  let password: string;
  const corporateTenantSample = {
    legalBusinessName: 'avow rarely',
    businessRegistrationNumber: 'yowza',
    taxIdentificationNumber: 'victoriously blossom instantly',
    operatingJurisdiction: 'KE',
    status: 'REJECTED',
    kycStatus: 'PENDING',
    createdAt: '2026-09-21T04:17:39.733Z',
  };

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
    cy.intercept('GET', '/api/corporate-tenants+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/corporate-tenants').as('postEntityRequest');
    cy.intercept('DELETE', '/api/corporate-tenants/*').as('deleteEntityRequest');
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

  it('CorporateTenants menu should load CorporateTenants page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('corporate-tenant');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CorporateTenant').should('exist');
    cy.location('pathname').should('eq', corporateTenantPageUrl);
  });

  describe('CorporateTenant page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(corporateTenantPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CorporateTenant page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${corporateTenantPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('CorporateTenant');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', corporateTenantPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/corporate-tenants',
          body: corporateTenantSample,
        }).then(({ body }) => {
          corporateTenant = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/corporate-tenants+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/corporate-tenants?page=0&size=20>; rel="last",<http://localhost/api/corporate-tenants?page=0&size=20>; rel="first"',
              },
              body: [corporateTenant],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(corporateTenantPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details CorporateTenant page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('corporateTenant');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', corporateTenantPageUrl);
      });

      it('edit button click should load edit CorporateTenant page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CorporateTenant');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', corporateTenantPageUrl);
      });

      it('edit button click should load edit CorporateTenant page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CorporateTenant');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', corporateTenantPageUrl);
      });

      it('last delete button click should delete instance of CorporateTenant', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('corporateTenant').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', corporateTenantPageUrl);

        corporateTenant = undefined;
      });
    });
  });

  describe('new CorporateTenant page', () => {
    beforeEach(() => {
      cy.visit(corporateTenantPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('CorporateTenant');
    });

    it('should create an instance of CorporateTenant', () => {
      cy.get(`[data-cy="legalBusinessName"]`).type('knowledgeably');
      cy.get(`[data-cy="legalBusinessName"]`).should('have.value', 'knowledgeably');

      cy.get(`[data-cy="businessRegistrationNumber"]`).type('dreamily');
      cy.get(`[data-cy="businessRegistrationNumber"]`).should('have.value', 'dreamily');

      cy.get(`[data-cy="taxIdentificationNumber"]`).type('sequester harp pitiful');
      cy.get(`[data-cy="taxIdentificationNumber"]`).should('have.value', 'sequester harp pitiful');

      cy.get(`[data-cy="operatingJurisdiction"]`).select('GB');

      cy.get(`[data-cy="status"]`).select('PENDING_REVIEW');

      cy.get(`[data-cy="kycStatus"]`).select('MANUAL_REVIEW');

      cy.get(`[data-cy="riskScore"]`).type('4002');
      cy.get(`[data-cy="riskScore"]`).should('have.value', '4002');

      cy.get(`[data-cy="createdAt"]`).type('2026-09-21T00:33');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2026-09-21T00:33');

      cy.get(`[data-cy="activatedAt"]`).type('2026-09-20T17:44');
      cy.get(`[data-cy="activatedAt"]`).blur();
      cy.get(`[data-cy="activatedAt"]`).should('have.value', '2026-09-20T17:44');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        corporateTenant = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', corporateTenantPageUrl);
    });
  });
});
