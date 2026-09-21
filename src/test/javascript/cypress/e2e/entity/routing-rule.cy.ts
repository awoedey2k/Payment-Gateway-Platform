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

describe('RoutingRule e2e test', () => {
  const routingRulePageUrl = '/routing-rule';
  let username: string;
  let password: string;
  const routingRuleSample = {
    priority: 752,
    scope: 'PLATFORM_GLOBAL',
    primaryAdapter: 'lightly forenenst puritan',
    maxRetries: 27354,
    isActive: false,
  };

  let routingRule;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/routing-rules+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/routing-rules').as('postEntityRequest');
    cy.intercept('DELETE', '/api/routing-rules/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (routingRule) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/routing-rules/${routingRule.id}`,
      }).then(() => {
        routingRule = undefined;
      });
    }
  });

  it('RoutingRules menu should load RoutingRules page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('routing-rule');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('RoutingRule').should('exist');
    cy.location('pathname').should('eq', routingRulePageUrl);
  });

  describe('RoutingRule page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(routingRulePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create RoutingRule page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${routingRulePageUrl}/new`);
        cy.getEntityCreateUpdateHeading('RoutingRule');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', routingRulePageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/routing-rules',
          body: routingRuleSample,
        }).then(({ body }) => {
          routingRule = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/routing-rules+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/routing-rules?page=0&size=20>; rel="last",<http://localhost/api/routing-rules?page=0&size=20>; rel="first"',
              },
              body: [routingRule],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(routingRulePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details RoutingRule page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('routingRule');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', routingRulePageUrl);
      });

      it('edit button click should load edit RoutingRule page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('RoutingRule');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', routingRulePageUrl);
      });

      it('edit button click should load edit RoutingRule page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('RoutingRule');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', routingRulePageUrl);
      });

      it('last delete button click should delete instance of RoutingRule', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('routingRule').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', routingRulePageUrl);

        routingRule = undefined;
      });
    });
  });

  describe('new RoutingRule page', () => {
    beforeEach(() => {
      cy.visit(routingRulePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('RoutingRule');
    });

    it('should create an instance of RoutingRule', () => {
      cy.get(`[data-cy="priority"]`).type('29562');
      cy.get(`[data-cy="priority"]`).should('have.value', '29562');

      cy.get(`[data-cy="scope"]`).select('TENANT_CUSTOM');

      cy.get(`[data-cy="countryCode"]`).type('GF');
      cy.get(`[data-cy="countryCode"]`).should('have.value', 'GF');

      cy.get(`[data-cy="currencyCode"]`).type('DOP');
      cy.get(`[data-cy="currencyCode"]`).should('have.value', 'DOP');

      cy.get(`[data-cy="cardBrand"]`).type('emotional sternly chap');
      cy.get(`[data-cy="cardBrand"]`).should('have.value', 'emotional sternly chap');

      cy.get(`[data-cy="primaryAdapter"]`).type('geez splosh juvenile');
      cy.get(`[data-cy="primaryAdapter"]`).should('have.value', 'geez splosh juvenile');

      cy.get(`[data-cy="fallbackAdapter"]`).type('knowingly shimmering');
      cy.get(`[data-cy="fallbackAdapter"]`).should('have.value', 'knowingly shimmering');

      cy.get(`[data-cy="maxRetries"]`).type('3955');
      cy.get(`[data-cy="maxRetries"]`).should('have.value', '3955');

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        routingRule = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', routingRulePageUrl);
    });
  });
});
