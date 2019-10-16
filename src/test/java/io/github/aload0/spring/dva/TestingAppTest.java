package io.github.aload0.spring.dva;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestingAppTest {

  private final FileHandler handler = new FileHandler(
      new File(TestingConfiguration.LOCAL_FILE));


  @Autowired
  private AppConfig config;

  @Test
  public void test() throws InterruptedException {
    assertEquals("DVA", config.name());
    assertEquals(8080, config.port());
    assertTrue(config.debug());
    assertArrayEquals(new String[]{"root", "admin"}, config.allowedUsers());
    assertEquals("file", config.dataStore().getType());
    assertEquals("/tmp/data", config.dataStore().getUrl());

    handler.setDebug(false);
    Thread.sleep(TestingConfiguration.INTERVAL_MILLIS * 2);
    assertFalse(config.debug());
  }

  static class FileHandler {

    private final File file;
    private final Map<String, String> map = new HashMap<>();

    FileHandler(File file) {
      this.file = file;

      map.put("dva.app.name", "DVA");
      map.put("dva.app.port", "8080");
      map.put("dva.app.debug", "true");
      map.put("dva.app.allowedUsers", "root,admin");
      map.put("dva.app.dataStore", "file,/tmp/data");

      write();
    }

    synchronized void setDebug(boolean debug) {
      map.put("dva.app.debug", Boolean.toString(debug));
      write();
    }

    synchronized void write() {
      try (FileWriter w = new FileWriter(file)) {
        for (Entry<String, String> entry : map.entrySet()) {
          w.write(entry.getKey() + "=" + entry.getValue() + "\n");
        }
      } catch (IOException e) {
        throw new RuntimeException("Writing error", e);
      }
    }
  }
}
