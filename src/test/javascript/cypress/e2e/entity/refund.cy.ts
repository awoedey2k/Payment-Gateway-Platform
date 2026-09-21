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

describe('Refund e2e test', () => {
  const refundPageUrl = '/refund';
  let username: string;
  let password: string;
  // const refundSample = {"reference":"vamoose meh uniform","amount":30645.23,"reason":"CUSTOMER_REQUEST_RETURN","status":"FAILED","createdAt":"2026-09-21T09:45:22.304Z"};

  let refund;
  // let transaction;

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
      url: '/api/transactions',
      body: {"reference":"fixed","tenantReference":"covenant obnoxiously","status":"SUCCESSFUL","amount":23562.81,"feeAmount":24131.63,"netAmount":31815.09,"currencyCode":"RUB","countryCode":"JE","paymentMethodCode":"sadly except","idempotencyKey":"hm","customerEmail":"sheepishly yum atrium","customerPhone":"individual worth","createdAt":"2026-09-21T02:27:09.936Z","completedAt":"2026-09-21T12:25:50.197Z"},
    }).then(({ body }) => {
      transaction = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/refunds+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/refunds').as('postEntityRequest');
    cy.intercept('DELETE', '/api/refunds/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/transactions', {
      statusCode: 200,
      body: [transaction],
    });

  });
   */

  afterEach(() => {
    if (refund) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/refunds/${refund.id}`,
      }).then(() => {
        refund = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
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
   */

  it('Refunds menu should load Refunds page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('refund');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Refund').should('exist');
    cy.location('pathname').should('eq', refundPageUrl);
  });

  describe('Refund page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(refundPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Refund page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${refundPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('Refund');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', refundPageUrl);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/refunds',
          body: {
            ...refundSample,
            transaction: transaction,
          },
        }).then(({ body }) => {
          refund = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/refunds+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/refunds?page=0&size=20>; rel="last",<http://localhost/api/refunds?page=0&size=20>; rel="first"',
              },
              body: [refund],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(refundPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(refundPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Refund page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('refund');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', refundPageUrl);
      });

      it('edit button click should load edit Refund page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Refund');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', refundPageUrl);
      });

      it('edit button click should load edit Refund page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Refund');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', refundPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of Refund', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('refund').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', refundPageUrl);

        refund = undefined;
      });
    });
  });

  describe('new Refund page', () => {
    beforeEach(() => {
      cy.visit(refundPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Refund');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of Refund', () => {
      cy.get(`[data-cy="reference"]`).type('pfft silently');
      cy.get(`[data-cy="reference"]`).should('have.value', 'pfft silently');

      cy.get(`[data-cy="amount"]`).type('31998.5');
      cy.get(`[data-cy="amount"]`).should('have.value', '31998.5');

      cy.get(`[data-cy="reason"]`).select('OTHER');

      cy.get(`[data-cy="status"]`).select('PENDING');

      cy.get(`[data-cy="createdAt"]`).type('2026-09-21T12:43');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2026-09-21T12:43');

      cy.get(`[data-cy="transaction"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        refund = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', refundPageUrl);
    });
  });
});
