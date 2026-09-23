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

describe('WebhookSubscription e2e test', () => {
  const webhookSubscriptionPageUrl = '/webhook-subscription';
  let username: string;
  let password: string;
  const webhookSubscriptionSample = { targetUrl: 'within yin camouflage', secretHash: 'silently quaver', isActive: true };

  let webhookSubscription;
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
        legalBusinessName: 'since brr scornful',
        businessRegistrationNumber: 'telescope sun around',
        taxIdentificationNumber: 'disinherit',
        operatingJurisdiction: 'GB',
        status: 'PENDING_REVIEW',
        kycStatus: 'MANUAL_REVIEW',
        riskScore: 21188,
        createdAt: '2026-09-21T03:22:21.738Z',
        activatedAt: '2026-09-20T17:57:32.674Z',
      },
    }).then(({ body }) => {
      corporateTenant = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/webhook-subscriptions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/webhook-subscriptions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/webhook-subscriptions/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/corporate-tenants', {
      statusCode: 200,
      body: [corporateTenant],
    });
  });

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

  it('WebhookSubscriptions menu should load WebhookSubscriptions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('webhook-subscription');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('WebhookSubscription').should('exist');
    cy.location('pathname').should('eq', webhookSubscriptionPageUrl);
  });

  describe('WebhookSubscription page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(webhookSubscriptionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create WebhookSubscription page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${webhookSubscriptionPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('WebhookSubscription');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', webhookSubscriptionPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/webhook-subscriptions',
          body: {
            ...webhookSubscriptionSample,
            tenant: corporateTenant,
          },
        }).then(({ body }) => {
          webhookSubscription = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/webhook-subscriptions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/webhook-subscriptions?page=0&size=20>; rel="last",<http://localhost/api/webhook-subscriptions?page=0&size=20>; rel="first"',
              },
              body: [webhookSubscription],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(webhookSubscriptionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details WebhookSubscription page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('webhookSubscription');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', webhookSubscriptionPageUrl);
      });

      it('edit button click should load edit WebhookSubscription page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('WebhookSubscription');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', webhookSubscriptionPageUrl);
      });

      it('edit button click should load edit WebhookSubscription page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('WebhookSubscription');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', webhookSubscriptionPageUrl);
      });

      it('last delete button click should delete instance of WebhookSubscription', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('webhookSubscription').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', webhookSubscriptionPageUrl);

        webhookSubscription = undefined;
      });
    });
  });

  describe('new WebhookSubscription page', () => {
    beforeEach(() => {
      cy.visit(webhookSubscriptionPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('WebhookSubscription');
    });

    it('should create an instance of WebhookSubscription', () => {
      cy.get(`[data-cy="targetUrl"]`).type('formamide');
      cy.get(`[data-cy="targetUrl"]`).should('have.value', 'formamide');

      cy.get(`[data-cy="secretHash"]`).type('because inasmuch sideboard');
      cy.get(`[data-cy="secretHash"]`).should('have.value', 'because inasmuch sideboard');

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(`[data-cy="tenant"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        webhookSubscription = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', webhookSubscriptionPageUrl);
    });
  });
});
