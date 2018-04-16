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
package org.apache.fineract.cn.group.service.internal.service;

import org.apache.fineract.cn.group.api.v1.domain.Group;
import org.apache.fineract.cn.group.api.v1.domain.GroupCommand;
import org.apache.fineract.cn.group.api.v1.domain.GroupPage;
import org.apache.fineract.cn.group.api.v1.domain.Meeting;
import org.apache.fineract.cn.group.service.ServiceConstants;
import org.apache.fineract.cn.group.service.internal.mapper.AddressMapper;
import org.apache.fineract.cn.group.service.internal.mapper.AttendeeMapper;
import org.apache.fineract.cn.group.service.internal.mapper.GroupCommandMapper;
import org.apache.fineract.cn.group.service.internal.mapper.GroupMapper;
import org.apache.fineract.cn.group.service.internal.mapper.MeetingMapper;
import org.apache.fineract.cn.group.service.internal.repository.AttendeeRepository;
import org.apache.fineract.cn.group.service.internal.repository.GroupCommandRepository;
import org.apache.fineract.cn.group.service.internal.repository.GroupEntity;
import org.apache.fineract.cn.group.service.internal.repository.GroupRepository;
import org.apache.fineract.cn.group.service.internal.repository.MeetingEntity;
import org.apache.fineract.cn.group.service.internal.repository.MeetingRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.fineract.cn.lang.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

  private final Logger logger;
  private final GroupRepository groupRepository;
  private final GroupCommandRepository groupCommandRepository;
  private final MeetingRepository meetingRepository;
  private final AttendeeRepository attendeeRepository;

  @Autowired
  public GroupService(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                      final GroupRepository groupRepository,
                      final GroupCommandRepository groupCommandRepository,
                      final MeetingRepository meetingRepository,
                      final AttendeeRepository attendeeRepository) {
    super();
    this.logger = logger;
    this.groupRepository = groupRepository;
    this.groupCommandRepository = groupCommandRepository;
    this.meetingRepository = meetingRepository;
    this.attendeeRepository = attendeeRepository;
  }

  public Optional<Group> findByIdentifier(final String identifier) {
    final Optional<GroupEntity> optionalGroup = this.groupRepository.findByIdentifier(identifier);
    if (optionalGroup.isPresent()) {
      final GroupEntity groupEntity = optionalGroup.get();
      final Group group = GroupMapper.map(groupEntity);
      group.setAddress(AddressMapper.map(groupEntity.getAddressEntity()));
      return Optional.of(group);
    } else {
      return Optional.empty();
    }
  }

  public GroupPage fetchGroups(final String employee, final Pageable pageable) {
    final Page<GroupEntity> page;
    if (employee != null) {
      page = this.groupRepository.findByAssignedEmployee(employee, pageable);
    } else {
      page = this.groupRepository.findAll(pageable);
    }

    final GroupPage groupPage = new GroupPage();
    groupPage.setGroups(page.map(GroupMapper::map).getContent());
    groupPage.setTotalPages(page.getTotalPages());
    groupPage.setTotalElements(page.getTotalElements());

    return groupPage;
  }

  public List<GroupCommand> findCommandsByIdentifier(final String identifier) {
    final GroupEntity groupEntity =
        this.groupRepository.findByIdentifier(identifier)
            .orElseThrow(() -> ServiceException.notFound("Group {0} not found.", identifier));
    return this.groupCommandRepository.findByGroup(groupEntity)
        .stream()
        .map(GroupCommandMapper::map)
        .collect(Collectors.toList());
  }

  public List<Meeting> findMeetings(final String identifier, final Boolean upcoming) {
    final GroupEntity groupEntity = this.groupRepository.findByIdentifier(identifier)
        .orElseThrow(() -> ServiceException.notFound("Group {0} not found.", identifier));

    final List<MeetingEntity> meetings;
    if (upcoming) {
      meetings = this.meetingRepository.findTopByGroupEntityAndScheduledForAfter(groupEntity, LocalDate.now(Clock.systemUTC()));
    } else {
      meetings = this.meetingRepository.findByGroupEntityOrderByCurrentCycleDescMeetingSequenceDesc(groupEntity);
    }

    return meetings
        .stream()
        .map(meetingEntity -> {
          final Meeting meeting = MeetingMapper.map(meetingEntity);
          meeting.setGroupIdentifier(groupEntity.getIdentifier());
          meeting.setAttendees(
              this.attendeeRepository.findByMeeting(meetingEntity)
                  .stream().map(AttendeeMapper::map).collect(Collectors.toSet())
          );
          meeting.setLocation(AddressMapper.map(groupEntity.getAddressEntity()));
          return meeting;
        })
        .collect(Collectors.toList());
  }
}
