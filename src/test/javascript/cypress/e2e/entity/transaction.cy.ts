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

describe('Transaction e2e test', () => {
  const transactionPageUrl = '/transaction';
  let username: string;
  let password: string;
  const transactionSample = {
    reference: 'ha behind incinerate',
    status: 'FAILED',
    amount: 20363.59,
    feeAmount: 10539.42,
    netAmount: 10085.39,
    currencyCode: 'KRW',
    countryCode: 'UM',
    paymentMethodCode: 'once obedient absent',
    idempotencyKey: 'within',
    createdAt: '2026-09-20T17:57:17.456Z',
  };

  let transaction;
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
        legalBusinessName: 'dishonor blight before',
        businessRegistrationNumber: 'nor heartfelt quarterly',
        taxIdentificationNumber: 'provided meanwhile distorted',
        operatingJurisdiction: 'NG',
        status: 'SUSPENDED',
        kycStatus: 'PENDING',
        riskScore: 18953,
        createdAt: '2026-09-21T15:10:32.136Z',
        activatedAt: '2026-09-21T07:13:55.245Z',
      },
    }).then(({ body }) => {
      corporateTenant = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/transactions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/transactions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/transactions/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/refunds', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/corporate-tenants', {
      statusCode: 200,
      body: [corporateTenant],
    });
  });

  afterEach(() => {
    if (transaction) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/transactions/${transaction.id}`,
      }).then(() => {
        transaction = undefined;
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

  it('Transactions menu should load Transactions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('transaction');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Transaction').should('exist');
    cy.location('pathname').should('eq', transactionPageUrl);
  });

  describe('Transaction page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(transactionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Transaction page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${transactionPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('Transaction');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', transactionPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/transactions',
          body: {
            ...transactionSample,
            tenant: corporateTenant,
          },
        }).then(({ body }) => {
          transaction = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/transactions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/transactions?page=0&size=20>; rel="last",<http://localhost/api/transactions?page=0&size=20>; rel="first"',
              },
              body: [transaction],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(transactionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Transaction page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('transaction');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', transactionPageUrl);
      });

      it('edit button click should load edit Transaction page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Transaction');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', transactionPageUrl);
      });

      it('edit button click should load edit Transaction page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Transaction');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', transactionPageUrl);
      });

      it('last delete button click should delete instance of Transaction', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('transaction').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', transactionPageUrl);

        transaction = undefined;
      });
    });
  });

  describe('new Transaction page', () => {
    beforeEach(() => {
      cy.visit(transactionPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Transaction');
    });

    it('should create an instance of Transaction', () => {
      cy.get(`[data-cy="reference"]`).type('at hm aboard');
      cy.get(`[data-cy="reference"]`).should('have.value', 'at hm aboard');

      cy.get(`[data-cy="tenantReference"]`).type('negotiation colorfully');
      cy.get(`[data-cy="tenantReference"]`).should('have.value', 'negotiation colorfully');

      cy.get(`[data-cy="status"]`).select('FAILED');

      cy.get(`[data-cy="amount"]`).type('31948.48');
      cy.get(`[data-cy="amount"]`).should('have.value', '31948.48');

      cy.get(`[data-cy="feeAmount"]`).type('29741.38');
      cy.get(`[data-cy="feeAmount"]`).should('have.value', '29741.38');

      cy.get(`[data-cy="netAmount"]`).type('11843.93');
      cy.get(`[data-cy="netAmount"]`).should('have.value', '11843.93');

      cy.get(`[data-cy="currencyCode"]`).type('TND');
      cy.get(`[data-cy="currencyCode"]`).should('have.value', 'TND');

      cy.get(`[data-cy="countryCode"]`).type('MR');
      cy.get(`[data-cy="countryCode"]`).should('have.value', 'MR');

      cy.get(`[data-cy="paymentMethodCode"]`).type('catalyze');
      cy.get(`[data-cy="paymentMethodCode"]`).should('have.value', 'catalyze');

      cy.get(`[data-cy="idempotencyKey"]`).type('fit altruistic');
      cy.get(`[data-cy="idempotencyKey"]`).should('have.value', 'fit altruistic');

      cy.get(`[data-cy="customerEmail"]`).type('via or');
      cy.get(`[data-cy="customerEmail"]`).should('have.value', 'via or');

      cy.get(`[data-cy="customerPhone"]`).type('fax');
      cy.get(`[data-cy="customerPhone"]`).should('have.value', 'fax');

      cy.get(`[data-cy="createdAt"]`).type('2026-09-20T18:36');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2026-09-20T18:36');

      cy.get(`[data-cy="completedAt"]`).type('2026-09-21T00:50');
      cy.get(`[data-cy="completedAt"]`).blur();
      cy.get(`[data-cy="completedAt"]`).should('have.value', '2026-09-21T00:50');

      cy.get(`[data-cy="tenant"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        transaction = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', transactionPageUrl);
    });
  });
});
