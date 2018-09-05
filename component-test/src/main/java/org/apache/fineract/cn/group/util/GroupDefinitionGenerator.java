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

import org.apache.fineract.cn.group.api.v1.domain.Cycle;
import org.apache.fineract.cn.group.api.v1.domain.GroupDefinition;
import org.apache.commons.lang3.RandomStringUtils;

public class GroupDefinitionGenerator {

  private GroupDefinitionGenerator() {
    super();
  }

  public static GroupDefinition createRandomGroupDefinition() {
    final GroupDefinition groupDefinition = new GroupDefinition();
    groupDefinition.setIdentifier("grpDef" + RandomStringUtils.randomAlphanumeric(3));
    groupDefinition.setDescription("Group Descr " + RandomStringUtils.randomAlphabetic(5));
    groupDefinition.setMinimalSize(10);
    groupDefinition.setMaximalSize(30);
    final Cycle cycle = new Cycle();
    cycle.setNumberOfMeetings(25);
    cycle.setFrequency(Cycle.Frequency.WEEKLY.name());
    cycle.setAdjustment(Cycle.Adjustment.NEXT_BUSINESS_DAY.name());
    groupDefinition.setCycle(cycle);
    return groupDefinition;
  }
}
