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
package io.mifos.group;

import io.mifos.anubis.test.v1.TenantApplicationSecurityEnvironmentTestRule;
import io.mifos.core.api.context.AutoUserContext;
import io.mifos.core.lang.TenantContextHolder;
import io.mifos.core.test.env.TestEnvironment;
import io.mifos.core.test.fixture.TenantDataStoreContextTestRule;
import io.mifos.core.test.fixture.cassandra.CassandraInitializer;
import io.mifos.core.test.fixture.mariadb.MariaDBInitializer;
import io.mifos.core.test.listener.EnableEventRecording;
import io.mifos.core.test.listener.EventRecorder;
import io.mifos.group.api.v1.EventConstants;
import io.mifos.group.api.v1.client.GroupManager;
import io.mifos.group.api.v1.domain.GroupDefinition;
import io.mifos.group.service.GroupConfiguration;
import io.mifos.group.util.GroupDefinitionGenerator;
import org.junit.*;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestGroupDefinition {
  private static final String APP_NAME = "group-v1";
  private static final String TEST_USER = "ranefer";

  private final static TestEnvironment testEnvironment = new TestEnvironment(APP_NAME);
  private final static CassandraInitializer cassandraInitializer = new CassandraInitializer();
  private final static MariaDBInitializer mariaDBInitializer = new MariaDBInitializer();
  private final static TenantDataStoreContextTestRule tenantDataStoreContext = TenantDataStoreContextTestRule.forRandomTenantName(cassandraInitializer, mariaDBInitializer);

  @ClassRule
  public static TestRule orderClassRules = RuleChain
          .outerRule(testEnvironment)
          .around(cassandraInitializer)
          .around(mariaDBInitializer)
          .around(tenantDataStoreContext);

  @Rule
  public final TenantApplicationSecurityEnvironmentTestRule tenantApplicationSecurityEnvironment
          = new TenantApplicationSecurityEnvironmentTestRule(testEnvironment, this::waitForInitialize);

  @Autowired
  private GroupManager testSubject;
  @Autowired
  private EventRecorder eventRecorder;

  private AutoUserContext userContext;

  public TestGroupDefinition() {
    super();
  }

  @Before
  public void prepTest() {
    userContext = this.tenantApplicationSecurityEnvironment.createAutoUserContext(TestGroupDefinition.TEST_USER);
  }

  @After
  public void cleanTest() {
    TenantContextHolder.clear();
    userContext.close();
  }

  public boolean waitForInitialize() {
    try {
      return this.eventRecorder.wait(EventConstants.INITIALIZE, EventConstants.INITIALIZE);
    } catch (final InterruptedException e) {
      throw new IllegalStateException(e);
    }
  }

  @Test
  public void shouldCreateGroupDefinition() throws Exception {
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

  @Configuration
  @EnableEventRecording
  @EnableFeignClients(basePackages = {"io.mifos.group.api.v1.client"})
  @RibbonClient(name = APP_NAME)
  @Import({GroupConfiguration.class})
  @ComponentScan("io.mifos.group.listener")
  public static class TestConfiguration {
    public TestConfiguration() {
      super();
    }

    @Bean()
    public Logger logger() {
      return LoggerFactory.getLogger("test-logger");
    }
  }
}
