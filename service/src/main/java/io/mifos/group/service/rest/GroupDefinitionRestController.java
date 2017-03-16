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
package io.mifos.group.service.rest;

import io.mifos.anubis.annotation.AcceptedTokenType;
import io.mifos.anubis.annotation.Permittable;
import io.mifos.core.command.gateway.CommandGateway;
import io.mifos.core.lang.ServiceException;
import io.mifos.group.service.ServiceConstants;
import io.mifos.group.service.internal.command.CreateGroupDefinitionCommand;
import io.mifos.group.service.internal.service.GroupDefinitionService;
import io.mifos.group.api.v1.domain.GroupDefinition;
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

import javax.validation.Valid;
import java.util.List;

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

  @Permittable(AcceptedTokenType.TENANT)
  @RequestMapping(
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> createDefinition(@RequestBody @Valid final GroupDefinition groupDefinition) {
    this.groupDefinitionService.findByIdentifier(groupDefinition.getIdentifier())
        .ifPresent(gd -> {
          throw ServiceException.conflict("Group definition {0} already exists.", gd.getIdentifier());
        });

    this.commandGateway.process(new CreateGroupDefinitionCommand(groupDefinition));
    return ResponseEntity.accepted().build();
  }

  @Permittable(AcceptedTokenType.TENANT)
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

  @Permittable(AcceptedTokenType.TENANT)
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
}
