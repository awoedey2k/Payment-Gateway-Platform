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

describe('SettlementBatch e2e test', () => {
  const settlementBatchPageUrl = '/settlement-batch';
  let username: string;
  let password: string;
  const settlementBatchSample = {
    reference: 'confirm famously',
    status: 'SCHEDULED',
    totalAmount: 18670.25,
    currencyCode: 'BIF',
    scheduledAt: '2026-09-20T21:43:54.885Z',
  };

  let settlementBatch;
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
        legalBusinessName: 'swine unlike excepting',
        businessRegistrationNumber: 'bah why',
        taxIdentificationNumber: 'messy',
        operatingJurisdiction: 'GH',
        status: 'CLOSED',
        kycStatus: 'MANUAL_REVIEW',
        riskScore: 518,
        createdAt: '2026-09-21T17:04:01.881Z',
        activatedAt: '2026-09-20T19:22:39.503Z',
      },
    }).then(({ body }) => {
      corporateTenant = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/settlement-batches+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/settlement-batches').as('postEntityRequest');
    cy.intercept('DELETE', '/api/settlement-batches/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/corporate-tenants', {
      statusCode: 200,
      body: [corporateTenant],
    });
  });

  afterEach(() => {
    if (settlementBatch) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/settlement-batches/${settlementBatch.id}`,
      }).then(() => {
        settlementBatch = undefined;
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

  it('SettlementBatches menu should load SettlementBatches page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('settlement-batch');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('SettlementBatch').should('exist');
    cy.location('pathname').should('eq', settlementBatchPageUrl);
  });

  describe('SettlementBatch page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(settlementBatchPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create SettlementBatch page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${settlementBatchPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('SettlementBatch');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', settlementBatchPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/settlement-batches',
          body: {
            ...settlementBatchSample,
            tenant: corporateTenant,
          },
        }).then(({ body }) => {
          settlementBatch = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/settlement-batches+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/settlement-batches?page=0&size=20>; rel="last",<http://localhost/api/settlement-batches?page=0&size=20>; rel="first"',
              },
              body: [settlementBatch],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(settlementBatchPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details SettlementBatch page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('settlementBatch');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', settlementBatchPageUrl);
      });

      it('edit button click should load edit SettlementBatch page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SettlementBatch');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', settlementBatchPageUrl);
      });

      it('edit button click should load edit SettlementBatch page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SettlementBatch');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', settlementBatchPageUrl);
      });

      it('last delete button click should delete instance of SettlementBatch', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('settlementBatch').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', settlementBatchPageUrl);

        settlementBatch = undefined;
      });
    });
  });

  describe('new SettlementBatch page', () => {
    beforeEach(() => {
      cy.visit(settlementBatchPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('SettlementBatch');
    });

    it('should create an instance of SettlementBatch', () => {
      cy.get(`[data-cy="reference"]`).type('less');
      cy.get(`[data-cy="reference"]`).should('have.value', 'less');

      cy.get(`[data-cy="status"]`).select('FAILED');

      cy.get(`[data-cy="totalAmount"]`).type('3734.34');
      cy.get(`[data-cy="totalAmount"]`).should('have.value', '3734.34');

      cy.get(`[data-cy="currencyCode"]`).type('THB');
      cy.get(`[data-cy="currencyCode"]`).should('have.value', 'THB');

      cy.get(`[data-cy="scheduledAt"]`).type('2026-09-21T14:12');
      cy.get(`[data-cy="scheduledAt"]`).blur();
      cy.get(`[data-cy="scheduledAt"]`).should('have.value', '2026-09-21T14:12');

      cy.get(`[data-cy="completedAt"]`).type('2026-09-20T19:48');
      cy.get(`[data-cy="completedAt"]`).blur();
      cy.get(`[data-cy="completedAt"]`).should('have.value', '2026-09-20T19:48');

      cy.get(`[data-cy="tenant"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        settlementBatch = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', settlementBatchPageUrl);
    });
  });
});
