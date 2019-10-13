package io.github.aload0.spring.dva;

import java.lang.reflect.Method;
import org.springframework.core.env.PropertyResolver;

public interface MethodAccessorFactory {

  MethodAccessor getMethodAccessor(Method method, Context context) throws IllegalArgumentException;

  interface Context {

    Class<?> clazz();

    String prefix();

    PropertyResolver propertyResolver();

    NameConvention nameConvention();
  }
}
