package io.github.aload0.spring.dva;

public interface PropertyReader {

  String getProperty(String name, String defaultValue);
}
