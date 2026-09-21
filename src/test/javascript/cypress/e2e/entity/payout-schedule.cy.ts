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

describe('PayoutSchedule e2e test', () => {
  const payoutSchedulePageUrl = '/payout-schedule';
  let username: string;
  let password: string;
  const payoutScheduleSample = { frequencyMode: 'MANUAL', thresholdAmount: 13131.23, isActive: false };

  let payoutSchedule;
  let corporateTenant;

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
      url: '/api/corporate-tenants',
      body: {
        legalBusinessName: 'horst',
        businessRegistrationNumber: 'lively',
        taxIdentificationNumber: 'digestive though',
        operatingJurisdiction: 'KE',
        status: 'PENDING_REVIEW',
        kycStatus: 'PENDING',
        riskScore: 15138,
        createdAt: '2026-09-21T11:15:28.341Z',
        activatedAt: '2026-09-21T12:35:16.466Z',
      },
    }).then(({ body }) => {
      corporateTenant = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/payout-schedules+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/payout-schedules').as('postEntityRequest');
    cy.intercept('DELETE', '/api/payout-schedules/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/corporate-tenants', {
      statusCode: 200,
      body: [corporateTenant],
    });
  });

  afterEach(() => {
    if (payoutSchedule) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/payout-schedules/${payoutSchedule.id}`,
      }).then(() => {
        payoutSchedule = undefined;
      });
    }
  });

  afterEach(() => {
    if (corporateTenant) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/corporate-tenants/${corporateTenant.id}`,
      }).then(() => {
        corporateTenant = undefined;
      });
    }
  });

  it('PayoutSchedules menu should load PayoutSchedules page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('payout-schedule');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PayoutSchedule').should('exist');
    cy.location('pathname').should('eq', payoutSchedulePageUrl);
  });

  describe('PayoutSchedule page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(payoutSchedulePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PayoutSchedule page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.location('pathname').should('eq', `${payoutSchedulePageUrl}/new`);
        cy.getEntityCreateUpdateHeading('PayoutSchedule');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', payoutSchedulePageUrl);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/payout-schedules',
          body: {
            ...payoutScheduleSample,
            tenant: corporateTenant,
          },
        }).then(({ body }) => {
          payoutSchedule = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/payout-schedules+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/payout-schedules?page=0&size=20>; rel="last",<http://localhost/api/payout-schedules?page=0&size=20>; rel="first"',
              },
              body: [payoutSchedule],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(payoutSchedulePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details PayoutSchedule page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('payoutSchedule');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', payoutSchedulePageUrl);
      });

      it('edit button click should load edit PayoutSchedule page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PayoutSchedule');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', payoutSchedulePageUrl);
      });

      it('edit button click should load edit PayoutSchedule page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PayoutSchedule');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', payoutSchedulePageUrl);
      });

      it('last delete button click should delete instance of PayoutSchedule', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('payoutSchedule').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.location('pathname').should('eq', payoutSchedulePageUrl);

        payoutSchedule = undefined;
      });
    });
  });

  describe('new PayoutSchedule page', () => {
    beforeEach(() => {
      cy.visit(payoutSchedulePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PayoutSchedule');
    });

    it('should create an instance of PayoutSchedule', () => {
      cy.get(`[data-cy="frequencyMode"]`).select('MONTHLY');

      cy.get(`[data-cy="thresholdAmount"]`).type('28777.8');
      cy.get(`[data-cy="thresholdAmount"]`).should('have.value', '28777.8');

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(`[data-cy="tenant"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        payoutSchedule = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.location('pathname').should('eq', payoutSchedulePageUrl);
    });
  });
});
