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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.Set;

public class Group {

  @NotBlank
  private String identifier;
  @NotBlank
  private String groupDefinitionIdentifier;
  @NotBlank
  private String name;
  @Size(max = 15)
  private Set<String> leaders;
  @Size(min = 1, max = 60)
  private Set<String> members;
  @NotBlank
  private String office;
  @NotBlank
  private String assignedEmployee;
  @NotNull
  private Integer weekday;
  private Status status;
  @Valid
  private Address address;
  private String createdOn;
  private String createdBy;
  private String lastModifiedOn;
  private String lastModifiedBy;

  public Group() {
    super();
  }

  public String getGroupDefinitionIdentifier() {
    return this.groupDefinitionIdentifier;
  }

  public void setGroupDefinitionIdentifier(final String groupDefinitionIdentifier) {
    this.groupDefinitionIdentifier = groupDefinitionIdentifier;
  }

  public String getIdentifier() {
    return this.identifier;
  }

  public void setIdentifier(final String identifier) {
    this.identifier = identifier;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Set<String> getLeaders() {
    return this.leaders;
  }

  public void setLeaders(final Set<String> leaders) {
    this.leaders = leaders;
  }

  public Set<String> getMembers() {
    return this.members;
  }

  public void setMembers(final Set<String> members) {
    this.members = members;
  }

  public String getOffice() {
    return this.office;
  }

  public void setOffice(final String office) {
    this.office = office;
  }

  public String getAssignedEmployee() {
    return this.assignedEmployee;
  }

  public void setAssignedEmployee(final String assignedEmployee) {
    this.assignedEmployee = assignedEmployee;
  }

  public String getStatus() {
    return this.status.name();
  }

  public void setStatus(final String status) {
    this.status = Status.valueOf(status);
  }

  public Integer getWeekday() {
    return this.weekday;
  }

  public void setWeekday(final Integer weekday) {
    this.weekday = weekday;
  }

  public Address getAddress() {
    return this.address;
  }

  public void setAddress(final Address address) {
    this.address = address;
  }

  public String getCreatedOn() {
    return this.createdOn;
  }

  public void setCreatedOn(final String createdOn) {
    this.createdOn = createdOn;
  }

  public String getCreatedBy() {
    return this.createdBy;
  }

  public void setCreatedBy(final String createdBy) {
    this.createdBy = createdBy;
  }

  public String getLastModifiedOn() {
    return this.lastModifiedOn;
  }

  public void setLastModifiedOn(final String lastModifiedOn) {
    this.lastModifiedOn = lastModifiedOn;
  }

  public String getLastModifiedBy() {
    return this.lastModifiedBy;
  }

  public void setLastModifiedBy(final String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public enum Weekday {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7);

    private Integer value;

    Weekday(final Integer value) {
      this.value = value;
    }

    public static Weekday from(final Integer dayOfWeek) {
      for (Weekday weekday : Weekday.values()) {
        if (Objects.equals(weekday.value, dayOfWeek)) {
          return weekday;
        }
      }
      throw new IllegalArgumentException("Unknown day of week '" + dayOfWeek + "'.");
    }

    public Integer getValue() {
      return this.value;
    }
  }

  public enum Status {
    PENDING,
    ACTIVE,
    CLOSED
  }
}
