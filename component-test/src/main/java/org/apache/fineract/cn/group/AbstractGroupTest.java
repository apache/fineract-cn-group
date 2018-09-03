package org.apache.fineract.cn.group;

import org.apache.fineract.cn.anubis.test.v1.TenantApplicationSecurityEnvironmentTestRule;
import org.apache.fineract.cn.api.context.AutoUserContext;
import org.apache.fineract.cn.group.api.v1.EventConstants;
import org.apache.fineract.cn.group.api.v1.client.GroupManager;
import org.apache.fineract.cn.test.listener.EnableEventRecording;
import org.apache.fineract.cn.test.listener.EventRecorder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = {AbstractGroupTest.TestConfiguration.class})
public class AbstractGroupTest extends SuiteTestEnvironment {

  @Rule
  public final TenantApplicationSecurityEnvironmentTestRule tenantApplicationSecurityEnvironment
          = new TenantApplicationSecurityEnvironmentTestRule(testEnvironment, this::waitForInitialize);
  @Autowired
  GroupManager testSubject;

  @Autowired
  EventRecorder eventRecorder;

  AutoUserContext userContext;

  @Before
  public void prepTest ( ) {
    userContext = this.tenantApplicationSecurityEnvironment.createAutoUserContext(TEST_USER);
  }

  @After
  public void cleanTest ( ) {
    userContext.close();
  }

  public boolean waitForInitialize ( ) {
    try {
      return this.eventRecorder.wait(EventConstants.INITIALIZE, EventConstants.INITIALIZE);
    } catch (final InterruptedException e) {
      throw new IllegalStateException(e);
    }
  }

  @Configuration
  @EnableEventRecording
  @EnableFeignClients(basePackages = {"org.apache.fineract.cn.group.api.v1.client"})
  @RibbonClient(name = APP_NAME)
  @Import({GroupConfiguration.class})
  @ComponentScan("org.apache.fineract.cn.group.listener")
  public static class TestConfiguration {
    public TestConfiguration ( ) {
      super();
    }

    @Bean()
    public Logger logger ( ) {
      return LoggerFactory.getLogger("test-logger");
    }
  }
}
