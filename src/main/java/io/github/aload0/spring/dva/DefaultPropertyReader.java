package io.github.aload0.spring.dva;

import org.springframework.core.env.Environment;

public class DefaultPropertyReader implements PropertyReader {

  private final Environment environment;

  DefaultPropertyReader(Environment environment) {
    this.environment = environment;
  }

  @Override
  public String getProperty(String name, String defaultValue) {
    return environment.getProperty(name, defaultValue);
  }
}
