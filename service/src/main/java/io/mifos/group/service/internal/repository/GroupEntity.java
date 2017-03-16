/*
 * Copyright 2016 The Mifos Initiative.
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
package io.mifos.group.service.internal.repository;

import io.mifos.core.mariadb.util.LocalDateTimeConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "ptah_groups")
public class GroupEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  @Column(name = "identifier", nullable = false)
  private String identifier;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "group_definition_id", nullable = false)
  private GroupDefinitionEntity groupDefinition;
  @Column(name = "a_name", nullable = false)
  private String name;
  @Column(name = "leaders")
  private String leaders;
  @Column(name = "members", nullable = false)
  private String members;
  @Column(name = "office", nullable = false)
  private String office;
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "address_id", nullable = false)
  private AddressEntity addressEntity;
  @Column(name = "assigned_employee", nullable = false)
  private String assignedEmployee;
  @Column(name = "weekday", nullable = false)
  private Integer weekday;
  @Column(name = "group_status", nullable = false)
  private String groupStatus;
  @Column(name = "current_cycle", nullable = false)
  private Integer currentCycle = Integer.valueOf(0);
  @Column(name = "created_on", nullable = false)
  @Convert(converter = LocalDateTimeConverter.class)
  private LocalDateTime createdOn;
  @Column(name = "created_by", nullable = false)
  private String createdBy;
  @Column(name = "last_modified_on")
  @Convert(converter = LocalDateTimeConverter.class)
  private LocalDateTime lastModifiedOn;
  @Column(name = "last_modified_by")
  private String lastModifiedBy;

  public GroupEntity() {
    super();
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getIdentifier() {
    return this.identifier;
  }

  public void setIdentifier(final String identifier) {
    this.identifier = identifier;
  }

  public GroupDefinitionEntity getGroupDefinition() {
    return this.groupDefinition;
  }

  public void setGroupDefinition(final GroupDefinitionEntity groupDefinition) {
    this.groupDefinition = groupDefinition;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getLeaders() {
    return this.leaders;
  }

  public void setLeaders(final String leaders) {
    this.leaders = leaders;
  }

  public String getMembers() {
    return this.members;
  }

  public void setMembers(final String members) {
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

  public AddressEntity getAddressEntity() {
    return this.addressEntity;
  }

  public void setAddressEntity(final AddressEntity addressEntity) {
    this.addressEntity = addressEntity;
  }

  public Integer getWeekday() {
    return this.weekday;
  }

  public void setWeekday(final Integer weekday) {
    this.weekday = weekday;
  }

  public String getGroupStatus() {
    return this.groupStatus;
  }

  public void setGroupStatus(final String groupStatus) {
    this.groupStatus = groupStatus;
  }

  public Integer getCurrentCycle() {
    return this.currentCycle;
  }

  public void setCurrentCycle(final Integer currentCycle) {
    this.currentCycle = currentCycle;
  }

  public LocalDateTime getCreatedOn() {
    return this.createdOn;
  }

  public void setCreatedOn(final LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }

  public String getCreatedBy() {
    return this.createdBy;
  }

  public void setCreatedBy(final String createdBy) {
    this.createdBy = createdBy;
  }

  public LocalDateTime getLastModifiedOn() {
    return this.lastModifiedOn;
  }

  public void setLastModifiedOn(final LocalDateTime lastModifiedOn) {
    this.lastModifiedOn = lastModifiedOn;
  }

  public String getLastModifiedBy() {
    return this.lastModifiedBy;
  }

  public void setLastModifiedBy(final String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }
}
