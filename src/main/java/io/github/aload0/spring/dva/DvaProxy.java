package io.github.aload0.spring.dva;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.env.PropertyResolver;

public class DvaProxy<T> implements FactoryBean {

  private final Class<T> proxyInterface;
  private final Context context;
  private final Map<String, MethodAccessor> accessors;
  private volatile Object proxyObject;

  public DvaProxy(Class<T> proxyInterface, Context context) {
    this.proxyInterface = proxyInterface;
    this.context = context;
    this.accessors = createMethodAccessors();
  }

  @Override
  public synchronized Object getObject() throws Exception {
    if (proxyObject == null) {
      proxyObject = Proxy.newProxyInstance(context.getEnvironment().getClass().getClassLoader(),
          new Class[]{proxyInterface}, (proxy, method, args) -> {
            String name = method.getName();
            if (accessors.containsKey(name)) {
              return accessors.get(name).get();
            }
            throw new RuntimeException("Cannot handle method " + name);
          });
      ;
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

  private Map<String, MethodAccessor> createMethodAccessors() {
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
      public PropertyResolver propertyResolver() {
        return context.getPropertyResolver();
      }

      @Override
      public NameConvention nameConvention() {
        return context.getNameConvention();
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
