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

describe('Dispute e2e test', () => {
  const disputePageUrl = '/dispute';
  let username: string;
  let password: string;
  // const disputeSample = {"caseReference":"fine midst","amount":9543.85,"currencyCode":"RWF","reasonCode":"canter beyond","status":"EVIDENCE_SUBMITTED","dueDate":"2026-09-21T05:22:24.556Z"};

  let dispute;
  // let corporateTenant;
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
      url: '/api/corporate-tenants',
      body: {"legalBusinessName":"hence er","businessRegistrationNumber":"meh zowie","taxIdentificationNumber":"vastly unbearably","operatingJurisdiction":"ZA","status":"PENDING_REVIEW","kycStatus":"MANUAL_REVIEW","riskScore":17491,"createdAt":"2026-09-21T16:09:36.908Z","activatedAt":"2026-09-20T19:46:30.129Z"},
    }).then(({ body }) => {
      corporateTenant = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/transactions',
      body: {"reference":"idolized yieldingly","tenantReference":"blissfully","status":"REVERSED","amount":1843.42,"feeAmount":22841.86,"netAmount":16303.23,"currencyCode":"SOS","countryCode":"GP","paymentMethodCode":"yowza inventory","idempotencyKey":"iridescence sailor lighthearted","customerEmail":"excluding","customerPhone":"ouch","createdAt":"2026-09-20T19:34:12.666Z","completedAt":"2026-09-21T13:16:28.687Z"},
    }).then(({ body }) => {
      transaction = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/disputes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/disputes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/disputes/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/corporate-tenants', {
      statusCode: 200,
      body: [corporateTenant],
    });

    cy.intercept('GET', '/api/transactions', {
      statusCode: 200,
      body: [transaction],
    });

  });
   */

  afterEach(() => {
    if (dispute) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/disputes/${dispute.id}`,
      }).then(() => {
        dispute = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (corporateTenant) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/corporate-tenants/${corporateTenant.id}`,
      }).then(() => {
        corporateTenant = undefined;
      });
    }
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

  it('Disputes menu should load Disputes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('dispute');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Dispute').should('exist');
    cy.location('pathname').should('eq', disputePageUrl);
  });

  describe('Dispute page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(disputePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Dispute page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${disputePageUrl}/new`);
        cy.getEntityCreateUpdateHeading('Dispute');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', disputePageUrl);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/disputes',
          body: {
            ...disputeSample,
            tenant: corporateTenant,
            transaction: transaction,
          },
        }).then(({ body }) => {
          dispute = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/disputes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/disputes?page=0&size=20>; rel="last",<http://localhost/api/disputes?page=0&size=20>; rel="first"',
              },
              body: [dispute],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(disputePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(disputePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Dispute page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('dispute');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', disputePageUrl);
      });

      it('edit button click should load edit Dispute page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Dispute');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', disputePageUrl);
      });

      it('edit button click should load edit Dispute page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Dispute');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', disputePageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of Dispute', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('dispute').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', disputePageUrl);

        dispute = undefined;
      });
    });
  });

  describe('new Dispute page', () => {
    beforeEach(() => {
      cy.visit(disputePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Dispute');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of Dispute', () => {
      cy.get(`[data-cy="caseReference"]`).type('apud');
      cy.get(`[data-cy="caseReference"]`).should('have.value', 'apud');

      cy.get(`[data-cy="amount"]`).type('11817.42');
      cy.get(`[data-cy="amount"]`).should('have.value', '11817.42');

      cy.get(`[data-cy="currencyCode"]`).type('LBP');
      cy.get(`[data-cy="currencyCode"]`).should('have.value', 'LBP');

      cy.get(`[data-cy="reasonCode"]`).type('officially modulo');
      cy.get(`[data-cy="reasonCode"]`).should('have.value', 'officially modulo');

      cy.get(`[data-cy="reasonDescription"]`).type('who');
      cy.get(`[data-cy="reasonDescription"]`).should('have.value', 'who');

      cy.get(`[data-cy="status"]`).select('CLOSED');

      cy.get(`[data-cy="dueDate"]`).type('2026-09-21T05:37');
      cy.get(`[data-cy="dueDate"]`).blur();
      cy.get(`[data-cy="dueDate"]`).should('have.value', '2026-09-21T05:37');

      cy.get(`[data-cy="evidenceSubmittedAt"]`).type('2026-09-20T17:48');
      cy.get(`[data-cy="evidenceSubmittedAt"]`).blur();
      cy.get(`[data-cy="evidenceSubmittedAt"]`).should('have.value', '2026-09-20T17:48');

      cy.get(`[data-cy="resolvedAt"]`).type('2026-09-21T06:03');
      cy.get(`[data-cy="resolvedAt"]`).blur();
      cy.get(`[data-cy="resolvedAt"]`).should('have.value', '2026-09-21T06:03');

      cy.get(`[data-cy="tenant"]`).select(1);
      cy.get(`[data-cy="transaction"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        dispute = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', disputePageUrl);
    });
  });
});
