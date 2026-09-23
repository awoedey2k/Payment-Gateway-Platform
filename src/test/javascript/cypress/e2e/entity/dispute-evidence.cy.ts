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

describe('DisputeEvidence e2e test', () => {
  const disputeEvidencePageUrl = '/dispute-evidence';
  let username: string;
  let password: string;
  // const disputeEvidenceSample = {"evidenceType":"CUSTOMER_COMMUNICATION","fileName":"pertinent beyond","fileUrl":"apud","fileSizeBytes":298,"mimeType":"sun meh boom","sha256Checksum":"roughly privilege anti","uploadedAt":"2026-09-21T16:18:38.148Z"};

  let disputeEvidence;
  // let dispute;

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
      url: '/api/disputes',
      body: {"caseReference":"enrich","amount":3013.03,"currencyCode":"CVE","reasonCode":"heartfelt gown","reasonDescription":"opposite","status":"OPEN","dueDate":"2026-09-21T15:21:27.365Z","evidenceSubmittedAt":"2026-09-20T23:03:02.690Z","resolvedAt":"2026-09-20T19:49:36.728Z"},
    }).then(({ body }) => {
      dispute = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/dispute-evidences+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/dispute-evidences').as('postEntityRequest');
    cy.intercept('DELETE', '/api/dispute-evidences/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/disputes', {
      statusCode: 200,
      body: [dispute],
    });

  });
   */

  afterEach(() => {
    if (disputeEvidence) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/dispute-evidences/${disputeEvidence.id}`,
      }).then(() => {
        disputeEvidence = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
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
   */

  it('DisputeEvidences menu should load DisputeEvidences page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('dispute-evidence');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('DisputeEvidence').should('exist');
    cy.location('pathname').should('eq', disputeEvidencePageUrl);
  });

  describe('DisputeEvidence page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(disputeEvidencePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create DisputeEvidence page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${disputeEvidencePageUrl}/new`);
        cy.getEntityCreateUpdateHeading('DisputeEvidence');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', disputeEvidencePageUrl);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/dispute-evidences',
          body: {
            ...disputeEvidenceSample,
            dispute: dispute,
          },
        }).then(({ body }) => {
          disputeEvidence = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/dispute-evidences+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/dispute-evidences?page=0&size=20>; rel="last",<http://localhost/api/dispute-evidences?page=0&size=20>; rel="first"',
              },
              body: [disputeEvidence],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(disputeEvidencePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(disputeEvidencePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details DisputeEvidence page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('disputeEvidence');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', disputeEvidencePageUrl);
      });

      it('edit button click should load edit DisputeEvidence page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DisputeEvidence');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', disputeEvidencePageUrl);
      });

      it('edit button click should load edit DisputeEvidence page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DisputeEvidence');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', disputeEvidencePageUrl);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of DisputeEvidence', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('disputeEvidence').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', disputeEvidencePageUrl);

        disputeEvidence = undefined;
      });
    });
  });

  describe('new DisputeEvidence page', () => {
    beforeEach(() => {
      cy.visit(disputeEvidencePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('DisputeEvidence');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of DisputeEvidence', () => {
      cy.get(`[data-cy="evidenceType"]`).select('REFUND_POLICY');

      cy.get(`[data-cy="fileName"]`).type('spirited');
      cy.get(`[data-cy="fileName"]`).should('have.value', 'spirited');

      cy.get(`[data-cy="fileUrl"]`).type('meh');
      cy.get(`[data-cy="fileUrl"]`).should('have.value', 'meh');

      cy.get(`[data-cy="fileSizeBytes"]`).type('17661');
      cy.get(`[data-cy="fileSizeBytes"]`).should('have.value', '17661');

      cy.get(`[data-cy="mimeType"]`).type('similar valiantly');
      cy.get(`[data-cy="mimeType"]`).should('have.value', 'similar valiantly');

      cy.get(`[data-cy="sha256Checksum"]`).type('exacerbate after louse');
      cy.get(`[data-cy="sha256Checksum"]`).should('have.value', 'exacerbate after louse');

      cy.get(`[data-cy="uploadedAt"]`).type('2026-09-21T01:50');
      cy.get(`[data-cy="uploadedAt"]`).blur();
      cy.get(`[data-cy="uploadedAt"]`).should('have.value', '2026-09-21T01:50');

      cy.get(`[data-cy="dispute"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        disputeEvidence = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', disputeEvidencePageUrl);
    });
  });
});
