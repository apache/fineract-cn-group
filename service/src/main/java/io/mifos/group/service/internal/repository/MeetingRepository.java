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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Convert;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<MeetingEntity, Long> {

  @Convert(converter = LocalDateConverter.class)
  List<MeetingEntity> findTopByGroupEntityAndScheduledForAfter(final GroupEntity groupEntity, final LocalDate date);

  List<MeetingEntity> findByGroupEntityAndCurrentCycleOrderByMeetingSequenceDesc(
      final GroupEntity groupEntity, final Integer currentCycle);

  List<MeetingEntity> findByGroupEntityOrderByCurrentCycleDescMeetingSequenceDesc(final GroupEntity groupEntity);

  Optional<MeetingEntity> findByGroupEntityAndCurrentCycleAndMeetingSequence(final GroupEntity groupEntity, final Integer cycle, final Integer sequence);
}
