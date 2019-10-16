package io.github.aload0.spring.dva;

import java.lang.reflect.Method;
import java.util.function.Function;
import org.springframework.util.StringUtils;

public class DvaMethodAccessorFactory implements MethodAccessorFactory {

  @Override
  public MethodAccessor getMethodAccessor(Method method, Context context)
      throws IllegalArgumentException {
    String key = context.prefix() + context.nameConvention().method(method);
    Class<?> type = method.getReturnType();
    PropertyReader resolver = context.propertyReader();

    if (type.equals(String.class)) {
      return new CachedMethodAccessor(key, null, resolver, s -> s);
    } else if (type.isArray()) {
      Class<?> componentType = type.getComponentType();
      if (componentType.isAssignableFrom(String.class)) {
        return new CachedMethodAccessor(key, "", resolver,
            StringUtils::commaDelimitedListToStringArray);
      }
    } else if (type.equals(int.class) || type.equals(Integer.class)) {
      return new CachedMethodAccessor(key, "0", resolver, Integer::parseInt);
    } else if (type.equals(long.class) || type.equals(Long.class)) {
      return new CachedMethodAccessor(key, "0", resolver, Long::parseLong);
    } else if (type.equals(float.class) || type.equals(Float.class)) {
      return new CachedMethodAccessor(key, "0", resolver, Float::parseFloat);
    } else if (type.equals(double.class) || type.equals(Double.class)) {
      return new CachedMethodAccessor(key, "0", resolver, Double::parseDouble);
    } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
      return new CachedMethodAccessor(key, "false", resolver, "true"::equalsIgnoreCase);
    }

    ObjectReader reader = context.objectReader();
    if (reader != null && isReadable(reader, type)) {
      return new CachedMethodAccessor(key, null, resolver, s -> reader.read(s, type));
    }

    throw new IllegalArgumentException(type + " is not supported");
  }

  private boolean isReadable(ObjectReader objectReader, Class<?> type) {
    try {
      objectReader.read(null, type);
    } catch (UnsupportedOperationException e) {
      return false;
    } catch (IllegalArgumentException ignored) {
      return true;
    }
    return true;
  }

  static class CachedMethodAccessor implements MethodAccessor {

    private final String key;
    private final String defaultValue;
    private final PropertyReader propertyReader;
    private final Function<String, Object> parser;
    private final ThreadLocal<String> value = new ThreadLocal<>();
    private final ThreadLocal<Object> parsed = new ThreadLocal<>();

    CachedMethodAccessor(String key, String defaultValue, PropertyReader propertyReader,
        Function<String, Object> parser) {
      this.key = key;
      this.defaultValue = defaultValue;
      this.propertyReader = propertyReader;
      this.parser = parser;
    }

    @Override
    public Object get() {
      String latest = propertyReader.getProperty(key, defaultValue);
      if (value.get() == null || !value.get().equals(latest)) {
        value.set(latest);
        parsed.set(latest == null ? null : parser.apply(latest));
      }
      return parsed.get();
    }
  }

}
