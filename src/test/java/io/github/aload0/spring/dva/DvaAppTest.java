package io.github.aload0.spring.dva;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DvaAppTest {
  @Autowired
  private AppConfig config;

  @Test
  public void testDvaApp() {
    assertEquals("DVA", config.name());
    assertEquals(8080, config.port());
  }
}
