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

describe('LedgerAccount e2e test', () => {
  const ledgerAccountPageUrl = '/ledger-account';
  let username: string;
  let password: string;
  const ledgerAccountSample = { accountCode: 'since hence', accountType: 'EXPENSE', currencyCode: 'MMK' };

  let ledgerAccount;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/ledger-accounts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/ledger-accounts').as('postEntityRequest');
    cy.intercept('DELETE', '/api/ledger-accounts/*').as('deleteEntityRequest');
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
  });

  it('LedgerAccounts menu should load LedgerAccounts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('ledger-account');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('LedgerAccount').should('exist');
    cy.location('pathname').should('eq', ledgerAccountPageUrl);
  });

  describe('LedgerAccount page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(ledgerAccountPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create LedgerAccount page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${ledgerAccountPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('LedgerAccount');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', ledgerAccountPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/ledger-accounts',
          body: ledgerAccountSample,
        }).then(({ body }) => {
          ledgerAccount = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/ledger-accounts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/ledger-accounts?page=0&size=20>; rel="last",<http://localhost/api/ledger-accounts?page=0&size=20>; rel="first"',
              },
              body: [ledgerAccount],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(ledgerAccountPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details LedgerAccount page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('ledgerAccount');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', ledgerAccountPageUrl);
      });

      it('edit button click should load edit LedgerAccount page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('LedgerAccount');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', ledgerAccountPageUrl);
      });

      it('edit button click should load edit LedgerAccount page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('LedgerAccount');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', ledgerAccountPageUrl);
      });

      it('last delete button click should delete instance of LedgerAccount', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('ledgerAccount').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', ledgerAccountPageUrl);

        ledgerAccount = undefined;
      });
    });
  });

  describe('new LedgerAccount page', () => {
    beforeEach(() => {
      cy.visit(ledgerAccountPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('LedgerAccount');
    });

    it('should create an instance of LedgerAccount', () => {
      cy.get(`[data-cy="accountCode"]`).type('gigantic coal different');
      cy.get(`[data-cy="accountCode"]`).should('have.value', 'gigantic coal different');

      cy.get(`[data-cy="accountType"]`).select('LIABILITY');

      cy.get(`[data-cy="currencyCode"]`).type('JPY');
      cy.get(`[data-cy="currencyCode"]`).should('have.value', 'JPY');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        ledgerAccount = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', ledgerAccountPageUrl);
    });
  });
});
