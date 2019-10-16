package io.github.aload0.spring.dva;

import java.io.File;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestingConfiguration {

  static final String LOCAL_FILE = "target/local.properties";
  static final long INTERVAL_MILLIS = 20;

  @Bean("localFile")
  public PropertyReader appFilePropertyReader() {
    try {
      return new BasicFilePropertyReader(new File(LOCAL_FILE), INTERVAL_MILLIS);
    } catch (IOException e) {
      throw new RuntimeException("Creating PropertyReader error", e);
    }
  }
}
