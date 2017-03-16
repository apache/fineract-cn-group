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

import io.mifos.core.api.util.UserContextHolder;
import io.mifos.core.lang.DateConverter;
import io.mifos.group.service.internal.repository.GroupCommandEntity;
import io.mifos.group.api.v1.domain.GroupCommand;

import java.time.Clock;
import java.time.LocalDateTime;

public class GroupCommandMapper {

  private GroupCommandMapper() {
    super();
  }

  public static GroupCommand map(final GroupCommandEntity groupCommandEntity) {
    final GroupCommand groupCommand = new GroupCommand();
    groupCommand.setAction(groupCommandEntity.getAction());
    groupCommand.setNote(groupCommandEntity.getNote());
    groupCommand.setCreatedBy(groupCommandEntity.getCreatedBy());
    groupCommand.setCreatedOn(DateConverter.toIsoString(groupCommandEntity.getCreatedOn()));
    return groupCommand;
  }

  public static GroupCommandEntity map(final GroupCommand groupCommand) {
    final GroupCommandEntity groupCommandEntity = new GroupCommandEntity();
    groupCommandEntity.setAction(groupCommand.getAction());
    groupCommandEntity.setNote(groupCommand.getNote());
    groupCommandEntity.setCreatedBy(UserContextHolder.checkedGetUser());
    groupCommandEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
    return groupCommandEntity;
  }
}
