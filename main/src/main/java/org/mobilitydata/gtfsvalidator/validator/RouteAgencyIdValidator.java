/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.validator;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

/**
 * Checks that agency_id field in "routes.txt" is defined for every row if there is more than 1
 * agency in the feed.
 *
 * <p>Generated notice: {@link MissingRequiredFieldNotice}.
 *
 * <p>Generated notice: {@link AgencyIdRecommendedNotice}.
 */
@GtfsValidator
public class RouteAgencyIdValidator extends FileValidator {
  private final GtfsAgencyTableContainer agencyTable;
  private final GtfsRouteTableContainer routeTable;

  @Inject
  RouteAgencyIdValidator(GtfsAgencyTableContainer agencyTable, GtfsRouteTableContainer routeTable) {
    this.agencyTable = agencyTable;
    this.routeTable = routeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {

    // routes.agency_id is required when there are multiple agencies
    // or an agencyId is specified for single agency
    boolean agencyIdRequired =
        (agencyTable.entityCount() > 1) || agencyTable.getEntities().get(0).hasAgencyId();

    for (GtfsRoute route : routeTable.getEntities()) {
      if (!route.hasAgencyId()) {
        if (agencyIdRequired) {
          noticeContainer.addValidationNotice(
              new MissingRequiredFieldNotice(
                  routeTable.gtfsFilename(),
                  route.csvRowNumber(),
                  GtfsRouteTableLoader.AGENCY_ID_FIELD_NAME));
        } else {
          noticeContainer.addValidationNotice(new AgencyIdRecommendedNotice(route.csvRowNumber()));
        }
      }
    }
    // No need to check reference integrity because it is done by a validator generated from
    // @ForeignKey annotation.
  }
  /**
   * AgencyId field is recommended even if only one agency.
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  static class AgencyIdRecommendedNotice extends ValidationNotice {
    private final long csvRowNumber;

    AgencyIdRecommendedNotice(long csvRowNumber) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = csvRowNumber;
    }
  }
}
