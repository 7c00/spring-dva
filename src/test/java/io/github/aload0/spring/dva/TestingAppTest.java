package io.github.aload0.spring.dva;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestingAppTest {

  @Autowired
  private AppConfig config;

  @Test
  public void test() {
    assertEquals("DVA", config.name());
    assertEquals(8080, config.port());
    assertTrue(config.debug());
    assertArrayEquals(new String[]{"root", "admin"}, config.allowedUsers());
    assertEquals("file", config.dataStore().getType());
    assertEquals("/tmp/data", config.dataStore().getUrl());
  }
}
