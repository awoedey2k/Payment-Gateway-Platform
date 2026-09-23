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

describe('JournalLine e2e test', () => {
  const journalLinePageUrl = '/journal-line';
  let username: string;
  let password: string;
  const journalLineSample = {};

  let journalLine;
  let ledgerAccount;
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
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/ledger-accounts',
      body: { accountCode: 'pupil quarrelsome svelte', accountType: 'EXPENSE', currencyCode: 'TOP' },
    }).then(({ body }) => {
      ledgerAccount = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/journal-entries',
      body: { reference: 'taut quaintly', description: 'plus', postedAt: '2026-09-20T21:37:56.481Z' },
    }).then(({ body }) => {
      journalEntry = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/journal-lines+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/journal-lines').as('postEntityRequest');
    cy.intercept('DELETE', '/api/journal-lines/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/ledger-accounts', {
      statusCode: 200,
      body: [ledgerAccount],
    });

    cy.intercept('GET', '/api/journal-entries', {
      statusCode: 200,
      body: [journalEntry],
    });
  });

  afterEach(() => {
    if (journalLine) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/journal-lines/${journalLine.id}`,
      }).then(() => {
        journalLine = undefined;
      });
    }
  });

  afterEach(() => {
    if (ledgerAccount) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/ledger-accounts/${ledgerAccount.id}`,
      }).then(() => {
        ledgerAccount = undefined;
      });
    }
    if (journalEntry) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/journal-entries/${journalEntry.id}`,
      }).then(() => {
        journalEntry = undefined;
      });
    }
  });

  it('JournalLines menu should load JournalLines page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('journal-line');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('JournalLine').should('exist');
    cy.location('pathname').should('eq', journalLinePageUrl);
  });

  describe('JournalLine page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(journalLinePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create JournalLine page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${journalLinePageUrl}/new`);
        cy.getEntityCreateUpdateHeading('JournalLine');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', journalLinePageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/journal-lines',
          body: {
            ...journalLineSample,
            account: ledgerAccount,
            journalEntry,
          },
        }).then(({ body }) => {
          journalLine = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/journal-lines+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/journal-lines?page=0&size=20>; rel="last",<http://localhost/api/journal-lines?page=0&size=20>; rel="first"',
              },
              body: [journalLine],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(journalLinePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details JournalLine page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('journalLine');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', journalLinePageUrl);
      });

      it('edit button click should load edit JournalLine page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('JournalLine');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', journalLinePageUrl);
      });

      it('edit button click should load edit JournalLine page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('JournalLine');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', journalLinePageUrl);
      });

      it('last delete button click should delete instance of JournalLine', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('journalLine').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', journalLinePageUrl);

        journalLine = undefined;
      });
    });
  });

  describe('new JournalLine page', () => {
    beforeEach(() => {
      cy.visit(journalLinePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('JournalLine');
    });

    it('should create an instance of JournalLine', () => {
      cy.get(`[data-cy="debitAmount"]`).type('22584.84');
      cy.get(`[data-cy="debitAmount"]`).should('have.value', '22584.84');

      cy.get(`[data-cy="creditAmount"]`).type('10269.99');
      cy.get(`[data-cy="creditAmount"]`).should('have.value', '10269.99');

      cy.get(`[data-cy="account"]`).select(1);
      cy.get(`[data-cy="journalEntry"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        journalLine = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', journalLinePageUrl);
    });
  });
});
