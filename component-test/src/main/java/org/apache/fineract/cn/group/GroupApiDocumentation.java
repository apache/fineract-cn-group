package org.apache.fineract.cn.group;

import com.google.gson.Gson;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.fineract.cn.group.api.v1.EventConstants;
import org.apache.fineract.cn.group.api.v1.domain.*;
import org.apache.fineract.cn.group.util.GroupDefinitionGenerator;
import org.apache.fineract.cn.group.util.GroupGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GroupApiDocumentation extends AbstractGroupTest {

  @Rule
  public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("build/doc/generated-snippets/test-group");

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @Before
  public void setUp ( ) {

    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
            .apply(documentationConfiguration(this.restDocumentation))
            .alwaysDo(document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
            .build();
  }

  @Test
  public void documentCreateGroup ( ) throws Exception {

    final Cycle groupCycle = new Cycle();
    groupCycle.setNumberOfMeetings(25);
    groupCycle.setFrequency(Cycle.Frequency.MONTHLY.name());
    groupCycle.setAdjustment(Cycle.Adjustment.NEXT_BUSINESS_DAY.name());

    final GroupDefinition randomGroupDefinition = GroupDefinitionGenerator.createRandomGroupDefinition();
    randomGroupDefinition.setIdentifier("grpDef101");
    randomGroupDefinition.setDescription("Group Defininition 101");
    randomGroupDefinition.setMinimalSize(Integer.valueOf(5));
    randomGroupDefinition.setMaximalSize(Integer.valueOf(10));
    randomGroupDefinition.setCycle(groupCycle);

    this.testSubject.createGroupDefinition(randomGroupDefinition);
    this.eventRecorder.wait(EventConstants.POST_GROUP_DEFINITION, randomGroupDefinition.getIdentifier());

    Set <String> leadership = new HashSet <>();
    leadership.add("Nditapa");
    leadership.add("Imele");

    Set <String> membership = new HashSet <>();
    membership.add("Kamga");
    membership.add("Ayuk");
    membership.add("Awasum");

    final Address address = new Address();
    address.setStreet("Hospital");
    address.setCity("Muyuka");
    address.setRegion("SWR");
    address.setPostalCode("8050");
    address.setCountry("Cameroon");
    address.setCountryCode("CM");

    final Group randomGroup = GroupGenerator.createRandomGroup(randomGroupDefinition.getIdentifier());
    randomGroup.setIdentifier("group001");
    randomGroup.setGroupDefinitionIdentifier(randomGroupDefinition.getIdentifier());
    randomGroup.setName("ARPOT Group");
    randomGroup.setOffice("Bambui");
    randomGroup.setAssignedEmployee("Tabah Tih");
    randomGroup.setWeekday(Group.Weekday.WEDNESDAY.getValue());
    randomGroup.setLeaders(leadership);
    randomGroup.setMembers(membership);
    randomGroup.setAddress(address);

    Gson serialize = new Gson();
    this.mockMvc.perform(post("/groups")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(serialize.toJson(randomGroup)))
            .andExpect(status().isAccepted())
            .andDo(document("document-create-group", preprocessRequest(prettyPrint()),
                    requestFields(
                            fieldWithPath("identifier").description("Group Id"),
                            fieldWithPath("groupDefinitionIdentifier").description("Group definition Id"),
                            fieldWithPath("name").description("Group's name"),
                            fieldWithPath("leaders").type("Set<String>").description("Group's leaders"),
                            fieldWithPath("members").type("Set<String>").description("Group's members"),
                            fieldWithPath("office").description("Group's office"),
                            fieldWithPath("assignedEmployee").description("Assigned Employee"),
                            fieldWithPath("weekday").type("Integer").description("Weekday for group meeting " +
                                    " \n + " +
                                    " *enum* _Weekday_ { + \n" +
                                    "    MONDAY(1), + \n" +
                                    "    TUESDAY(2), + \n" +
                                    "    WEDNESDAY(3), + \n" +
                                    "    THURSDAY(4), + \n" +
                                    "    FRIDAY(5), + \n" +
                                    "    SATURDAY(6), + \n" +
                                    "    SUNDAY(7) + \n " +
                                    " } \n +"),
                            fieldWithPath("address.street").description("Office street"),
                            fieldWithPath("address.city").description("Office city"),
                            fieldWithPath("address.region").description("Office region"),
                            fieldWithPath("address.postalCode").description("Office postal code"),
                            fieldWithPath("address.countryCode").description("Office country code"),
                            fieldWithPath("address.country").description("Office country")
                    )));
  }

  @Test
  public void documentFindGroup ( ) throws Exception {

    final Cycle groupCycle = new Cycle();
    groupCycle.setNumberOfMeetings(12);
    groupCycle.setFrequency(Cycle.Frequency.MONTHLY.name());
    groupCycle.setAdjustment(Cycle.Adjustment.NEXT_BUSINESS_DAY.name());

    final GroupDefinition randomGroupDefinition = GroupDefinitionGenerator.createRandomGroupDefinition();
    randomGroupDefinition.setIdentifier("grpDef202");
    randomGroupDefinition.setDescription("Group Defininition 202");
    randomGroupDefinition.setMinimalSize(Integer.valueOf(5));
    randomGroupDefinition.setMaximalSize(Integer.valueOf(10));
    randomGroupDefinition.setCycle(groupCycle);

    this.testSubject.createGroupDefinition(randomGroupDefinition);
    this.eventRecorder.wait(EventConstants.POST_GROUP_DEFINITION, randomGroupDefinition.getIdentifier());

    Set <String> leadership = new HashSet <>();
    leadership.add("Ndi");
    leadership.add("Mele");

    Set <String> membership = new HashSet <>();
    membership.add("Kamgue");
    membership.add("Etta");
    membership.add("Awasom");

    final Address address = new Address();
    address.setStreet("Ghana Street");
    address.setCity("Bamenda");
    address.setRegion("NWR");
    address.setPostalCode("8050");
    address.setCountry("Cameroon");
    address.setCountryCode("CM");

    final Group randomGroup = GroupGenerator.createRandomGroup(randomGroupDefinition.getIdentifier());
    randomGroup.setIdentifier("group002");
    randomGroup.setGroupDefinitionIdentifier(randomGroupDefinition.getIdentifier());
    randomGroup.setName("SDLE Group");
    randomGroup.setOffice("Bambui");
    randomGroup.setAssignedEmployee("Tabah Tih");
    randomGroup.setWeekday(Group.Weekday.SATURDAY.getValue());
    randomGroup.setLeaders(leadership);
    randomGroup.setMembers(membership);
    randomGroup.setAddress(address);

    this.testSubject.createGroup(randomGroup);
    this.eventRecorder.wait(EventConstants.POST_GROUP, randomGroup.getIdentifier());

    this.mockMvc.perform(get("/groups/" + randomGroup.getIdentifier())
            .accept(MediaType.ALL_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andDo(document("document-find-group", preprocessResponse(prettyPrint()),
                    responseFields(
                            fieldWithPath("identifier").description("Group Id"),
                            fieldWithPath("groupDefinitionIdentifier").description("Group definition Id"),
                            fieldWithPath("name").description("Group's name"),
                            fieldWithPath("leaders").type("Set<String>").description("Group's leaders"),
                            fieldWithPath("members").type("Set<String>").description("Group's members"),
                            fieldWithPath("office").description("Group's office"),
                            fieldWithPath("assignedEmployee").description("Assigned Employee"),
                            fieldWithPath("weekday").type("Integer").description("Weekday for group meeting " +
                                    " \n + " +
                                    " *enum* _Weekday_ { + \n" +
                                    "    MONDAY(1), + \n" +
                                    "    TUESDAY(2), + \n" +
                                    "    WEDNESDAY(3), + \n" +
                                    "    THURSDAY(4), + \n" +
                                    "    FRIDAY(5), + \n" +
                                    "    SATURDAY(6), + \n" +
                                    "    SUNDAY(7) + \n " +
                                    " } \n +"),
                            fieldWithPath("address.street").description("Office street"),
                            fieldWithPath("address.city").description("Office city"),
                            fieldWithPath("address.region").description("Office region"),
                            fieldWithPath("address.postalCode").description("Office postal code"),
                            fieldWithPath("address.countryCode").description("Office country code"),
                            fieldWithPath("address.country").description("Office country"),
                            fieldWithPath("status").description("Status " +
                                    " + \n" +
                                    "*enum* _Status_ { + \n" +
                                    "    PENDING, + \n" +
                                    "    ACTIVE, + \n" +
                                    "    CLOSED + \n" +
                                    "  }"),
                            fieldWithPath("createdOn").description(""),
                            fieldWithPath("createdBy").description(""),
                            fieldWithPath("lastModifiedOn").description(""),
                            fieldWithPath("lastModifiedBy").description("")
                    )));
  }

  @Test
  public void documentActivateGroup ( ) throws Exception {

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
    activate.setNote("Activate Note" + RandomStringUtils.randomAlphanumeric(3));
    activate.setCreatedBy(TestGroup.TEST_USER);
    activate.setCreatedOn(ZonedDateTime.now(Clock.systemUTC()).format(DateTimeFormatter.ISO_ZONED_DATE_TIME));

    Gson serialize = new Gson();
    this.mockMvc.perform(post("/groups/" + randomGroup.getIdentifier() + "/commands")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(serialize.toJson(activate)))
            .andExpect(status().isAccepted())
            .andDo(document("document-activate-group", preprocessRequest(prettyPrint()),
                    requestFields(
                            fieldWithPath("action").description("Action " +
                                    "" +
                                    "*enum* _Action_ { + \n" +
                                    "    ACTIVATE, + \n" +
                                    "    CLOSE, + \n" +
                                    "    REOPEN + \n" +
                                    "  }"),
                            fieldWithPath("note").description("Group NOte"),
                            fieldWithPath("createdBy").description("Assigned Employee to Group"),
                            fieldWithPath("createdOn").type("String").description("Date when group was created")
                    )));
  }

  @Test
  public void documentCloseGroup ( ) throws Exception {

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
    activate.setNote("Activate Note" + RandomStringUtils.randomAlphanumeric(3));
    activate.setCreatedBy(TestGroup.TEST_USER);
    activate.setCreatedOn(ZonedDateTime.now(Clock.systemUTC()).format(DateTimeFormatter.ISO_ZONED_DATE_TIME));

    this.testSubject.processGroupCommand(randomGroup.getIdentifier(), activate);
    this.eventRecorder.wait(EventConstants.ACTIVATE_GROUP, randomGroup.getIdentifier());

    final GroupCommand close = new GroupCommand();
    close.setAction(GroupCommand.Action.CLOSE.name());
    close.setNote("Close Note" + RandomStringUtils.randomAlphanumeric(3));
    close.setCreatedBy(TestGroup.TEST_USER);
    close.setCreatedOn(ZonedDateTime.now(Clock.systemUTC()).format(DateTimeFormatter.ISO_ZONED_DATE_TIME));

    Gson serialize = new Gson();
    this.mockMvc.perform(post("/groups/" + fetchedGroup.getIdentifier() + "/commands")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(serialize.toJson(close)))
            .andExpect(status().isAccepted())
            .andDo(document("document-close-group", preprocessRequest(prettyPrint()),
                    requestFields(
                            fieldWithPath("action").description("Action " +
                                    "" +
                                    "*enum* _Action_ { + \n" +
                                    "    ACTIVATE, + \n" +
                                    "    CLOSE, + \n" +
                                    "    REOPEN + \n" +
                                    "  }"),
                            fieldWithPath("note").description("Group NOte"),
                            fieldWithPath("createdBy").description("Assigned Employee to Group"),
                            fieldWithPath("createdOn").type("String").description("Date when group was created")
                    )));
  }

  @Test
  public void documentReopenGroup ( ) throws Exception {

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
    activate.setNote("Activate Note" + RandomStringUtils.randomAlphanumeric(3));
    activate.setCreatedBy(TestGroup.TEST_USER);
    activate.setCreatedOn(ZonedDateTime.now(Clock.systemUTC()).format(DateTimeFormatter.ISO_ZONED_DATE_TIME));

    this.testSubject.processGroupCommand(randomGroup.getIdentifier(), activate);
    this.eventRecorder.wait(EventConstants.ACTIVATE_GROUP, randomGroup.getIdentifier());

    final GroupCommand close = new GroupCommand();
    close.setAction(GroupCommand.Action.CLOSE.name());
    close.setNote("Close Note" + RandomStringUtils.randomAlphanumeric(3));
    close.setCreatedBy(TestGroup.TEST_USER);
    close.setCreatedOn(ZonedDateTime.now(Clock.systemUTC()).format(DateTimeFormatter.ISO_ZONED_DATE_TIME));

    this.testSubject.processGroupCommand(randomGroup.getIdentifier(), close);
    this.eventRecorder.wait(EventConstants.CLOSE_GROUP, randomGroup.getIdentifier());

    final GroupCommand reopen = new GroupCommand();
    reopen.setAction(GroupCommand.Action.REOPEN.name());
    reopen.setNote("Reopen Note" + RandomStringUtils.randomAlphanumeric(3));
    reopen.setCreatedBy(TestGroup.TEST_USER);
    reopen.setCreatedOn(ZonedDateTime.now(Clock.systemUTC()).format(DateTimeFormatter.ISO_ZONED_DATE_TIME));

    Gson serialize = new Gson();
    this.mockMvc.perform(post("/groups/" + fetchedGroup.getIdentifier() + "/commands")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(serialize.toJson(reopen)))
            .andExpect(status().isAccepted())
            .andDo(document("document-reopen-group", preprocessRequest(prettyPrint()),
                    requestFields(
                            fieldWithPath("action").description("Action " +
                                    "" +
                                    "*enum* _Action_ { + \n" +
                                    "    ACTIVATE, + \n" +
                                    "    CLOSE, + \n" +
                                    "    REOPEN + \n" +
                                    "  }"),
                            fieldWithPath("note").description("Group NOte"),
                            fieldWithPath("createdBy").description("Assigned Employee to Group"),
                            fieldWithPath("createdOn").type("String").description("Date when group was created")
                    )));
  }

  @Test
  public void documentGetGroupCommands ( ) throws Exception {

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
    activate.setNote("Activate Note " + RandomStringUtils.randomAlphanumeric(3));
    activate.setCreatedBy(TestGroup.TEST_USER);
    activate.setCreatedOn(ZonedDateTime.now(Clock.systemUTC()).format(DateTimeFormatter.ISO_ZONED_DATE_TIME));

    this.testSubject.processGroupCommand(randomGroup.getIdentifier(), activate);
    this.eventRecorder.wait(EventConstants.ACTIVATE_GROUP, randomGroup.getIdentifier());

    final Group activatedGroup = this.testSubject.findGroup(randomGroup.getIdentifier());
    Assert.assertEquals(Group.Status.ACTIVE.name(), activatedGroup.getStatus());

    this.mockMvc.perform(get("/groups/" + activatedGroup.getIdentifier() + "/commands")
            .accept(MediaType.ALL_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andDo(document("document-get-group-commands", preprocessResponse(prettyPrint()),
                    responseFields(
                            fieldWithPath("[].action").description("Action " +
                                    "" +
                                    "*enum* _Action_ { + \n" +
                                    "    ACTIVATE, + \n" +
                                    "    CLOSE, + \n" +
                                    "    REOPEN + \n" +
                                    "  }"),
                            fieldWithPath("[].note").description("Group NOte"),
                            fieldWithPath("[].createdBy").description("Assigned Employee to Group"),
                            fieldWithPath("[].createdOn").type("String").description("Date when group was created")
                    )));
  }

  @Test
  public void documentUpdateLeaders ( ) throws Exception {

    final GroupDefinition randomGroupDefinition = GroupDefinitionGenerator.createRandomGroupDefinition();
    this.testSubject.createGroupDefinition(randomGroupDefinition);
    this.eventRecorder.wait(EventConstants.POST_GROUP_DEFINITION, randomGroupDefinition.getIdentifier());

    final Group randomGroup = GroupGenerator.createRandomGroup(randomGroupDefinition.getIdentifier());
    this.testSubject.createGroup(randomGroup);
    this.eventRecorder.wait(EventConstants.POST_GROUP, randomGroup.getIdentifier());

    randomGroup.getLeaders().add(RandomStringUtils.randomAlphanumeric(5));

    Gson gson = new Gson();
    this.mockMvc.perform(put("/groups/" + randomGroup.getIdentifier() + "/leaders")
            .accept(MediaType.ALL_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(gson.toJson(randomGroup.getLeaders())))
            .andExpect(status().isAccepted())
            .andDo(document("document-update-leaders"));
  }

  @Test
  public void documentUpdateMembers ( ) throws Exception {

    final GroupDefinition randomGroupDefinition = GroupDefinitionGenerator.createRandomGroupDefinition();
    this.testSubject.createGroupDefinition(randomGroupDefinition);
    this.eventRecorder.wait(EventConstants.POST_GROUP_DEFINITION, randomGroupDefinition.getIdentifier());

    final Group randomGroup = GroupGenerator.createRandomGroup(randomGroupDefinition.getIdentifier());
    this.testSubject.createGroup(randomGroup);
    this.eventRecorder.wait(EventConstants.POST_GROUP, randomGroup.getIdentifier());

    randomGroup.getMembers().addAll(Arrays.asList(
            "Member" + RandomStringUtils.randomAlphanumeric(3),
            "Member" + RandomStringUtils.randomAlphanumeric(3)
    ));

    Gson gson = new Gson();
    this.mockMvc.perform(put("/groups/" + randomGroup.getIdentifier() + "/members")
            .accept(MediaType.ALL_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(gson.toJson(randomGroup.getMembers())))
            .andExpect(status().isAccepted())
            .andDo(document("document-update-members"));
  }

  @Test
  public void documentUpdateAssignedEmployee ( ) throws Exception {

    final GroupDefinition randomGroupDefinition = GroupDefinitionGenerator.createRandomGroupDefinition();
    this.testSubject.createGroupDefinition(randomGroupDefinition);
    this.eventRecorder.wait(EventConstants.POST_GROUP_DEFINITION, randomGroupDefinition.getIdentifier());

    final Group randomGroup = GroupGenerator.createRandomGroup(randomGroupDefinition.getIdentifier());
    this.testSubject.createGroup(randomGroup);
    this.eventRecorder.wait(EventConstants.POST_GROUP, randomGroup.getIdentifier());

    final AssignedEmployeeHolder anotherEmployee = new AssignedEmployeeHolder();
    anotherEmployee.setIdentifier("Emply" + RandomStringUtils.randomAlphanumeric(3));

    Gson gson = new Gson();
    this.mockMvc.perform(put("/groups/" + randomGroup.getIdentifier() + "/employee")
            .accept(MediaType.ALL_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(gson.toJson(anotherEmployee)))
            .andExpect(status().isAccepted())
            .andDo(document("document-update-assigned-employee", preprocessRequest(prettyPrint()),
                    requestFields(
                            fieldWithPath("identifier").description("Assigned employee identifier")
                    )));
  }

  @Test
  public void documentFetchMeetings ( ) throws Exception {

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

    this.mockMvc.perform(get("/groups/" + activatedGroup.getIdentifier() + "/meetings")
            .param("upcoming", Boolean.FALSE.toString())
            .accept(MediaType.ALL_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andDo(document("document-fetch-meetings"));
  }

  @Test
  public void documentCloseMeeting ( ) throws Exception {

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

    final List <GroupCommand> groupCommands = this.testSubject.fetchGroupCommands(activatedGroup.getIdentifier());
    Assert.assertTrue(groupCommands.size() == 1);
    final GroupCommand groupCommand = groupCommands.get(0);
    Assert.assertEquals(activate.getAction(), groupCommand.getAction());
    Assert.assertEquals(activate.getNote(), groupCommand.getNote());
    Assert.assertEquals(activate.getCreatedBy(), groupCommand.getCreatedBy());
    Assert.assertNotNull(groupCommand.getCreatedOn());

    final List <Meeting> meetings = this.testSubject.fetchMeetings(activatedGroup.getIdentifier(), Boolean.FALSE);
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

    Gson gson = new Gson();
    this.mockMvc.perform(put("/groups/" + randomGroup.getIdentifier() + "/meetings")
            .accept(MediaType.ALL_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(gson.toJson(signOffMeeting)))
            .andExpect(status().isAccepted())
            .andDo(document("document-close-meeting", preprocessRequest(prettyPrint()),
                    requestFields(
                            fieldWithPath("cycle").type("Integer").description("Meetings in cycle"),
                            fieldWithPath("sequence").type("Integer").description("Meeting sequence"),
                            fieldWithPath("attendees").type("Set<String>").description("Set of attendees"),
                            fieldWithPath("duration").type("Long").description("Duration of meeting")
                    )));
  }

  @Test
  public void documentCreateGroupDefinition ( ) throws Exception {

    final GroupDefinition randomGroupDefinition = GroupDefinitionGenerator.createRandomGroupDefinition();

    Gson serialize = new Gson();
    this.mockMvc.perform(post("/definitions")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(serialize.toJson(randomGroupDefinition)))
            .andExpect(status().isAccepted())
            .andDo(document("document-create-group-definition", preprocessRequest(prettyPrint()),
                    requestFields(
                            fieldWithPath("identifier").description("Group definition Identifier"),
                            fieldWithPath("description").description("Group definition description"),
                            fieldWithPath("minimalSize").type("Integer").description("Group's minimum size"),
                            fieldWithPath("maximalSize").type("Integer").description("Group's maximum size"),
                            fieldWithPath("cycle").type("Cycle").description("Group definition's cycle")
                    )));
  }
}
