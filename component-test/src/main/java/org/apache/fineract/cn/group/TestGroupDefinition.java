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
package org.apache.fineract.cn.group;

import org.apache.fineract.cn.group.api.v1.EventConstants;
import org.apache.fineract.cn.group.api.v1.client.GroupManager;
import org.apache.fineract.cn.group.api.v1.domain.GroupDefinition;
import org.apache.fineract.cn.group.util.GroupDefinitionGenerator;
import org.apache.fineract.cn.anubis.test.v1.TenantApplicationSecurityEnvironmentTestRule;
import org.apache.fineract.cn.api.context.AutoUserContext;
import org.apache.fineract.cn.lang.TenantContextHolder;
import org.apache.fineract.cn.test.env.TestEnvironment;
import org.apache.fineract.cn.test.fixture.TenantDataStoreContextTestRule;
import org.apache.fineract.cn.test.fixture.cassandra.CassandraInitializer;
import org.apache.fineract.cn.test.fixture.mariadb.MariaDBInitializer;
import org.apache.fineract.cn.test.listener.EnableEventRecording;
import org.apache.fineract.cn.test.listener.EventRecorder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

public class TestGroupDefinition extends AbstractGroupTest {

  @Test
  public void shouldCreateGroupDefinition ( ) throws Exception {
    final GroupDefinition randomGroupDefinition = GroupDefinitionGenerator.createRandomGroupDefinition();
    this.testSubject.createGroupDefinition(randomGroupDefinition);

    this.eventRecorder.wait(EventConstants.POST_GROUP_DEFINITION, randomGroupDefinition.getIdentifier());

    final GroupDefinition fetchedGroupDefinition = this.testSubject.findGroupDefinition(randomGroupDefinition.getIdentifier());

    Assert.assertEquals(randomGroupDefinition.getIdentifier(), fetchedGroupDefinition.getIdentifier());
    Assert.assertEquals(randomGroupDefinition.getDescription(), fetchedGroupDefinition.getDescription());
    Assert.assertEquals(randomGroupDefinition.getMinimalSize(), fetchedGroupDefinition.getMinimalSize());
    Assert.assertEquals(randomGroupDefinition.getMaximalSize(), fetchedGroupDefinition.getMaximalSize());
    Assert.assertNotNull(fetchedGroupDefinition.getCycle());
    Assert.assertEquals(randomGroupDefinition.getCycle().getNumberOfMeetings(), fetchedGroupDefinition.getCycle().getNumberOfMeetings());
    Assert.assertEquals(randomGroupDefinition.getCycle().getFrequency(), fetchedGroupDefinition.getCycle().getFrequency());
    Assert.assertEquals(randomGroupDefinition.getCycle().getAdjustment(), fetchedGroupDefinition.getCycle().getAdjustment());
    Assert.assertNotNull(fetchedGroupDefinition.getCreatedBy());
    Assert.assertNotNull(fetchedGroupDefinition.getCreateOn());
    Assert.assertNull(fetchedGroupDefinition.getLastModifiedBy());
    Assert.assertNull(fetchedGroupDefinition.getLastModifiedOn());
  }

  @Test
  public void shouldUpdateGroupDefinition ( ) throws Exception {
    final GroupDefinition randomGroupDefinition = GroupDefinitionGenerator.createRandomGroupDefinition();
    this.testSubject.createGroupDefinition(randomGroupDefinition);

    this.eventRecorder.wait(EventConstants.POST_GROUP_DEFINITION, randomGroupDefinition.getIdentifier());

    final GroupDefinition updatedGroupDefinition = GroupDefinitionGenerator.createRandomGroupDefinition();
    updatedGroupDefinition.setIdentifier(randomGroupDefinition.getIdentifier());

    this.testSubject.updateGroupDefinition(updatedGroupDefinition.getIdentifier(), updatedGroupDefinition);

    this.eventRecorder.wait(EventConstants.PUT_GROUP_DEFINITION, randomGroupDefinition.getIdentifier());

    final GroupDefinition fetchedGroupDefinition = this.testSubject.findGroupDefinition(updatedGroupDefinition.getIdentifier());
    Assert.assertNotNull(fetchedGroupDefinition);
    Assert.assertEquals(updatedGroupDefinition.getIdentifier(), fetchedGroupDefinition.getIdentifier());
    Assert.assertEquals(updatedGroupDefinition.getDescription(), fetchedGroupDefinition.getDescription());
    Assert.assertEquals(updatedGroupDefinition.getMinimalSize(), fetchedGroupDefinition.getMinimalSize());
    Assert.assertEquals(updatedGroupDefinition.getMaximalSize(), fetchedGroupDefinition.getMaximalSize());
    Assert.assertNotNull(fetchedGroupDefinition.getCycle());
    Assert.assertEquals(updatedGroupDefinition.getCycle().getNumberOfMeetings(), fetchedGroupDefinition.getCycle().getNumberOfMeetings());
    Assert.assertEquals(updatedGroupDefinition.getCycle().getFrequency(), fetchedGroupDefinition.getCycle().getFrequency());
  }
}
