import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { RoutingRuleScope } from 'app/entities/enumerations/routing-rule-scope.model';
import { AlertError } from 'app/shared/alert';
import { IRoutingRule } from '../routing-rule.model';
import { RoutingRuleService } from '../service/routing-rule.service';

import { RoutingRuleFormGroup, RoutingRuleFormService } from './routing-rule-form.service';

@Component({
  selector: 'jhi-routing-rule-update',
  templateUrl: './routing-rule-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class RoutingRuleUpdate implements OnInit {
  readonly isSaving = signal(false);
  routingRule: IRoutingRule | null = null;
  routingRuleScopeValues = Object.keys(RoutingRuleScope);

  corporateTenantsSharedCollection = signal<ICorporateTenant[]>([]);

  protected routingRuleService = inject(RoutingRuleService);
  protected routingRuleFormService = inject(RoutingRuleFormService);
  protected corporateTenantService = inject(CorporateTenantService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: RoutingRuleFormGroup = this.routingRuleFormService.createRoutingRuleFormGroup();

  compareCorporateTenant = (o1: ICorporateTenant | null, o2: ICorporateTenant | null): boolean =>
    this.corporateTenantService.compareCorporateTenant(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ routingRule }) => {
      this.routingRule = routingRule;
      if (routingRule) {
        this.updateForm(routingRule);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const routingRule = this.routingRuleFormService.getRoutingRule(this.editForm);
    if (routingRule.id === null) {
      this.subscribeToSaveResponse(this.routingRuleService.create(routingRule));
    } else {
      this.subscribeToSaveResponse(this.routingRuleService.update(routingRule));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IRoutingRule | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(routingRule: IRoutingRule): void {
    this.routingRule = routingRule;
    this.routingRuleFormService.resetForm(this.editForm, routingRule);

    this.corporateTenantsSharedCollection.update(corporateTenants =>
      this.corporateTenantService.addCorporateTenantToCollectionIfMissing<ICorporateTenant>(corporateTenants, routingRule.tenant),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.corporateTenantService
      .query()
      .pipe(map((res: HttpResponse<ICorporateTenant[]>) => res.body ?? []))
      .pipe(
        map((corporateTenants: ICorporateTenant[]) =>
          this.corporateTenantService.addCorporateTenantToCollectionIfMissing<ICorporateTenant>(corporateTenants, this.routingRule?.tenant),
        ),
      )
      .subscribe((corporateTenants: ICorporateTenant[]) => this.corporateTenantsSharedCollection.set(corporateTenants));
  }
}
