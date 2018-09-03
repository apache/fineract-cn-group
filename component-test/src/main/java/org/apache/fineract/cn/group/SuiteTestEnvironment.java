package org.apache.fineract.cn.group;

import org.apache.fineract.cn.test.env.TestEnvironment;
import org.apache.fineract.cn.test.fixture.TenantDataStoreContextTestRule;
import org.apache.fineract.cn.test.fixture.cassandra.CassandraInitializer;
import org.apache.fineract.cn.test.fixture.mariadb.MariaDBInitializer;
import org.junit.ClassRule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

public class SuiteTestEnvironment {
  static final String APP_NAME = "group-v1";
  static final String TEST_USER = "ranefer";

  static final TestEnvironment testEnvironment = new TestEnvironment(APP_NAME);
  static final CassandraInitializer cassandraInitializer = new CassandraInitializer();
  static final MariaDBInitializer mariaDBInitializer = new MariaDBInitializer();
  static final TenantDataStoreContextTestRule tenantDataStoreContext =
          TenantDataStoreContextTestRule.forRandomTenantName(cassandraInitializer, mariaDBInitializer);

  @ClassRule
  public static TestRule orderClassRules = RuleChain
          .outerRule(testEnvironment)
          .around(cassandraInitializer)
          .around(mariaDBInitializer)
          .around(tenantDataStoreContext);
}