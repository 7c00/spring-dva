package io.github.aload0.spring.dva;

import java.lang.reflect.Method;
import java.util.function.Function;
import org.springframework.core.env.PropertyResolver;
import org.springframework.util.StringUtils;

public class DvaMethodAccessorFactory implements MethodAccessorFactory {

  @Override
  public MethodAccessor getMethodAccessor(Method method, Context context)
      throws IllegalArgumentException {
    String key = context.prefix() + context.nameConvention().method(method);
    Class<?> type = method.getReturnType();
    PropertyResolver resolver = context.propertyResolver();

    if (type.equals(String.class)) {
      return new CachedMethodAccessor(key, null, resolver, s -> s);
    } else if (type.isArray()) {
      Class<?> componentType = type.getComponentType();
      if (componentType.isAssignableFrom(String.class)) {
        return new CachedMethodAccessor(key, "", resolver,
            StringUtils::commaDelimitedListToStringArray);
      }
    } else if (type.equals(int.class) || type.equals(Integer.class)) {
      return new CachedMethodAccessor(key, null, resolver, Integer::parseInt);
    } else if (type.equals(long.class) || type.equals(Long.class)) {
      return new CachedMethodAccessor(key, null, resolver, Long::parseLong);
    } else if (type.equals(float.class) || type.equals(Float.class)) {
      return new CachedMethodAccessor(key, null, resolver, Float::parseFloat);
    } else if (type.equals(double.class) || type.equals(Double.class)) {
      return new CachedMethodAccessor(key, null, resolver, Double::parseDouble);
    } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
      return new CachedMethodAccessor(key, null, resolver, "true"::equalsIgnoreCase);
    }

    throw new IllegalArgumentException(type + " is not supported");
  }

  static class CachedMethodAccessor implements MethodAccessor {

    private final String key;
    private final String defaultValue;
    private final PropertyResolver propertyResolver;
    private final Function<String, Object> parser;
    private final ThreadLocal<String> value = new ThreadLocal<>();
    private final ThreadLocal<Object> parsed = new ThreadLocal<>();

    CachedMethodAccessor(String key, String defaultValue, PropertyResolver propertyResolver,
        Function<String, Object> parser) {
      this.key = key;
      this.defaultValue = defaultValue;
      this.propertyResolver = propertyResolver;
      this.parser = parser;
    }

    @Override
    public Object get() {
      String latest = propertyResolver.getProperty(key, defaultValue);
      if (value.get() == null || !value.get().equals(latest)) {
        value.set(latest);
        parsed.set(latest == null ? null : parser.apply(latest));
      }
      return parsed.get();
    }
  }

}
