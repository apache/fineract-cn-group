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
package io.mifos.group.service.internal.command.handler;

import io.mifos.core.api.util.UserContextHolder;
import io.mifos.core.command.annotation.Aggregate;
import io.mifos.core.command.annotation.CommandHandler;
import io.mifos.core.command.annotation.EventEmitter;
import io.mifos.core.lang.DateConverter;
import io.mifos.core.lang.ServiceException;
import io.mifos.group.service.internal.command.ActivateGroupCommand;
import io.mifos.group.service.internal.command.CloseGroupCommand;
import io.mifos.group.service.internal.command.CreateGroupCommand;
import io.mifos.group.service.internal.command.CreateGroupDefinitionCommand;
import io.mifos.group.service.internal.command.ReopenGroupCommand;
import io.mifos.group.service.internal.command.SignOffMeetingCommand;
import io.mifos.group.service.internal.command.UpdateAssignedEmployeeCommand;
import io.mifos.group.service.internal.command.UpdateLeadersCommand;
import io.mifos.group.service.internal.command.UpdateMembersCommand;
import io.mifos.group.service.internal.repository.AddressEntity;
import io.mifos.group.service.internal.repository.AddressRepository;
import io.mifos.group.service.internal.repository.AttendeeEntity;
import io.mifos.group.service.internal.repository.AttendeeRepository;
import io.mifos.group.service.internal.repository.GroupCommandEntity;
import io.mifos.group.service.internal.repository.GroupCommandRepository;
import io.mifos.group.service.internal.repository.GroupDefinitionEntity;
import io.mifos.group.service.internal.repository.GroupDefinitionRepository;
import io.mifos.group.service.internal.repository.GroupEntity;
import io.mifos.group.service.internal.repository.GroupRepository;
import io.mifos.group.service.internal.repository.MeetingEntity;
import io.mifos.group.service.internal.repository.MeetingRepository;
import io.mifos.group.api.v1.EventConstants;
import io.mifos.group.api.v1.domain.*;
import io.mifos.group.service.internal.mapper.AddressMapper;
import io.mifos.group.service.internal.mapper.GroupCommandMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Aggregate
public class GroupAggregate {

  private final GroupDefinitionRepository groupDefinitionRepository;
  private final GroupRepository groupRepository;
  private final GroupCommandRepository groupCommandRepository;
  private final MeetingRepository meetingRepository;
  private final AttendeeRepository attendeeRepository;
  private final AddressRepository addressRepository;

