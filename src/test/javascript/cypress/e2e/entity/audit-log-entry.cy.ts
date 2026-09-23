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

describe('AuditLogEntry e2e test', () => {
  const auditLogEntryPageUrl = '/audit-log-entry';
  let username: string;
  let password: string;
  const auditLogEntrySample = {
    actorType: 'STAFF_USER',
    actorId: 'phew that',
    action: 'indeed',
    entityType: 'crooked',
    entityId: 'linear',
    entryHash: 'save forsaken major',
    recordedAt: '2026-09-21T03:55:19.662Z',
  };

  let auditLogEntry;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/audit-log-entries+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/audit-log-entries').as('postEntityRequest');
    cy.intercept('DELETE', '/api/audit-log-entries/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (auditLogEntry) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/audit-log-entries/${auditLogEntry.id}`,
      }).then(() => {
        auditLogEntry = undefined;
      });
    }
  });

  it('AuditLogEntries menu should load AuditLogEntries page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('audit-log-entry');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('AuditLogEntry').should('exist');
    cy.location('pathname').should('eq', auditLogEntryPageUrl);
  });

  describe('AuditLogEntry page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(auditLogEntryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create AuditLogEntry page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${auditLogEntryPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('AuditLogEntry');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', auditLogEntryPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/audit-log-entries',
          body: auditLogEntrySample,
        }).then(({ body }) => {
          auditLogEntry = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/audit-log-entries+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/audit-log-entries?page=0&size=20>; rel="last",<http://localhost/api/audit-log-entries?page=0&size=20>; rel="first"',
              },
              body: [auditLogEntry],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(auditLogEntryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details AuditLogEntry page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('auditLogEntry');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', auditLogEntryPageUrl);
      });

      it('edit button click should load edit AuditLogEntry page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AuditLogEntry');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', auditLogEntryPageUrl);
      });

      it('edit button click should load edit AuditLogEntry page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AuditLogEntry');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', auditLogEntryPageUrl);
      });

      it('last delete button click should delete instance of AuditLogEntry', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('auditLogEntry').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', auditLogEntryPageUrl);

        auditLogEntry = undefined;
      });
    });
  });

  describe('new AuditLogEntry page', () => {
    beforeEach(() => {
      cy.visit(auditLogEntryPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('AuditLogEntry');
    });

    it('should create an instance of AuditLogEntry', () => {
      cy.get(`[data-cy="actorType"]`).select('SYSTEM');

      cy.get(`[data-cy="actorId"]`).type('oh fashion meanwhile');
      cy.get(`[data-cy="actorId"]`).should('have.value', 'oh fashion meanwhile');

      cy.get(`[data-cy="action"]`).type('outdo');
      cy.get(`[data-cy="action"]`).should('have.value', 'outdo');

      cy.get(`[data-cy="entityType"]`).type('ack whose split');
      cy.get(`[data-cy="entityType"]`).should('have.value', 'ack whose split');

      cy.get(`[data-cy="entityId"]`).type('jovially excited gosh');
      cy.get(`[data-cy="entityId"]`).should('have.value', 'jovially excited gosh');

      cy.get(`[data-cy="previousHash"]`).type('during writ');
      cy.get(`[data-cy="previousHash"]`).should('have.value', 'during writ');

      cy.get(`[data-cy="entryHash"]`).type('conversation wearily whisper');
      cy.get(`[data-cy="entryHash"]`).should('have.value', 'conversation wearily whisper');

      cy.get(`[data-cy="recordedAt"]`).type('2026-09-21T12:17');
      cy.get(`[data-cy="recordedAt"]`).blur();
      cy.get(`[data-cy="recordedAt"]`).should('have.value', '2026-09-21T12:17');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        auditLogEntry = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', auditLogEntryPageUrl);
    });
  });
});
