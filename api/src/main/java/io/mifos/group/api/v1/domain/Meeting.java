/*
 * Copyright 2017 The Mifos Initiative
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
package io.mifos.group.api.v1.domain;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class Meeting {

  @NotBlank
  private Integer meetingSequence;
  @NotBlank
  private String groupIdentifier;
  @NotNull
  private Integer currentCycle;
  @NotEmpty
  @Valid
  private Set<Attendee> attendees;
  private String scheduledFor;
  @Valid
  private Address location;
  private String heldOn;
  private Long duration;
  private String createdOn;
  private String createdBy;

  public Meeting() {
    super();
  }

  public Integer getMeetingSequence() {
    return this.meetingSequence;
  }

  public void setMeetingSequence(final Integer meetingSequence) {
    this.meetingSequence = meetingSequence;
  }

  public String getGroupIdentifier() {
    return this.groupIdentifier;
  }

  public void setGroupIdentifier(final String groupIdentifier) {
    this.groupIdentifier = groupIdentifier;
  }

  public Integer getCurrentCycle() {
    return this.currentCycle;
  }

  public void setCurrentCycle(final Integer currentCycle) {
    this.currentCycle = currentCycle;
  }

  public Set<Attendee> getAttendees() {
    return this.attendees;
  }

  public void setAttendees(final Set<Attendee> attendees) {
    this.attendees = attendees;
  }

  public String getScheduledFor() {
    return this.scheduledFor;
  }

  public void setScheduledFor(final String scheduledFor) {
    this.scheduledFor = scheduledFor;
  }

  public Address getLocation() {
    return this.location;
  }

  public void setLocation(final Address location) {
    this.location = location;
  }

  public String getHeldOn() {
    return this.heldOn;
  }

  public void setHeldOn(final String heldOn) {
    this.heldOn = heldOn;
  }

  public Long getDuration() {
    return this.duration;
  }

  public void setDuration(final Long duration) {
    this.duration = duration;
  }

  public String getCreatedOn() {
    return this.createdOn;
  }

  public void setCreatedOn(final String createdOn) {
    this.createdOn = createdOn;
  }

  public String getCreatedBy() {
    return this.createdBy;
  }

  public void setCreatedBy(final String createdBy) {
    this.createdBy = createdBy;
  }
}
