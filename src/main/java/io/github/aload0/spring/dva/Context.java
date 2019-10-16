package io.github.aload0.spring.dva;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.env.Environment;

public interface Context {

  String getHeader();

  String getObjectReaderName();

  String getPropertyReaderName();

  Environment getEnvironment();

  BeanFactory getBeanFactory();

  NameConvention getNameConvention();

  MethodAccessorFactory getMethodAccessorFactory();
}