  @Autowired
  public GroupAggregate(final GroupDefinitionRepository groupDefinitionRepository,
                        final GroupRepository groupRepository,
                        final GroupCommandRepository groupCommandRepository,
                        final MeetingRepository meetingRepository,
                        final AttendeeRepository attendeeRepository,
                        final AddressRepository addressRepository) {
    super();
    this.groupDefinitionRepository = groupDefinitionRepository;
    this.groupRepository = groupRepository;
    this.groupCommandRepository = groupCommandRepository;
    this.meetingRepository = meetingRepository;
    this.attendeeRepository = attendeeRepository;
    this.addressRepository = addressRepository;
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.POST_GROUP_DEFINITION)
  public String createDefinition(final CreateGroupDefinitionCommand createGroupDefinitionCommand) {
    final GroupDefinition groupDefinition = createGroupDefinitionCommand.groupDefinition();
    final GroupDefinitionEntity groupDefinitionEntity = new GroupDefinitionEntity();
    groupDefinitionEntity.setIdentifier(groupDefinition.getIdentifier());
    groupDefinitionEntity.setDescription(groupDefinition.getDescription());
    groupDefinitionEntity.setMinimalSize(groupDefinition.getMinimalSize());
    groupDefinitionEntity.setMaximalSize(groupDefinition.getMaximalSize());
    final Cycle cycle = groupDefinition.getCycle();
    groupDefinitionEntity.setNumberOfMeetings(cycle.getNumberOfMeetings());
    groupDefinitionEntity.setFrequency(cycle.getFrequency());
    groupDefinitionEntity.setAdjustment(cycle.getAdjustment());
    groupDefinitionEntity.setCreatedBy(UserContextHolder.checkedGetUser());
    groupDefinitionEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
    this.groupDefinitionRepository.save(groupDefinitionEntity);

    return groupDefinition.getIdentifier();
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.POST_GROUP)
  public String createGroup(final CreateGroupCommand createGroupCommand) {
    final Group group = createGroupCommand.group();
    final GroupDefinitionEntity groupDefinitionEntity =
        this.groupDefinitionRepository.findByIdentifier(group.getGroupDefinitionIdentifier())
            .orElseThrow(
                () -> ServiceException.notFound("Group definition {0} not found.", group.getGroupDefinitionIdentifier())
            );

    final AddressEntity savedAddress = this.addressRepository.save(AddressMapper.map(group.getAddress()));

    final GroupEntity groupEntity = new GroupEntity();
    groupEntity.setGroupDefinition(groupDefinitionEntity);
    groupEntity.setIdentifier(group.getIdentifier());
    groupEntity.setName(group.getName());
    groupEntity.setLeaders(StringUtils.collectionToCommaDelimitedString(group.getLeaders()));
    groupEntity.setMembers(StringUtils.collectionToCommaDelimitedString(group.getMembers()));
    groupEntity.setOffice(group.getOffice());
    groupEntity.setAddressEntity(savedAddress);
    groupEntity.setAssignedEmployee(group.getAssignedEmployee());
    groupEntity.setWeekday(group.getWeekday());
    groupEntity.setGroupStatus(Group.Status.PENDING.name());
    groupEntity.setCreatedBy(UserContextHolder.checkedGetUser());
    groupEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
    this.groupRepository.save(groupEntity);
    return group.getIdentifier();
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.ACTIVATE_GROUP)
  public String activateGroup(final ActivateGroupCommand activateGroupCommand) {
    this.groupRepository.findByIdentifier(activateGroupCommand.identifier())
        .ifPresent(groupEntity -> {
          final GroupEntity savedGroupEntity = this.processCommandInternally(groupEntity, activateGroupCommand.groupCommand());
          this.createMeetingSchedule(groupEntity.getGroupDefinition(), savedGroupEntity);
        });
    return activateGroupCommand.identifier();
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.CLOSE_GROUP)
  public String closeGroup(final CloseGroupCommand closeGroupCommand) {
    this.groupRepository.findByIdentifier(closeGroupCommand.identifier())
        .ifPresent(groupEntity -> {
          final List<MeetingEntity> currentMeetings =
              this.meetingRepository.findByGroupEntityAndCurrentCycleOrderByMeetingSequenceDesc(groupEntity, groupEntity.getCurrentCycle());
          if (currentMeetings.stream().anyMatch(meetingEntity -> meetingEntity.getHeldOn() == null)) {
            throw ServiceException.conflict("Not all meetings for group {0} signed off.", closeGroupCommand.identifier());
          }
          this.processCommandInternally(groupEntity, closeGroupCommand.groupCommand());
        });
    return closeGroupCommand.identifier();
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.REOPEN_GROUP)
  public String reopenGroup(final ReopenGroupCommand reopenGroupCommand) {
    this.groupRepository.findByIdentifier(reopenGroupCommand.identifier())
        .ifPresent(groupEntity -> {
          final GroupEntity savedGroupEntity = this.processCommandInternally(groupEntity, reopenGroupCommand.groupCommand());
          this.createMeetingSchedule(groupEntity.getGroupDefinition(), savedGroupEntity);
        });
    return reopenGroupCommand.identifier();
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.PUT_GROUP)
  public String updateLeaders(final UpdateLeadersCommand updateLeadersCommand) {
    this.groupRepository.findByIdentifier(updateLeadersCommand.identifier())
        .ifPresent(groupEntity -> {
          groupEntity.setLeaders(StringUtils.collectionToCommaDelimitedString(updateLeadersCommand.customerIdentifiers()));
          groupEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
          groupEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));
          this.groupRepository.save(groupEntity);
        });
    return updateLeadersCommand.identifier();
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.PUT_GROUP)
  public String updateMembers(final UpdateMembersCommand updateMembersCommand) {
    this.groupRepository.findByIdentifier(updateMembersCommand.identifier())
        .ifPresent(groupEntity -> {
          groupEntity.setMembers(StringUtils.collectionToCommaDelimitedString(updateMembersCommand.customerIdentifiers()));
          groupEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
          groupEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));
          this.groupRepository.save(groupEntity);
        });
    return updateMembersCommand.identifier();
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.PUT_GROUP)
  public String updateAssignedEmployee(final UpdateAssignedEmployeeCommand updateAssignedEmployeeCommand) {
    this.groupRepository.findByIdentifier(updateAssignedEmployeeCommand.identifier())
        .ifPresent(groupEntity -> {
          groupEntity.setAssignedEmployee(updateAssignedEmployeeCommand.employeeIdentifier());
          groupEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
          groupEntity.setLastModifiedOn(LocalDateTime.now(Clock.systemUTC()));
          this.groupRepository.save(groupEntity);
        });
    return updateAssignedEmployeeCommand.identifier();
  }

  @Transactional
  @CommandHandler
  @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.PUT_GROUP)
  public String signOffMeeting(final SignOffMeetingCommand signOffMeetingCommand) {
    this.groupRepository.findByIdentifier(signOffMeetingCommand.groupIdentifier())
        .ifPresent(groupEntity -> {
          final SignOffMeeting signOffMeeting = signOffMeetingCommand.signOffMeeting();
          this.meetingRepository
              .findByGroupEntityAndCurrentCycleAndMeetingSequence(groupEntity,
                  signOffMeeting.getCycle(), signOffMeeting.getSequence())
              .ifPresent(meetingEntity -> {
                meetingEntity.setDuration(signOffMeeting.getDuration());
                meetingEntity.setHeldOn(LocalDate.now(Clock.systemUTC()));
                this.meetingRepository.save(meetingEntity);

                final List<AttendeeEntity> attendeeEntities = this.attendeeRepository.findByMeeting(meetingEntity);
                attendeeEntities.forEach(attendeeEntity -> signOffMeeting.getAttendees()
                    .stream()
                    .filter(attendee -> attendee.getCustomerIdentifier().equals(attendeeEntity.getCustomerIdentifier()))
                    .findFirst()
                    .ifPresent(attendee -> {
                      attendeeEntity.setStatus(attendee.getStatus());
                      this.attendeeRepository.save(attendeeEntity);
                }));
              });
        });
    return signOffMeetingCommand.groupIdentifier();
  }

  private void createMeetingSchedule(final GroupDefinitionEntity groupDefinitionEntity, final GroupEntity groupEntity) {
    final Integer numberOfMeetings = groupDefinitionEntity.getNumberOfMeetings();
    final Cycle.Frequency frequency = Cycle.Frequency.valueOf(groupDefinitionEntity.getFrequency());
    final Cycle.Adjustment adjustment = Cycle.Adjustment.valueOf(groupDefinitionEntity.getAdjustment());
    final Group.Weekday weekday = Group.Weekday.from(groupEntity.getWeekday());

    final Set<String> members = StringUtils.commaDelimitedListToSet(groupEntity.getMembers());

    LocalDate meeting = LocalDate.now(Clock.systemUTC());
    if (frequency != Cycle.Frequency.DAILY) {
      meeting = meeting.with(ChronoField.DAY_OF_WEEK, weekday.getValue());
    }

    for (int i = 0; i < numberOfMeetings; i++) {
      switch (frequency) {
        case DAILY:
          meeting = meeting.plusDays(1L);
          break;
        case WEEKLY:
          meeting = meeting.plusWeeks(1L);
          break;
        case FORTNIGHTLY:
          meeting = meeting.plusWeeks(2L);
          break;
        case MONTHLY:
          meeting.plusMonths(1L);
          break;
      }

      final MeetingEntity meetingEntity = new MeetingEntity();
      meetingEntity.setGroupEntity(groupEntity);
      meetingEntity.setCurrentCycle(groupEntity.getCurrentCycle());
      meetingEntity.setMeetingSequence((i + 1));
      meetingEntity.setScheduledFor(meeting);
      meetingEntity.setCreatedBy(UserContextHolder.checkedGetUser());
      meetingEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
      final MeetingEntity savedMeeting = this.meetingRepository.save(meetingEntity);

      this.attendeeRepository.save(
          members
              .stream()
              .map(member -> {
                final AttendeeEntity attendeeEntity = new AttendeeEntity();
                attendeeEntity.setMeeting(savedMeeting);
                attendeeEntity.setCustomerIdentifier(member);
                attendeeEntity.setStatus(Attendee.Status.EXPECTED.name());
                return attendeeEntity;
              })
              .collect(Collectors.toList())
      );
    }
  }

  private GroupEntity processCommandInternally(final GroupEntity groupEntity, final GroupCommand groupCommand) {
    this.saveGroupCommand(groupEntity, groupCommand);

    final GroupCommand.Action action = GroupCommand.Action.valueOf(groupCommand.getAction());
    switch (action) {
      case ACTIVATE:
        groupEntity.setGroupStatus(Group.Status.ACTIVE.name());
        groupEntity.setCurrentCycle(groupEntity.getCurrentCycle() + 1);
        break;
      case CLOSE:
        groupEntity.setGroupStatus(Group.Status.CLOSED.name());
        break;
      case REOPEN:
        groupEntity.setGroupStatus(Group.Status.PENDING.name());
        groupEntity.setCurrentCycle(groupEntity.getCurrentCycle() + 1);
        break;
      default:
        throw ServiceException.badRequest("Unsupported command {}.", action.name());
    }
    groupEntity.setLastModifiedBy(groupCommand.getCreatedBy());
    groupEntity.setLastModifiedOn(DateConverter.fromIsoString(groupCommand.getCreatedOn()));
    return this.groupRepository.save(groupEntity);
  }

  private void saveGroupCommand(final GroupEntity groupEntity, final GroupCommand groupCommand) {
    final GroupCommandEntity groupCommandEntity = GroupCommandMapper.map(groupCommand);
    groupCommandEntity.setGroup(groupEntity);
    this.groupCommandRepository.save(groupCommandEntity);
  }
}
