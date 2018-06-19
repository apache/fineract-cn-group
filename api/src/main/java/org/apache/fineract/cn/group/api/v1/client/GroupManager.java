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
package org.apache.fineract.cn.group.api.v1.client;

import org.apache.fineract.cn.group.api.v1.domain.AssignedEmployeeHolder;
import org.apache.fineract.cn.group.api.v1.domain.Group;
import org.apache.fineract.cn.group.api.v1.domain.GroupCommand;
import org.apache.fineract.cn.group.api.v1.domain.GroupDefinition;
import org.apache.fineract.cn.group.api.v1.domain.GroupPage;
import org.apache.fineract.cn.group.api.v1.domain.Meeting;
import org.apache.fineract.cn.group.api.v1.domain.SignOffMeeting;
import java.util.List;
import java.util.Set;
import org.apache.fineract.cn.api.annotation.ThrowsException;
import org.apache.fineract.cn.api.annotation.ThrowsExceptions;
import org.apache.fineract.cn.api.util.CustomFeignClientsConfiguration;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@SuppressWarnings("unused")
@FeignClient(name="group-v1", path="/group/v1", configuration=CustomFeignClientsConfiguration.class)
public interface GroupManager {

  @RequestMapping(
      value = "/definitions",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.CONFLICT, exception = GroupDefinitionAlreadyExists.class),
      @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = GroupDefinitionValidation.class)
  })
  void createGroupDefinition(@RequestBody final GroupDefinition groupDefinition);

  @RequestMapping(
      value = "/definitions/{identifier}",
      method = RequestMethod.GET,
      produces = MediaType.ALL_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = GroupDefinitionNotFound.class)
  GroupDefinition findGroupDefinition(@PathVariable("identifier") final String identifier);

  @RequestMapping(
      value = "/definitions",
      method = RequestMethod.GET,
      produces = MediaType.ALL_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  List<GroupDefinition> fetchGroupDefinitions();

  @RequestMapping(
      value = "/groups",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsExceptions({
      @ThrowsException(status = HttpStatus.CONFLICT, exception = GroupAlreadyExists.class),
      @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = GroupValidationException.class)
  })
  void createGroup(@RequestBody final Group group);

  @RequestMapping(
      value = "/groups",
      method = RequestMethod.GET,
      produces = MediaType.ALL_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  GroupPage fetchGroups(@RequestParam(value="employee", required=false) final String employee,
                        @RequestParam(value="page", required=false) final Integer page,
                        @RequestParam(value="size",required=false) final Integer size,
                        @RequestParam(value="sortColumn", required=false) final String sortColumn,
                        @RequestParam(value="sortDirection",required=false) final String sortDirection);

  @RequestMapping(
      value = "/groups/{identifier}",
      method = RequestMethod.GET,
      produces = MediaType.ALL_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = GroupNotFoundException.class)
  Group findGroup(@PathVariable("identifier") final String identifier);

  @RequestMapping(
      value = "/groups/{identifier}/leaders",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = GroupNotFoundException.class)
  void updateLeaders(@PathVariable("identifier") final String identifier, final Set<String> customerIdentifiers);

  @RequestMapping(
      value = "/groups/{identifier}/members",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = GroupNotFoundException.class)
  void updateMembers(@PathVariable("identifier") final String identifier, final Set<String> customerIdentifiers);

  @RequestMapping(
      value = "/groups/{identifier}/employee",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = GroupNotFoundException.class)
  void updateAssignedEmployee(@PathVariable("identifier") final String identifier,
                              final AssignedEmployeeHolder assignedEmployeeHolder);

  @RequestMapping(
      value = "/groups/{identifier}/commands",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = GroupNotFoundException.class)
  void processGroupCommand(@PathVariable("identifier") final String identifier, final GroupCommand groupCommand);

  @RequestMapping(
      value = "/groups/{identifier}/commands",
      method = RequestMethod.GET,
      produces = MediaType.ALL_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = GroupNotFoundException.class)
  List<GroupCommand> fetchGroupCommands(@PathVariable("identifier") final String identifier);

  @RequestMapping(
      value = "/groups/{identifier}/meetings",
      method = RequestMethod.GET,
      produces = MediaType.ALL_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = GroupNotFoundException.class)
  List<Meeting> fetchMeetings(
      @PathVariable("identifier") final String groupIdentifier,
      @RequestParam("upcoming") final Boolean upcoming);

  @RequestMapping(
      value = "/groups/{identifier}/meetings",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ThrowsException(status = HttpStatus.NOT_FOUND, exception = GroupNotFoundException.class)
  void closeMeeting(@PathVariable("identifier") final String groupIdentifier, final SignOffMeeting signOffMeeting);
}
