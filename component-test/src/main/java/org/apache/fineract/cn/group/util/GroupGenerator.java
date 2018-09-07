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
package org.apache.fineract.cn.group.util;

import org.apache.fineract.cn.group.api.v1.domain.Address;
import org.apache.fineract.cn.group.api.v1.domain.Group;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Arrays;
import java.util.HashSet;

public class GroupGenerator {

  private GroupGenerator() {
    super();
  }

  public static Group createRandomGroup(final String definitionIdentifier) {
    final Group group = new Group();
    group.setIdentifier("grp" + RandomStringUtils.randomAlphanumeric(3));
    group.setGroupDefinitionIdentifier(definitionIdentifier);
    group.setName("groupName" + RandomStringUtils.randomAlphanumeric(2));
    group.setOffice("office" + RandomStringUtils.randomAlphanumeric(4));
    group.setAssignedEmployee("employee" + RandomStringUtils.randomAlphanumeric(3));
    group.setLeaders(new HashSet<>(Arrays.asList("Leader" +
            RandomStringUtils.randomAlphanumeric(3), "Leader" + RandomStringUtils.randomAlphanumeric(3)
    )));
    group.setMembers(new HashSet<>(Arrays.asList(
            "Member" + RandomStringUtils.randomAlphanumeric(3), "Member" + RandomStringUtils.randomAlphanumeric(3)
    )));
    group.setWeekday(Group.Weekday.WEDNESDAY.getValue());
    final Address address = new Address();
    address.setStreet("street" + RandomStringUtils.randomAlphanumeric(4));
    address.setCity("city" + RandomStringUtils.randomAlphanumeric(5));
    address.setRegion("region" + RandomStringUtils.randomAlphanumeric(6));
    address.setPostalCode(RandomStringUtils.randomAlphanumeric(4));
    address.setCountry("country" + RandomStringUtils.randomAlphanumeric(6));
    address.setCountryCode(RandomStringUtils.randomAlphanumeric(2).toUpperCase());
    group.setAddress(address);
    return group;
  }
}