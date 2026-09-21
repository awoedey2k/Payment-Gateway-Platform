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

describe('ApiKey e2e test', () => {
  const apiKeyPageUrl = '/api-key';
  let username: string;
  let password: string;
  const apiKeySample = {
    keyPrefix: 'tremendously gosh rubric',
    keyHash: 'on square',
    environment: 'TEST',
    isActive: false,
    issuedAt: '2026-09-21T03:59:37.147Z',
  };

  let apiKey;
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
        legalBusinessName: 'hammock headline',
        businessRegistrationNumber: 'fervently deceivingly',
        taxIdentificationNumber: 'successfully overplay',
        operatingJurisdiction: 'NG',
        status: 'ACTIVE',
        kycStatus: 'PENDING',
        riskScore: 8839,
        createdAt: '2026-09-21T00:50:32.204Z',
        activatedAt: '2026-09-21T08:28:47.340Z',
      },
    }).then(({ body }) => {
      corporateTenant = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/api-keys+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/api-keys').as('postEntityRequest');
    cy.intercept('DELETE', '/api/api-keys/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/corporate-tenants', {
      statusCode: 200,
      body: [corporateTenant],
    });
  });

  afterEach(() => {
    if (apiKey) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/api-keys/${apiKey.id}`,
      }).then(() => {
        apiKey = undefined;
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

  it('ApiKeys menu should load ApiKeys page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('api-key');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ApiKey').should('exist');
    cy.location('pathname').should('eq', apiKeyPageUrl);
  });

  describe('ApiKey page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(apiKeyPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ApiKey page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${apiKeyPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('ApiKey');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', apiKeyPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/api-keys',
          body: {
            ...apiKeySample,
            tenant: corporateTenant,
          },
        }).then(({ body }) => {
          apiKey = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/api-keys+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/api-keys?page=0&size=20>; rel="last",<http://localhost/api/api-keys?page=0&size=20>; rel="first"',
              },
              body: [apiKey],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(apiKeyPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ApiKey page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('apiKey');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', apiKeyPageUrl);
      });

      it('edit button click should load edit ApiKey page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ApiKey');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', apiKeyPageUrl);
      });

      it('edit button click should load edit ApiKey page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ApiKey');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', apiKeyPageUrl);
      });

      it('last delete button click should delete instance of ApiKey', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('apiKey').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', apiKeyPageUrl);

        apiKey = undefined;
      });
    });
  });

  describe('new ApiKey page', () => {
    beforeEach(() => {
      cy.visit(apiKeyPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ApiKey');
    });

    it('should create an instance of ApiKey', () => {
      cy.get(`[data-cy="keyPrefix"]`).type('against nautical');
      cy.get(`[data-cy="keyPrefix"]`).should('have.value', 'against nautical');

      cy.get(`[data-cy="keyHash"]`).type('epic psst');
      cy.get(`[data-cy="keyHash"]`).should('have.value', 'epic psst');

      cy.get(`[data-cy="environment"]`).select('LIVE');

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(`[data-cy="issuedAt"]`).type('2026-09-21T04:41');
      cy.get(`[data-cy="issuedAt"]`).blur();
      cy.get(`[data-cy="issuedAt"]`).should('have.value', '2026-09-21T04:41');

      cy.get(`[data-cy="revokedAt"]`).type('2026-09-21T07:43');
      cy.get(`[data-cy="revokedAt"]`).blur();
      cy.get(`[data-cy="revokedAt"]`).should('have.value', '2026-09-21T07:43');

      cy.get(`[data-cy="graceExpiresAt"]`).type('2026-09-21T11:07');
      cy.get(`[data-cy="graceExpiresAt"]`).blur();
      cy.get(`[data-cy="graceExpiresAt"]`).should('have.value', '2026-09-21T11:07');

      cy.get(`[data-cy="tenant"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        apiKey = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', apiKeyPageUrl);
    });
  });
});
