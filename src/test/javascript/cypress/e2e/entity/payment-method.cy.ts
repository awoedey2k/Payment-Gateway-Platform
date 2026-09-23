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

describe('PaymentMethod e2e test', () => {
  const paymentMethodPageUrl = '/payment-method';
  let username: string;
  let password: string;
  const paymentMethodSample = { code: 'whereas now camouflage', displayName: 'famously', category: 'MOBILE_MONEY' };

  let paymentMethod;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/payment-methods+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/payment-methods').as('postEntityRequest');
    cy.intercept('DELETE', '/api/payment-methods/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (paymentMethod) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/payment-methods/${paymentMethod.id}`,
      }).then(() => {
        paymentMethod = undefined;
      });
    }
  });

  it('PaymentMethods menu should load PaymentMethods page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('payment-method');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PaymentMethod').should('exist');
    cy.location('pathname').should('eq', paymentMethodPageUrl);
  });

  describe('PaymentMethod page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(paymentMethodPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PaymentMethod page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${paymentMethodPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('PaymentMethod');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', paymentMethodPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/payment-methods',
          body: paymentMethodSample,
        }).then(({ body }) => {
          paymentMethod = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/payment-methods+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/payment-methods?page=0&size=20>; rel="last",<http://localhost/api/payment-methods?page=0&size=20>; rel="first"',
              },
              body: [paymentMethod],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(paymentMethodPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details PaymentMethod page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('paymentMethod');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', paymentMethodPageUrl);
      });

      it('edit button click should load edit PaymentMethod page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PaymentMethod');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', paymentMethodPageUrl);
      });

      it('edit button click should load edit PaymentMethod page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PaymentMethod');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', paymentMethodPageUrl);
      });

      it('last delete button click should delete instance of PaymentMethod', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('paymentMethod').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', paymentMethodPageUrl);

        paymentMethod = undefined;
      });
    });
  });

  describe('new PaymentMethod page', () => {
    beforeEach(() => {
      cy.visit(paymentMethodPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PaymentMethod');
    });

    it('should create an instance of PaymentMethod', () => {
      cy.get(`[data-cy="code"]`).type('searchingly hypothesise taxicab');
      cy.get(`[data-cy="code"]`).should('have.value', 'searchingly hypothesise taxicab');

      cy.get(`[data-cy="displayName"]`).type('multicolored gown');
      cy.get(`[data-cy="displayName"]`).should('have.value', 'multicolored gown');

      cy.get(`[data-cy="category"]`).select('BANK_TRANSFER');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        paymentMethod = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', paymentMethodPageUrl);
    });
  });
});
