/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.cn.group.internal.mapper;

import org.apache.fineract.cn.group.api.v1.domain.Group;
import org.apache.fineract.cn.group.internal.repository.GroupEntity;
import org.apache.fineract.cn.lang.DateConverter;
import org.springframework.util.StringUtils;

public class GroupMapper {

  private GroupMapper() {
    super();
  }

  public static Group map(final GroupEntity groupEntity) {
    final Group group = new Group();
    group.setIdentifier(groupEntity.getIdentifier());
    group.setGroupDefinitionIdentifier(groupEntity.getGroupDefinition().getIdentifier());
    group.setName(groupEntity.getName());
    group.setLeaders(StringUtils.commaDelimitedListToSet(groupEntity.getLeaders()));
    group.setMembers(StringUtils.commaDelimitedListToSet(groupEntity.getMembers()));
    group.setOffice(groupEntity.getOffice());
    group.setAssignedEmployee(groupEntity.getAssignedEmployee());
    group.setWeekday(groupEntity.getWeekday());
    group.setStatus(groupEntity.getGroupStatus());
    group.setCreatedOn(DateConverter.toIsoString(groupEntity.getCreatedOn()));
    group.setCreatedBy(groupEntity.getCreatedBy());
    if (groupEntity.getLastModifiedOn() != null) {
      group.setLastModifiedOn(DateConverter.toIsoString(groupEntity.getLastModifiedOn()));
      group.setLastModifiedBy(groupEntity.getLastModifiedBy());
    }
    return group;
  }
}
