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

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

public class Attendee {

  @NotBlank
  private String customerIdentifier;
  @NotNull
  private Status status;

  public Attendee() {
    super();
  }

  public String getCustomerIdentifier() {
    return this.customerIdentifier;
  }

  public void setCustomerIdentifier(final String customerIdentifier) {
    this.customerIdentifier = customerIdentifier;
  }

  public String getStatus() {
    return this.status.name();
  }

  public void setStatus(final String status) {
    this.status = Status.valueOf(status);
  }

  public enum Status {
    EXPECTED,
    ATTENDED,
    MISSED
  }
}
