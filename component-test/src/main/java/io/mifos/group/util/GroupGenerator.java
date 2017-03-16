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
package io.mifos.group.util;

import io.mifos.group.api.v1.domain.Address;
import io.mifos.group.api.v1.domain.Group;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Arrays;
import java.util.HashSet;

public class GroupGenerator {

  private GroupGenerator() {
    super();
  }

  public static Group createRandomGroup(final String definitionIdentifier) {
    final Group group = new Group();
    group.setIdentifier(RandomStringUtils.randomAlphanumeric(32));
    group.setGroupDefinitionIdentifier(definitionIdentifier);
    group.setName(RandomStringUtils.randomAlphanumeric(256));
    group.setOffice(RandomStringUtils.randomAlphanumeric(32));
    group.setAssignedEmployee(RandomStringUtils.randomAlphanumeric(32));
    group.setLeaders(new HashSet<>(Arrays.asList(
        RandomStringUtils.randomAlphanumeric(32), RandomStringUtils.randomAlphanumeric(32)
    )));
    group.setMembers(new HashSet<>(Arrays.asList(
        RandomStringUtils.randomAlphanumeric(32), RandomStringUtils.randomAlphanumeric(32),
        RandomStringUtils.randomAlphanumeric(32), RandomStringUtils.randomAlphanumeric(32),
        RandomStringUtils.randomAlphanumeric(32), RandomStringUtils.randomAlphanumeric(32),
        RandomStringUtils.randomAlphanumeric(32), RandomStringUtils.randomAlphanumeric(32),
        RandomStringUtils.randomAlphanumeric(32), RandomStringUtils.randomAlphanumeric(32)
    )));
    group.setWeekday(Group.Weekday.WEDNESDAY.getValue());
    final Address address = new Address();
    address.setStreet(RandomStringUtils.randomAlphanumeric(256));
    address.setCity(RandomStringUtils.randomAlphanumeric(256));
    address.setRegion(RandomStringUtils.randomAlphanumeric(256));
    address.setPostalCode(RandomStringUtils.randomAlphanumeric(2));
    address.setCountry(RandomStringUtils.randomAlphanumeric(256));
    address.setCountryCode(RandomStringUtils.randomAlphanumeric(2));
    group.setAddress(address);
    return group;
  }
}
