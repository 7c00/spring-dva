package io.github.aload0.spring.dva;

import org.springframework.stereotype.Component;

@Component
public class TestingObjectReader implements ObjectReader {

  @Override
  public Object read(String s, Class<?> cls)
      throws IllegalArgumentException, UnsupportedOperationException {
    if (cls.equals(DataStore.class)) {
      return DataStore.valueOf(s);
    }
    throw new UnsupportedOperationException("Cannot read " + cls);
  }
}
