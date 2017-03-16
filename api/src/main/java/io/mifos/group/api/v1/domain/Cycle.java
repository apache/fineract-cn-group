/*
 * Copyright 2017 The Mifos Initiative
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mifos.group.api.v1.domain;

import javax.validation.constraints.NotNull;

public class Cycle {

  @NotNull
  private Integer numberOfMeetings;
  @NotNull
  private Frequency frequency;
  private Adjustment adjustment;

  public Cycle() {
    super();
  }

  public Integer getNumberOfMeetings() {
    return this.numberOfMeetings;
  }

  public void setNumberOfMeetings(final Integer numberOfMeetings) {
    this.numberOfMeetings = numberOfMeetings;
  }

  public String getFrequency() {
    return this.frequency.name();
  }

  public void setFrequency(final String frequency) {
    this.frequency = Frequency.valueOf(frequency);
  }

  public String getAdjustment() {
    return this.adjustment.name();
  }

  public void setAdjustment(final String adjustment) {
    this.adjustment = Adjustment.valueOf(adjustment);
  }

  public enum Frequency {
    DAILY,
    WEEKLY,
    FORTNIGHTLY,
    MONTHLY
  }

  public enum Adjustment {
    NEXT_BUSINESS_DAY,
    SKIP
  }
}
