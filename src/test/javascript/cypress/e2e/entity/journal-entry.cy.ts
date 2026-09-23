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

describe('JournalEntry e2e test', () => {
  const journalEntryPageUrl = '/journal-entry';
  let username: string;
  let password: string;
  const journalEntrySample = { reference: 'bowling', description: 'grade frightfully', postedAt: '2026-09-20T23:31:17.892Z' };

  let journalEntry;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/journal-entries+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/journal-entries').as('postEntityRequest');
    cy.intercept('DELETE', '/api/journal-entries/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (journalEntry) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/journal-entries/${journalEntry.id}`,
      }).then(() => {
        journalEntry = undefined;
      });
    }
  });

  it('JournalEntries menu should load JournalEntries page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('journal-entry');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('JournalEntry').should('exist');
    cy.location('pathname').should('eq', journalEntryPageUrl);
  });

  describe('JournalEntry page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(journalEntryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create JournalEntry page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${journalEntryPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('JournalEntry');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', journalEntryPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/journal-entries',
          body: journalEntrySample,
        }).then(({ body }) => {
          journalEntry = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/journal-entries+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/journal-entries?page=0&size=20>; rel="last",<http://localhost/api/journal-entries?page=0&size=20>; rel="first"',
              },
              body: [journalEntry],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(journalEntryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details JournalEntry page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('journalEntry');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', journalEntryPageUrl);
      });

      it('edit button click should load edit JournalEntry page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('JournalEntry');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', journalEntryPageUrl);
      });

      it('edit button click should load edit JournalEntry page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('JournalEntry');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', journalEntryPageUrl);
      });

      it('last delete button click should delete instance of JournalEntry', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('journalEntry').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', journalEntryPageUrl);

        journalEntry = undefined;
      });
    });
  });

  describe('new JournalEntry page', () => {
    beforeEach(() => {
      cy.visit(journalEntryPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('JournalEntry');
    });

    it('should create an instance of JournalEntry', () => {
      cy.get(`[data-cy="reference"]`).type('alert amid');
      cy.get(`[data-cy="reference"]`).should('have.value', 'alert amid');

      cy.get(`[data-cy="description"]`).type('stiffen towards');
      cy.get(`[data-cy="description"]`).should('have.value', 'stiffen towards');

      cy.get(`[data-cy="postedAt"]`).type('2026-09-21T03:15');
      cy.get(`[data-cy="postedAt"]`).blur();
      cy.get(`[data-cy="postedAt"]`).should('have.value', '2026-09-21T03:15');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        journalEntry = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', journalEntryPageUrl);
    });
  });
});
