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

describe('AmlCheck e2e test', () => {
  const amlCheckPageUrl = '/aml-check';
  let username: string;
  let password: string;
  const amlCheckSample = { riskScore: 6986, decision: 'TIER_REVIEW', checkedAt: '2026-09-20T23:30:39.880Z' };

  let amlCheck;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/aml-checks+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/aml-checks').as('postEntityRequest');
    cy.intercept('DELETE', '/api/aml-checks/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (amlCheck) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/aml-checks/${amlCheck.id}`,
      }).then(() => {
        amlCheck = undefined;
      });
    }
  });

  it('AmlChecks menu should load AmlChecks page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('aml-check');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('AmlCheck').should('exist');
    cy.location('pathname').should('eq', amlCheckPageUrl);
  });

  describe('AmlCheck page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(amlCheckPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create AmlCheck page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${amlCheckPageUrl}/new`);
        cy.getEntityCreateUpdateHeading('AmlCheck');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', amlCheckPageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/aml-checks',
          body: amlCheckSample,
        }).then(({ body }) => {
          amlCheck = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/aml-checks+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/aml-checks?page=0&size=20>; rel="last",<http://localhost/api/aml-checks?page=0&size=20>; rel="first"',
              },
              body: [amlCheck],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(amlCheckPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details AmlCheck page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('amlCheck');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', amlCheckPageUrl);
      });

      it('edit button click should load edit AmlCheck page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AmlCheck');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', amlCheckPageUrl);
      });

      it('edit button click should load edit AmlCheck page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AmlCheck');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', amlCheckPageUrl);
      });

      it('last delete button click should delete instance of AmlCheck', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('amlCheck').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', amlCheckPageUrl);

        amlCheck = undefined;
      });
    });
  });

  describe('new AmlCheck page', () => {
    beforeEach(() => {
      cy.visit(amlCheckPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('AmlCheck');
    });

    it('should create an instance of AmlCheck', () => {
      cy.get(`[data-cy="riskScore"]`).type('30052');
      cy.get(`[data-cy="riskScore"]`).should('have.value', '30052');

      cy.get(`[data-cy="decision"]`).select('ALLOW');

      cy.get(`[data-cy="ruleTriggered"]`).type('hastily');
      cy.get(`[data-cy="ruleTriggered"]`).should('have.value', 'hastily');

      cy.get(`[data-cy="checkedAt"]`).type('2026-09-21T10:27');
      cy.get(`[data-cy="checkedAt"]`).blur();
      cy.get(`[data-cy="checkedAt"]`).should('have.value', '2026-09-21T10:27');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        amlCheck = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', amlCheckPageUrl);
    });
  });
});
