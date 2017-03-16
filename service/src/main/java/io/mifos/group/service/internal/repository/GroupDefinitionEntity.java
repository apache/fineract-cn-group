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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "ptah_group_definitions")
public class GroupDefinitionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  @Column(name = "identifier", nullable = false)
  private String identifier;
  @Column(name = "description")
  private String description;
  @Column(name = "minimal_size", nullable = false)
  private Integer minimalSize;
  @Column(name = "maximal_size", nullable = false)
  private Integer maximalSize;
  @Column(name = "number_of_meetings", nullable = false)
  private Integer numberOfMeetings;
  @Column(name = "frequency", nullable = false)
  private String frequency;
  @Column(name = "adjustment")
  private String adjustment;
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

  public GroupDefinitionEntity() {
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

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public Integer getMinimalSize() {
    return this.minimalSize;
  }

  public void setMinimalSize(final Integer minimalSize) {
    this.minimalSize = minimalSize;
  }

  public Integer getMaximalSize() {
    return this.maximalSize;
  }

  public void setMaximalSize(final Integer maximalSize) {
    this.maximalSize = maximalSize;
  }

  public Integer getNumberOfMeetings() {
    return this.numberOfMeetings;
  }

  public void setNumberOfMeetings(final Integer numberOfMeetings) {
    this.numberOfMeetings = numberOfMeetings;
  }

  public String getFrequency() {
    return this.frequency;
  }

  public void setFrequency(final String frequency) {
    this.frequency = frequency;
  }

  public String getAdjustment() {
    return this.adjustment;
  }

  public void setAdjustment(final String adjustment) {
    this.adjustment = adjustment;
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
