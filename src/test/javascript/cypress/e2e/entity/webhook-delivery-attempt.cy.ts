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

describe('WebhookDeliveryAttempt e2e test', () => {
  const webhookDeliveryAttemptPageUrl = '/webhook-delivery-attempt';
  let username: string;
  let password: string;
  // const webhookDeliveryAttemptSample = {"eventType":"blaring","status":"RETRYING","attemptNumber":11523,"attemptedAt":"2026-09-21T13:41:12.415Z"};

  let webhookDeliveryAttempt;
  // let webhookSubscription;

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
      url: '/api/webhook-subscriptions',
      body: {"targetUrl":"divine catalog","secretHash":"upwardly excluding delightfully","isActive":true},
    }).then(({ body }) => {
      webhookSubscription = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/webhook-delivery-attempts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/webhook-delivery-attempts').as('postEntityRequest');
    cy.intercept('DELETE', '/api/webhook-delivery-attempts/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/webhook-subscriptions', {
      statusCode: 200,
      body: [webhookSubscription],
    });

  });
   */

  afterEach(() => {
    if (webhookDeliveryAttempt) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/webhook-delivery-attempts/${webhookDeliveryAttempt.id}`,
      }).then(() => {
        webhookDeliveryAttempt = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (webhookSubscription) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/webhook-subscriptions/${webhookSubscription.id}`,
      }).then(() => {
        webhookSubscription = undefined;
      });
    }
  });
   */

  it('WebhookDeliveryAttempts menu should load WebhookDeliveryAttempts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('webhook-delivery-attempt');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('WebhookDeliveryAttempt').should('exist');
    cy.location('pathname').should('eq', webhookDeliveryAttemptPageUrl);
  });

  describe('WebhookDeliveryAttempt page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(webhookDeliveryAttemptPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create WebhookDeliveryAttempt page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${webhookDeliveryAttemptPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('WebhookDeliveryAttempt');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', webhookDeliveryAttemptPageUrl);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/webhook-delivery-attempts',
          body: {
            ...webhookDeliveryAttemptSample,
            subscription: webhookSubscription,
          },
        }).then(({ body }) => {
          webhookDeliveryAttempt = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/webhook-delivery-attempts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/webhook-delivery-attempts?page=0&size=20>; rel="last",<http://localhost/api/webhook-delivery-attempts?page=0&size=20>; rel="first"',
              },
              body: [webhookDeliveryAttempt],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(webhookDeliveryAttemptPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(webhookDeliveryAttemptPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details WebhookDeliveryAttempt page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('webhookDeliveryAttempt');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', webhookDeliveryAttemptPageUrl);
      });

      it('edit button click should load edit WebhookDeliveryAttempt page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('WebhookDeliveryAttempt');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', webhookDeliveryAttemptPageUrl);
      });

      it('edit button click should load edit WebhookDeliveryAttempt page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('WebhookDeliveryAttempt');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', webhookDeliveryAttemptPageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of WebhookDeliveryAttempt', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('webhookDeliveryAttempt').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', webhookDeliveryAttemptPageUrl);

        webhookDeliveryAttempt = undefined;
      });
    });
  });

  describe('new WebhookDeliveryAttempt page', () => {
    beforeEach(() => {
      cy.visit(webhookDeliveryAttemptPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('WebhookDeliveryAttempt');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of WebhookDeliveryAttempt', () => {
      cy.get(`[data-cy="eventType"]`).type('mmm');
      cy.get(`[data-cy="eventType"]`).should('have.value', 'mmm');

      cy.get(`[data-cy="status"]`).select('RETRYING');

      cy.get(`[data-cy="httpStatusCode"]`).type('11086');
      cy.get(`[data-cy="httpStatusCode"]`).should('have.value', '11086');

      cy.get(`[data-cy="attemptNumber"]`).type('24727');
      cy.get(`[data-cy="attemptNumber"]`).should('have.value', '24727');

      cy.get(`[data-cy="attemptedAt"]`).type('2026-09-20T22:44');
      cy.get(`[data-cy="attemptedAt"]`).blur();
      cy.get(`[data-cy="attemptedAt"]`).should('have.value', '2026-09-20T22:44');

      cy.get(`[data-cy="subscription"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        webhookDeliveryAttempt = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', webhookDeliveryAttemptPageUrl);
    });
  });
});
