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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class GroupDefinition {

  @NotBlank
  private String identifier;
  private String description;
  @NotNull
  @Min(1L)
  private Integer minimalSize;
  @Min(2L)
  private Integer maximalSize;
  @Valid
  private Cycle cycle;
  private String createOn;
  private String createdBy;
  private String lastModifiedOn;
  private String lastModifiedBy;

  public GroupDefinition() {
    super();
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

  public Cycle getCycle() {
    return this.cycle;
  }

  public void setCycle(final Cycle cycle) {
    this.cycle = cycle;
  }

  public String getCreateOn() {
    return this.createOn;
  }

  public void setCreateOn(final String createOn) {
    this.createOn = createOn;
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
}
