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

import javax.validation.Valid;
import java.util.Set;

public class SignOffMeeting {

  @Valid
  private Integer cycle;
  private Integer sequence;
  private Set<Attendee> attendees;
  private Long duration;

  public SignOffMeeting() {
    super();
  }

  public Integer getCycle() {
    return this.cycle;
  }

  public void setCycle(final Integer cycle) {
    this.cycle = cycle;
  }

  public Integer getSequence() {
    return this.sequence;
  }

  public void setSequence(final Integer sequence) {
    this.sequence = sequence;
  }

  public Set<Attendee> getAttendees() {
    return this.attendees;
  }

  public void setAttendees(final Set<Attendee> attendees) {
    this.attendees = attendees;
  }

  public Long getDuration() {
    return this.duration;
  }

  public void setDuration(final Long duration) {
    this.duration = duration;
  }
}
