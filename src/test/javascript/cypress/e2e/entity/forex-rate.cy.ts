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

describe('ForexRate e2e test', () => {
  const forexRatePageUrl = '/forex-rate';
  let username: string;
  let password: string;
  const forexRateSample = {
    baseCurrency: 'per',
    quoteCurrency: 'than even',
    rate: 26648.16,
    platformSpreadBps: 15055,
    lockedAt: '2026-09-20T23:22:19.250Z',
    expiresAt: '2026-09-21T14:50:06.911Z',
  };

  let forexRate;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/forex-rates+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/forex-rates').as('postEntityRequest');
    cy.intercept('DELETE', '/api/forex-rates/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (forexRate) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/forex-rates/${forexRate.id}`,
      }).then(() => {
        forexRate = undefined;
      });
    }
  });

  it('ForexRates menu should load ForexRates page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('forex-rate');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ForexRate').should('exist');
    cy.location('pathname').should('eq', forexRatePageUrl);
  });

  describe('ForexRate page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(forexRatePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ForexRate page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${forexRatePageUrl}/new`);
        cy.getEntityCreateUpdateHeading('ForexRate');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', forexRatePageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/forex-rates',
          body: forexRateSample,
        }).then(({ body }) => {
          forexRate = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/forex-rates+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/forex-rates?page=0&size=20>; rel="last",<http://localhost/api/forex-rates?page=0&size=20>; rel="first"',
              },
              body: [forexRate],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(forexRatePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ForexRate page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('forexRate');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', forexRatePageUrl);
      });

      it('edit button click should load edit ForexRate page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ForexRate');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', forexRatePageUrl);
      });

      it('edit button click should load edit ForexRate page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ForexRate');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', forexRatePageUrl);
      });

      it('last delete button click should delete instance of ForexRate', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('forexRate').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', forexRatePageUrl);

        forexRate = undefined;
      });
    });
  });

  describe('new ForexRate page', () => {
    beforeEach(() => {
      cy.visit(forexRatePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ForexRate');
    });

    it('should create an instance of ForexRate', () => {
      cy.get(`[data-cy="baseCurrency"]`).type('glisten');
      cy.get(`[data-cy="baseCurrency"]`).should('have.value', 'glisten');

      cy.get(`[data-cy="quoteCurrency"]`).type('intermesh');
      cy.get(`[data-cy="quoteCurrency"]`).should('have.value', 'intermesh');

      cy.get(`[data-cy="rate"]`).type('14120.51');
      cy.get(`[data-cy="rate"]`).should('have.value', '14120.51');

      cy.get(`[data-cy="platformSpreadBps"]`).type('23254');
      cy.get(`[data-cy="platformSpreadBps"]`).should('have.value', '23254');

      cy.get(`[data-cy="lockedAt"]`).type('2026-09-21T06:29');
      cy.get(`[data-cy="lockedAt"]`).blur();
      cy.get(`[data-cy="lockedAt"]`).should('have.value', '2026-09-21T06:29');

      cy.get(`[data-cy="expiresAt"]`).type('2026-09-21T09:18');
      cy.get(`[data-cy="expiresAt"]`).blur();
      cy.get(`[data-cy="expiresAt"]`).should('have.value', '2026-09-21T09:18');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        forexRate = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', forexRatePageUrl);
    });
  });
});
