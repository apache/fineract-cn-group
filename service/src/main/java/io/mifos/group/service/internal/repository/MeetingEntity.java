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
package io.mifos.group.service.internal.repository;

import io.mifos.core.mariadb.util.LocalDateConverter;
import io.mifos.core.mariadb.util.LocalDateTimeConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ptah_meetings")
public class MeetingEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  @Column(name = "meeting_sequence", nullable = false)
  private Integer meetingSequence;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private GroupEntity groupEntity;
  @Column(name = "current_cycle", nullable = false)
  private Integer currentCycle;
  @Column(name = "scheduled_for", nullable = false)
  @Convert(converter = LocalDateConverter.class)
  private LocalDate scheduledFor;
  @Column(name = "held_on", nullable = true)
  @Convert(converter = LocalDateConverter.class)
  private LocalDate heldOn;
  @Column(name = "duration", nullable = true)
  private Long duration;
  @Column(name = "created_on", nullable = false)
  @Convert(converter = LocalDateTimeConverter.class)
  private LocalDateTime createdOn;
  @Column(name = "created_by", nullable = false)
  private String createdBy;

  public MeetingEntity() {
    super();
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Integer getMeetingSequence() {
    return this.meetingSequence;
  }

  public void setMeetingSequence(final Integer meetingSequence) {
    this.meetingSequence = meetingSequence;
  }

  public GroupEntity getGroupEntity() {
    return this.groupEntity;
  }

  public void setGroupEntity(final GroupEntity groupEntity) {
    this.groupEntity = groupEntity;
  }

  public Integer getCurrentCycle() {
    return this.currentCycle;
  }

  public void setCurrentCycle(final Integer currentCycle) {
    this.currentCycle = currentCycle;
  }

  public LocalDate getScheduledFor() {
    return this.scheduledFor;
  }

  public void setScheduledFor(final LocalDate scheduledFor) {
    this.scheduledFor = scheduledFor;
  }

  public LocalDate getHeldOn() {
    return this.heldOn;
  }

  public void setHeldOn(final LocalDate heldOn) {
    this.heldOn = heldOn;
  }

  public Long getDuration() {
    return this.duration;
  }

  public void setDuration(final Long duration) {
    this.duration = duration;
  }

  public LocalDateTime getCreatedOn() {
    return this.createdOn;
  }

  public void setCreatedOn(final LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }

  public String getCreatedBy() {
    return this.createdBy;
  }

  public void setCreatedBy(final String createdBy) {
    this.createdBy = createdBy;
  }
}
