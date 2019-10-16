package io.github.aload0.spring.dva;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class BasicFilePropertyReader implements PropertyReader {

  private static final Logger LOGGER = LoggerFactory.getLogger(BasicFilePropertyReader.class);

  private final AtomicReference<Map<String, String>> properties = new AtomicReference<>();
  private final AtomicLong lastTimeMillis = new AtomicLong(System.currentTimeMillis());
  private final long intervalMillis;
  private final File file;

  /**
   * Create a new {@code BasicFilePropertyReader} that reads properties from file.
   *
   * BasicFilePropertyReader reads file when creating or reading after intervalMillis.
   */
  public BasicFilePropertyReader(File file, long intervalMillis) throws IOException {
    requireNonNull(file);
    this.file = file;
    this.intervalMillis = intervalMillis;

    properties.set(load(file));
  }

  @Override
  public String getProperty(String name, String defaultValue) {
    long current = System.currentTimeMillis();
    long last = lastTimeMillis.get();
    if (current - last > intervalMillis && lastTimeMillis.compareAndSet(last, current)) {
      try {
        LOGGER.info("Loading {}", file);
        properties.set(load(file));
      } catch (IOException e) {
        LOGGER.warn("Loading {} error", file, e);
      }
    }
    return properties.get().getOrDefault(name, defaultValue);
  }

  private Map<String, String> load(File file) throws IOException {
    Properties prop =
        PropertiesLoaderUtils.loadProperties(new FileSystemResource(file));
    Map<String, String> map = new HashMap<>(prop.size());
    for (final String name : prop.stringPropertyNames()) {
      map.put(name, prop.getProperty(name));
    }
    return map;
  }
}
