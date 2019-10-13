package io.github.aload0.spring.dva;

import java.lang.reflect.Method;

public class DvaNameConvention implements NameConvention {

  private static final String SUFFIX = "Config";

  @Override
  public boolean accept(String className) {
    return className.endsWith(SUFFIX);
  }

  @Override
  public String classScope(Class<?> cls) throws IllegalArgumentException {
    String name = cls.getSimpleName();
    int e = name.lastIndexOf(SUFFIX);
    return name.substring(0, 1).toLowerCase() + name.substring(1, e);
  }

  @Override
  public String method(Method method) throws IllegalArgumentException {
    return method.getName();
  }
}
