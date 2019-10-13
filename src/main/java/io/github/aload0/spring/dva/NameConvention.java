package io.github.aload0.spring.dva;

import java.lang.reflect.Method;

public interface NameConvention {

  boolean accept(String className);

  String classScope(Class<?> cls) throws IllegalArgumentException;

  String method(Method method) throws IllegalArgumentException;
}
