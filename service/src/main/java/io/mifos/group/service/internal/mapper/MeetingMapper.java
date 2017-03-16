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
import io.mifos.group.api.v1.domain.Meeting;
import io.mifos.group.service.internal.repository.MeetingEntity;

public class MeetingMapper {

  private MeetingMapper() {
    super();
  }

  public static Meeting map(final MeetingEntity meetingEntity) {
    final Meeting meeting = new Meeting();
    meeting.setCurrentCycle(meetingEntity.getCurrentCycle());
    meeting.setMeetingSequence(meetingEntity.getMeetingSequence());
    meeting.setScheduledFor(DateConverter.toIsoString(meetingEntity.getScheduledFor()));
    if (meetingEntity.getHeldOn() != null) {
      meeting.setHeldOn(DateConverter.toIsoString(meetingEntity.getHeldOn()));
    }
    if (meetingEntity.getCreatedBy() != null) {
      meeting.setCreatedBy(meetingEntity.getCreatedBy());
      meeting.setCreatedOn(DateConverter.toIsoString(meetingEntity.getCreatedOn()));
    }
    meeting.setDuration(meetingEntity.getDuration());
    return meeting;
  }
}
