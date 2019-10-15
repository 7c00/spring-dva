package io.github.aload0.spring.dva;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;

public interface Context {

  String getHeader();

  String getObjectReaderName();

  Environment getEnvironment();

  BeanFactory getBeanFactory();

  PropertyResolver getPropertyResolver();

  NameConvention getNameConvention();

  MethodAccessorFactory getMethodAccessorFactory();
}
