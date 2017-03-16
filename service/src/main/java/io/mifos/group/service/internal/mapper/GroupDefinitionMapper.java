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
package io.mifos.group.service.internal.mapper;

import io.mifos.core.lang.DateConverter;
import io.mifos.group.service.internal.repository.GroupDefinitionEntity;
import io.mifos.group.api.v1.domain.Cycle;
import io.mifos.group.api.v1.domain.GroupDefinition;

public class GroupDefinitionMapper {

  private GroupDefinitionMapper() {
    super();
  }

  public static GroupDefinition map(final GroupDefinitionEntity groupDefinitionEntity) {
    final GroupDefinition groupDefinition = new GroupDefinition();
    groupDefinition.setIdentifier(groupDefinitionEntity.getIdentifier());
    groupDefinition.setDescription(groupDefinitionEntity.getDescription());
    groupDefinition.setMinimalSize(groupDefinitionEntity.getMinimalSize());
    groupDefinition.setMaximalSize(groupDefinitionEntity.getMaximalSize());
    groupDefinition.setCreateOn(DateConverter.toIsoString(groupDefinitionEntity.getCreatedOn()));
    groupDefinition.setCreatedBy(groupDefinitionEntity.getCreatedBy());

    if (groupDefinitionEntity.getLastModifiedOn() != null) {
      groupDefinition.setLastModifiedOn(DateConverter.toIsoString(groupDefinitionEntity.getLastModifiedOn()));
      groupDefinition.setLastModifiedBy(groupDefinitionEntity.getLastModifiedBy());
    }

    final Cycle cycle = new Cycle();
    cycle.setNumberOfMeetings(groupDefinitionEntity.getNumberOfMeetings());
    cycle.setFrequency(groupDefinitionEntity.getFrequency());
    cycle.setAdjustment(groupDefinitionEntity.getAdjustment());
    groupDefinition.setCycle(cycle);

    return groupDefinition;
  }
}
