package io.github.aload0.spring.dva;

public interface ObjectReader {

  Object read(String s, Class<?> cls)
      throws IllegalArgumentException, UnsupportedOperationException;
}
