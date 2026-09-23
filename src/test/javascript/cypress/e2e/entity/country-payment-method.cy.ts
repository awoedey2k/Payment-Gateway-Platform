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

describe('CountryPaymentMethod e2e test', () => {
  const countryPaymentMethodPageUrl = '/country-payment-method';
  let username: string;
  let password: string;
  const countryPaymentMethodSample = {
    minTxnAmount: 13706.29,
    maxTxnAmount: 5497.12,
    supportsRecurring: true,
    supportsInstantRefund: false,
    isActive: false,
  };

  let countryPaymentMethod;
  let country;
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
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/countries',
      body: { isoCode: 'st', name: 'millet puzzled', defaultCurrencyCode: 'dreamily spotless clavicle' },
    }).then(({ body }) => {
      country = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/payment-methods',
      body: { code: 'aw', displayName: 'hurtful cod thorny', category: 'USSD' },
    }).then(({ body }) => {
      paymentMethod = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/country-payment-methods+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/country-payment-methods').as('postEntityRequest');
    cy.intercept('DELETE', '/api/country-payment-methods/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/countries', {
      statusCode: 200,
      body: [country],
    });

    cy.intercept('GET', '/api/payment-methods', {
      statusCode: 200,
      body: [paymentMethod],
    });
  });

  afterEach(() => {
    if (countryPaymentMethod) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/country-payment-methods/${countryPaymentMethod.id}`,
      }).then(() => {
        countryPaymentMethod = undefined;
      });
    }
  });

  afterEach(() => {
    if (country) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/countries/${country.id}`,
      }).then(() => {
        country = undefined;
      });
    }
    if (paymentMethod) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/payment-methods/${paymentMethod.id}`,
      }).then(() => {
        paymentMethod = undefined;
      });
    }
  });

  it('CountryPaymentMethods menu should load CountryPaymentMethods page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('country-payment-method');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CountryPaymentMethod').should('exist');
    cy.location('pathname').should('eq', countryPaymentMethodPageUrl);
  });

  describe('CountryPaymentMethod page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(countryPaymentMethodPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CountryPaymentMethod page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${countryPaymentMethodPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('CountryPaymentMethod');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', countryPaymentMethodPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/country-payment-methods',
          body: {
            ...countryPaymentMethodSample,
            country,
            paymentMethod,
          },
        }).then(({ body }) => {
          countryPaymentMethod = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/country-payment-methods+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/country-payment-methods?page=0&size=20>; rel="last",<http://localhost/api/country-payment-methods?page=0&size=20>; rel="first"',
              },
              body: [countryPaymentMethod],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(countryPaymentMethodPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details CountryPaymentMethod page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('countryPaymentMethod');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', countryPaymentMethodPageUrl);
      });

      it('edit button click should load edit CountryPaymentMethod page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CountryPaymentMethod');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', countryPaymentMethodPageUrl);
      });

      it('edit button click should load edit CountryPaymentMethod page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CountryPaymentMethod');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', countryPaymentMethodPageUrl);
      });

      it('last delete button click should delete instance of CountryPaymentMethod', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('countryPaymentMethod').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', countryPaymentMethodPageUrl);

        countryPaymentMethod = undefined;
      });
    });
  });

  describe('new CountryPaymentMethod page', () => {
    beforeEach(() => {
      cy.visit(countryPaymentMethodPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('CountryPaymentMethod');
    });

    it('should create an instance of CountryPaymentMethod', () => {
      cy.get(`[data-cy="minTxnAmount"]`).type('6233.54');
      cy.get(`[data-cy="minTxnAmount"]`).should('have.value', '6233.54');

      cy.get(`[data-cy="maxTxnAmount"]`).type('6525.91');
      cy.get(`[data-cy="maxTxnAmount"]`).should('have.value', '6525.91');

      cy.get(`[data-cy="supportsRecurring"]`).should('not.be.checked');
      cy.get(`[data-cy="supportsRecurring"]`).click();
      cy.get(`[data-cy="supportsRecurring"]`).should('be.checked');

      cy.get(`[data-cy="supportsInstantRefund"]`).should('not.be.checked');
      cy.get(`[data-cy="supportsInstantRefund"]`).click();
      cy.get(`[data-cy="supportsInstantRefund"]`).should('be.checked');

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(`[data-cy="country"]`).select(1);
      cy.get(`[data-cy="paymentMethod"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        countryPaymentMethod = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', countryPaymentMethodPageUrl);
    });
  });
});
