package io.github.aload0.spring.dva;

import java.lang.reflect.Method;

public interface MethodAccessorFactory {

  MethodAccessor getMethodAccessor(Method method, Context context) throws IllegalArgumentException;

  interface Context {

    Class<?> clazz();

    String prefix();

    PropertyReader propertyReader();

    NameConvention nameConvention();

    ObjectReader objectReader();
  }
}
