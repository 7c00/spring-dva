package io.github.aload0.spring.dva;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.StringUtils;

public class DvaProxy<T> implements FactoryBean {

  private final Class<T> proxyInterface;
  private final Context context;
  private volatile Object proxyObject;

  public DvaProxy(Class<T> proxyInterface, Context context) {
    this.proxyInterface = proxyInterface;
    this.context = context;
  }

  @Override
  public synchronized Object getObject() throws Exception {
    if (proxyObject == null) {
      final Map<String, MethodAccessor> accessors = createMethodAccessors();
      proxyObject = Proxy.newProxyInstance(context.getEnvironment().getClass().getClassLoader(),
          new Class[]{proxyInterface}, (proxy, method, args) -> {
            if (Object.class.equals(method.getDeclaringClass())) {
              return method.invoke(this, args);
            }
            String name = method.getName();
            if (accessors.containsKey(name)) {
              return accessors.get(name).get();
            }
            throw new RuntimeException("Cannot handle method " + name);
          });
    }
    return proxyObject;
  }

  @Override
  public Class<T> getObjectType() {
    return proxyInterface;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

  private ObjectReader getObjectReader() {
    String name = context.getObjectReaderName();
    if (StringUtils.isEmpty(name)) {
      return null;
    }
    return context.getBeanFactory().getBean(name, ObjectReader.class);
  }

  private PropertyReader getPropertyReader() {
    String name = context.getPropertyReaderName();
    if (StringUtils.isEmpty(name)) {
      return new DefaultPropertyReader(context.getEnvironment());
    }
    return context.getBeanFactory().getBean(name, PropertyReader.class);
  }

  private Map<String, MethodAccessor> createMethodAccessors() {
    final ObjectReader objectReader = getObjectReader();
    final PropertyReader propertyReader = getPropertyReader();
    String header = context.getHeader();
    String scope = context.getNameConvention().classScope(proxyInterface);
    final String prefix = header + "." + scope + ".";

    MethodAccessorFactory.Context ctx = new MethodAccessorFactory.Context() {
      @Override
      public Class<?> clazz() {
        return proxyInterface;
      }

      @Override
      public String prefix() {
        return prefix;
      }

      @Override
      public PropertyReader propertyReader() {
        return propertyReader;
      }

      @Override
      public NameConvention nameConvention() {
        return context.getNameConvention();
      }

      @Override
      public ObjectReader objectReader() {
        return objectReader;
      }
    };
    MethodAccessorFactory factory = context.getMethodAccessorFactory();

    Map<String, MethodAccessor> result = new HashMap<>();
    for (Method method : proxyInterface.getMethods()) {
      String name = method.getName();
      if (result.containsKey(name)) {
        throw new IllegalArgumentException("Method overwrite not allowed");
      }
      result.put(name, factory.getMethodAccessor(method, ctx));
    }
    return result;
  }
}
