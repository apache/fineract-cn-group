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
import io.mifos.group.service.internal.command.SignOffMeetingCommand;
import io.mifos.group.service.internal.command.UpdateLeadersCommand;
import io.mifos.group.service.internal.command.UpdateMembersCommand;
import io.mifos.group.service.internal.service.GroupDefinitionService;
import io.mifos.group.service.internal.service.GroupService;
import io.mifos.group.api.v1.domain.AssignedEmployeeHolder;
import io.mifos.group.api.v1.domain.Group;
import io.mifos.group.api.v1.domain.GroupCommand;
import io.mifos.group.api.v1.domain.GroupPage;
import io.mifos.group.api.v1.domain.Meeting;
import io.mifos.group.api.v1.domain.SignOffMeeting;
import io.mifos.group.service.ServiceConstants;
import io.mifos.group.service.internal.command.ActivateGroupCommand;
import io.mifos.group.service.internal.command.CloseGroupCommand;
import io.mifos.group.service.internal.command.CreateGroupCommand;
import io.mifos.group.service.internal.command.ReopenGroupCommand;
import io.mifos.group.service.internal.command.UpdateAssignedEmployeeCommand;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/groups")
public class GroupRestController {

  private final Logger logger;
  private final CommandGateway commandGateway;
  private final GroupService groupService;
  private final GroupDefinitionService groupDefinitionService;


  @Autowired
  public GroupRestController(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                             final CommandGateway commandGateway,
                             final GroupService groupService,
                             final GroupDefinitionService groupDefinitionService) {
    super();
    this.logger = logger;
    this.commandGateway = commandGateway;
    this.groupService = groupService;
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
  ResponseEntity<Void> createGroup(@RequestBody @Valid final Group group) {
    this.groupService.findByIdentifier(group.getIdentifier())
        .ifPresent(g -> {
          throw ServiceException.conflict("Group {0} already exists.", g.getIdentifier());
        });

    if (!this.groupDefinitionService.findByIdentifier(group.getGroupDefinitionIdentifier()).isPresent()) {
      throw ServiceException.notFound("Unknown group definition {0}.", group.getGroupDefinitionIdentifier());
    }

    this.commandGateway.process(new CreateGroupCommand(group));
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
  ResponseEntity<GroupPage> fetchGroups(
      @RequestParam("employee") final String employee,
      @RequestParam("page") final Integer page,
      @RequestParam("size") final Integer size,
      @RequestParam("sortColumn") final String sortColumn,
      @RequestParam("sortDirection") final String sortDirection) {
    return ResponseEntity.ok(
        this.groupService.fetchGroups(employee, this.createPageRequest(page, size, sortColumn, sortDirection))
    );
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
  ResponseEntity<Group> findByIdentifier(@PathVariable("identifier") final String identifier) {
    return this.groupService.findByIdentifier(identifier)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> ServiceException.notFound("Group {0} not found.", identifier));
  }

  @RequestMapping(
      value = "/{identifier}/commands",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> processGroupCommand(@PathVariable("identifier") final String identifier, @RequestBody final GroupCommand groupCommand) {
    this.groupService.findByIdentifier(identifier).orElseThrow(() -> ServiceException.notFound("Group {0} not found.", identifier));
    final GroupCommand.Action action = GroupCommand.Action.valueOf(groupCommand.getAction());
    switch (action) {
      case ACTIVATE:
        this.commandGateway.process(new ActivateGroupCommand(identifier, groupCommand));
        break;
      case CLOSE:
        this.commandGateway.process(new CloseGroupCommand(identifier, groupCommand));
        break;
      case REOPEN:
        this.commandGateway.process(new ReopenGroupCommand(identifier, groupCommand));
        break;
      default:
        throw ServiceException.badRequest("Unsupported command {0}.", action.name());
    }
    return ResponseEntity.accepted().build();
  }

  @RequestMapping(
      value = "/{identifier}/commands",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<List<GroupCommand>> getGroupCommands(@PathVariable("identifier") final String identifier) {
    return ResponseEntity.ok(this.groupService.findCommandsByIdentifier(identifier));
  }

  @RequestMapping(
      value = "/{identifier}/leaders",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> updateLeaders(@PathVariable("identifier") final String identifier,
                                     @RequestBody final Set<String> customerIdentifiers) {
    this.groupService.findByIdentifier(identifier)
        .orElseThrow(() -> ServiceException.notFound("Group {0} not found.", identifier));

    this.commandGateway.process(new UpdateLeadersCommand(identifier, customerIdentifiers));

    return ResponseEntity.accepted().build();
  }

  @RequestMapping(
      value = "/{identifier}/members",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> updateMembers(@PathVariable("identifier") final String identifier,
                                     @RequestBody final Set<String> customerIdentifiers) {
    this.groupService.findByIdentifier(identifier)
        .orElseThrow(() -> ServiceException.notFound("Group {0} not found.", identifier));

    this.commandGateway.process(new UpdateMembersCommand(identifier, customerIdentifiers));

    return ResponseEntity.accepted().build();
  }

  @RequestMapping(
      value = "/{identifier}/employee",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> updateAssignedEmployee(@PathVariable("identifier") final String identifier,
                                              @RequestBody final AssignedEmployeeHolder assignedEmployeeHolder) {

    this.groupService.findByIdentifier(identifier)
        .orElseThrow(() -> ServiceException.notFound("Group {0} not found.", identifier));

    this.commandGateway.process(new UpdateAssignedEmployeeCommand(identifier, assignedEmployeeHolder.getIdentifier()));

    return ResponseEntity.accepted().build();
  }

  @RequestMapping(
      value = "/{identifier}/meetings",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<List<Meeting>> fetchMeetings(@PathVariable("identifier") final String groupIdentifier,
                                              @RequestParam(value = "upcoming", required = false, defaultValue = "false") final Boolean upcoming) {
    this.groupService.findByIdentifier(groupIdentifier)
        .orElseThrow(() -> ServiceException.notFound("Group {0} not found.", groupIdentifier));
    return ResponseEntity.ok(this.groupService.findMeetings(groupIdentifier, upcoming));
  }

  @RequestMapping(
      value = "/{identifier}/meetings",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public
  ResponseEntity<Void> closeMeeting(@PathVariable("identifier") final String groupIdentifier,
                                    @RequestBody final SignOffMeeting signOffMeeting) {
    this.groupService.findByIdentifier(groupIdentifier)
        .orElseThrow(() -> ServiceException.notFound("Group {0} not found.", groupIdentifier));

    this.commandGateway.process(new SignOffMeetingCommand(groupIdentifier, signOffMeeting));
    return ResponseEntity.accepted().build();
  }

  private Pageable createPageRequest(final Integer page, final Integer size, final String sortColumn, final String sortDirection) {
    final Integer pageToUse = page != null ? page : 0;
    final Integer sizeToUse = size != null ? size : 20;
    final String sortColumnToUse = sortColumn != null ? sortColumn : "identifier";
    final Sort.Direction direction = sortDirection != null ? Sort.Direction.valueOf(sortDirection.toUpperCase()) : Sort.Direction.ASC;
    return new PageRequest(pageToUse, sizeToUse, direction, sortColumnToUse);
  }
}
