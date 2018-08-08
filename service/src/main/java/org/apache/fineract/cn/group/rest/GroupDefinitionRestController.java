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
package org.apache.fineract.cn.group.rest;

import org.apache.fineract.cn.group.api.v1.PermittableGroupIds;
import org.apache.fineract.cn.group.api.v1.domain.GroupDefinition;
import org.apache.fineract.cn.group.ServiceConstants;
import org.apache.fineract.cn.group.internal.command.CreateGroupDefinitionCommand;
import org.apache.fineract.cn.group.internal.command.UpdateGroupDefinitionCommand;
import org.apache.fineract.cn.group.internal.service.GroupDefinitionService;
import java.util.List;
import javax.validation.Valid;
import org.apache.fineract.cn.anubis.annotation.AcceptedTokenType;
import org.apache.fineract.cn.anubis.annotation.Permittable;
import org.apache.fineract.cn.command.gateway.CommandGateway;
import org.apache.fineract.cn.lang.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/definitions")
public class GroupDefinitionRestController {

  private final Logger logger;
  private final CommandGateway commandGateway;
  private final GroupDefinitionService groupDefinitionService;

  @Autowired
  public GroupDefinitionRestController(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                                       final CommandGateway commandGateway,
                                       final GroupDefinitionService groupDefinitionService) {
    super();
    this.logger = logger;
    this.commandGateway = commandGateway;
    this.groupDefinitionService = groupDefinitionService;
  }

  @Permittable(value= AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION)
  @RequestMapping(
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> createDefinition(@RequestBody @Valid final GroupDefinition groupDefinition) {
    if (this.groupDefinitionService.groupDefinitionExists(groupDefinition.getIdentifier())) {
      throw ServiceException.conflict("Group definition {0} already exists.", groupDefinition.getIdentifier());
    }
      this.commandGateway.process(new CreateGroupDefinitionCommand(groupDefinition));
    return ResponseEntity.accepted().build();

//    this.groupDefinitionService.findByIdentifier(groupDefinition.getIdentifier())
//        .ifPresent(gd -> {
//          throw ServiceException.conflict("Group definition {0} already exists.", gd.getIdentifier());
//        });
//
//    this.commandGateway.process(new CreateGroupDefinitionCommand(groupDefinition));
//    return ResponseEntity.accepted().build();
  }

  @Permittable(value= AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION)
  @RequestMapping(
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<List<GroupDefinition>> fetchAllGroupDefinitions() {
    return ResponseEntity.ok(this.groupDefinitionService.fetchAllGroupDefinitions());
  }

  @Permittable(value= AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION)
  @RequestMapping(
      value = "/{identifier}",
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<GroupDefinition> findGroupDefinitionByIdentifier(
      @PathVariable("identifier") final String identifier) {
    return this.groupDefinitionService.findByIdentifier(identifier)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> ServiceException.notFound("Group definition {0} not found.", identifier));
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DEFINITION)
  @RequestMapping(
          value = "/{identifier}",
          method = RequestMethod.PUT,
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> updateGroupDefinition(@PathVariable("identifier") final String identifier, @RequestBody final GroupDefinition groupDefinition) {
    this.groupDefinitionService.findByIdentifier(identifier)
            .orElseThrow(() -> ServiceException.notFound("Group Definition {0} not found.", identifier));

    this.commandGateway.process(new UpdateGroupDefinitionCommand(groupDefinition));

    return ResponseEntity.accepted().build();
  }

    // if (this.groupDefinitionService.groupDefinitionExists(identifier)) {
     // this.commandGateway.process(new UpdateGroupDefinitionCommand(identifier, groupDefinition));
    //} else {
      //throw ServiceException.notFound("Group Definition {0} not found.", identifier);
    //}
    //return ResponseEntity.accepted().build();
 // }
}
