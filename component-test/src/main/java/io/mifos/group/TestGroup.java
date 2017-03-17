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
import io.mifos.core.test.env.TestEnvironment;
import io.mifos.core.test.fixture.TenantDataStoreContextTestRule;
import io.mifos.core.test.fixture.cassandra.CassandraInitializer;
import io.mifos.core.test.fixture.mariadb.MariaDBInitializer;
import io.mifos.core.test.listener.EnableEventRecording;
import io.mifos.core.test.listener.EventRecorder;
import io.mifos.group.api.v1.EventConstants;
import io.mifos.group.api.v1.client.GroupManager;
import io.mifos.group.api.v1.domain.AssignedEmployeeHolder;
import io.mifos.group.api.v1.domain.Attendee;
import io.mifos.group.api.v1.domain.Group;
import io.mifos.group.api.v1.domain.GroupCommand;
import io.mifos.group.api.v1.domain.GroupDefinition;
import io.mifos.group.api.v1.domain.Meeting;
import io.mifos.group.api.v1.domain.SignOffMeeting;
import io.mifos.group.service.GroupConfiguration;
import io.mifos.group.util.GroupDefinitionGenerator;
import io.mifos.group.util.GroupGenerator;
import org.apache.commons.lang3.RandomStringUtils;
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

import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestGroup {
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

  public TestGroup() {
    super();
  }

  @Before
  public void prepTest() {
    userContext = this.tenantApplicationSecurityEnvironment.createAutoUserContext(TestGroup.TEST_USER);
  }

  @After
  public void cleanTest() {
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
  public void shouldCreateGroup() throws Exception {
    final GroupDefinition randomGroupDefinition = GroupDefinitionGenerator.createRandomGroupDefinition();

    this.testSubject.createGroupDefinition(randomGroupDefinition);

    this.eventRecorder.wait(EventConstants.POST_GROUP_DEFINITION, randomGroupDefinition.getIdentifier());

    final Group randomGroup = GroupGenerator.createRandomGroup(randomGroupDefinition.getIdentifier());

    this.testSubject.createGroup(randomGroup);

    this.eventRecorder.wait(EventConstants.POST_GROUP, randomGroup.getIdentifier());

    final Group fetchedGroup = this.testSubject.findGroup(randomGroup.getIdentifier());
    Assert.assertEquals(randomGroup.getIdentifier(), fetchedGroup.getIdentifier());
    Assert.assertEquals(randomGroup.getGroupDefinitionIdentifier(), fetchedGroup.getGroupDefinitionIdentifier());
    Assert.assertEquals(randomGroup.getName(), fetchedGroup.getName());
    Assert.assertEquals(randomGroup.getOffice(), fetchedGroup.getOffice());
    Assert.assertEquals(randomGroup.getAssignedEmployee(), fetchedGroup.getAssignedEmployee());
    Assert.assertEquals(randomGroup.getWeekday(), fetchedGroup.getWeekday());
    Assert.assertEquals(Group.Status.PENDING.name(), fetchedGroup.getStatus());
    Assert.assertEquals(randomGroup.getLeaders().size(), fetchedGroup.getLeaders().size());
    Assert.assertEquals(randomGroup.getMembers().size(), fetchedGroup.getMembers().size());
    Assert.assertNotNull(fetchedGroup.getCreatedBy());
    Assert.assertNotNull(fetchedGroup.getCreatedOn());
    Assert.assertNull(fetchedGroup.getLastModifiedBy());
    Assert.assertNull(fetchedGroup.getLastModifiedOn());
    Assert.assertNotNull(fetchedGroup.getAddress());
  }

  @Test
  public void shouldActivateCommand() throws Exception {
    final GroupDefinition randomGroupDefinition = GroupDefinitionGenerator.createRandomGroupDefinition();
    this.testSubject.createGroupDefinition(randomGroupDefinition);
    this.eventRecorder.wait(EventConstants.POST_GROUP_DEFINITION, randomGroupDefinition.getIdentifier());

    final Group randomGroup = GroupGenerator.createRandomGroup(randomGroupDefinition.getIdentifier());
    this.testSubject.createGroup(randomGroup);
    this.eventRecorder.wait(EventConstants.POST_GROUP, randomGroup.getIdentifier());

    final Group fetchedGroup = this.testSubject.findGroup(randomGroup.getIdentifier());
    Assert.assertEquals(Group.Status.PENDING.name(), fetchedGroup.getStatus());

    final GroupCommand activate = new GroupCommand();
    activate.setAction(GroupCommand.Action.ACTIVATE.name());
    activate.setNote(RandomStringUtils.randomAlphanumeric(256));
    activate.setCreatedBy(TestGroup.TEST_USER);
    activate.setCreatedOn(ZonedDateTime.now(Clock.systemUTC()).format(DateTimeFormatter.ISO_ZONED_DATE_TIME));

    this.testSubject.processGroupCommand(randomGroup.getIdentifier(), activate);
    this.eventRecorder.wait(EventConstants.ACTIVATE_GROUP, randomGroup.getIdentifier());

    final Group activatedGroup = this.testSubject.findGroup(randomGroup.getIdentifier());
    Assert.assertEquals(Group.Status.ACTIVE.name(), activatedGroup.getStatus());

    final List<GroupCommand> groupCommands = this.testSubject.fetchGroupCommands(activatedGroup.getIdentifier());
    Assert.assertTrue(groupCommands.size() == 1);
    final GroupCommand groupCommand = groupCommands.get(0);
    Assert.assertEquals(activate.getAction(), groupCommand.getAction());
    Assert.assertEquals(activate.getNote(), groupCommand.getNote());
    Assert.assertEquals(activate.getCreatedBy(), groupCommand.getCreatedBy());
    Assert.assertNotNull(groupCommand.getCreatedOn());

    final List<Meeting> meetings = this.testSubject.fetchMeetings(activatedGroup.getIdentifier(), Boolean.FALSE);
    Assert.assertNotNull(meetings);
    Assert.assertEquals(randomGroupDefinition.getCycle().getNumberOfMeetings(), Integer.valueOf(meetings.size()));

    final Meeting meeting2signOff = meetings.get(0);
    final SignOffMeeting signOffMeeting = new SignOffMeeting();
    signOffMeeting.setCycle(meeting2signOff.getCurrentCycle());
    signOffMeeting.setSequence(meeting2signOff.getMeetingSequence());
    signOffMeeting.setDuration(120L);
    signOffMeeting.setAttendees(meeting2signOff.getAttendees()
        .stream()
        .map(attendee -> {
          attendee.setStatus(Attendee.Status.ATTENDED.name());
          return attendee;
        })
        .collect(Collectors.toSet())
    );

    this.testSubject.closeMeeting(activatedGroup.getIdentifier(), signOffMeeting);
    this.eventRecorder.wait(EventConstants.PUT_GROUP, activatedGroup.getIdentifier());
  }

  @Test
  public void shouldUpdateLeaders() throws Exception {
    final GroupDefinition randomGroupDefinition = GroupDefinitionGenerator.createRandomGroupDefinition();
    this.testSubject.createGroupDefinition(randomGroupDefinition);
    this.eventRecorder.wait(EventConstants.POST_GROUP_DEFINITION, randomGroupDefinition.getIdentifier());

    final Group randomGroup = GroupGenerator.createRandomGroup(randomGroupDefinition.getIdentifier());
    this.testSubject.createGroup(randomGroup);
    this.eventRecorder.wait(EventConstants.POST_GROUP, randomGroup.getIdentifier());

    final int currentLeadersSize = randomGroup.getLeaders().size();
    randomGroup.getLeaders().add(RandomStringUtils.randomAlphanumeric(32));
    this.testSubject.updateLeaders(randomGroup.getIdentifier(), randomGroup.getLeaders());
    this.eventRecorder.wait(EventConstants.PUT_GROUP, randomGroup.getIdentifier());

    final Group fetchedGroup = this.testSubject.findGroup(randomGroup.getIdentifier());
    Assert.assertEquals((currentLeadersSize + 1), fetchedGroup.getLeaders().size());
  }

  @Test
  public void shouldUpdateMembers() throws Exception {
    final GroupDefinition randomGroupDefinition = GroupDefinitionGenerator.createRandomGroupDefinition();
    this.testSubject.createGroupDefinition(randomGroupDefinition);
    this.eventRecorder.wait(EventConstants.POST_GROUP_DEFINITION, randomGroupDefinition.getIdentifier());

    final Group randomGroup = GroupGenerator.createRandomGroup(randomGroupDefinition.getIdentifier());
    this.testSubject.createGroup(randomGroup);
    this.eventRecorder.wait(EventConstants.POST_GROUP, randomGroup.getIdentifier());

    final int currentMembersSize = randomGroup.getMembers().size();
    randomGroup.getMembers().addAll(Arrays.asList(
        RandomStringUtils.randomAlphanumeric(32),
        RandomStringUtils.randomAlphanumeric(32)
    ));
    this.testSubject.updateMembers(randomGroup.getIdentifier(), randomGroup.getMembers());
    this.eventRecorder.wait(EventConstants.PUT_GROUP, randomGroup.getIdentifier());

    final Group fetchedGroup = this.testSubject.findGroup(randomGroup.getIdentifier());
    Assert.assertEquals((currentMembersSize + 2), fetchedGroup.getMembers().size());
  }

  @Test
  public void shouldUpdateAssignedEmployee() throws Exception {
    final GroupDefinition randomGroupDefinition = GroupDefinitionGenerator.createRandomGroupDefinition();
    this.testSubject.createGroupDefinition(randomGroupDefinition);
    this.eventRecorder.wait(EventConstants.POST_GROUP_DEFINITION, randomGroupDefinition.getIdentifier());

    final Group randomGroup = GroupGenerator.createRandomGroup(randomGroupDefinition.getIdentifier());
    this.testSubject.createGroup(randomGroup);
    this.eventRecorder.wait(EventConstants.POST_GROUP, randomGroup.getIdentifier());

    final AssignedEmployeeHolder anotherEmployee = new AssignedEmployeeHolder();
    anotherEmployee.setIdentifier(RandomStringUtils.randomAlphanumeric(32));

    this.testSubject.updateAssignedEmployee(randomGroup.getIdentifier(), anotherEmployee);
    this.eventRecorder.wait(EventConstants.PUT_GROUP, randomGroup.getIdentifier());

    final Group fetchedGroup = this.testSubject.findGroup(randomGroup.getIdentifier());
    Assert.assertEquals(anotherEmployee.getIdentifier(), fetchedGroup.getAssignedEmployee());
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
