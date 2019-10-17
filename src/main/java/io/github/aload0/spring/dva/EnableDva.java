package io.github.aload0.spring.dva;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.core.env.PropertyResolver;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(DvaRegistrar.class)
public @interface EnableDva {

  /**
   * Base packages to scan.
   */
  String[] basePackages() default {};

  /**
   * Prefix of properties.
   */
  String header() default "";

  /**
   * Name to specify a custom ObjectReader Bean.
   */
  String objectReaderName() default "";

  /**
   * Name to specify a custom PropertyReader Bean. If blank, {@code DefaultPropertyReader} is used.
   */
  String propertyReaderName() default "";

  /**
   * Class to specify a {@code NameConvention} implementation. Default is {@code
   * DvaNameConvention}.
   */
  Class<? extends NameConvention> nameConvention() default DvaNameConvention.class;

  /**
   * Class to specify a {@code MethodAccessorFactory} implementation. Default is {@code
   * DvaMethodAccessorFactory}.
   */
  Class<? extends MethodAccessorFactory> methodAccessorFactory() default DvaMethodAccessorFactory.class;
}
