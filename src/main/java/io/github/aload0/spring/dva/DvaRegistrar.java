package io.github.aload0.spring.dva;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

public class DvaRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware,
    BeanFactoryAware {

  private Environment environment;
  private BeanFactory beanFactory;

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  @Override
  public void registerBeanDefinitions(AnnotationMetadata meta,
      BeanDefinitionRegistry registry) {
    // Handle EnableDva annotation
    AnnotationAttributes attrs = AnnotationAttributes
        .fromMap(meta.getAnnotationAttributes(EnableDva.class.getName()));

    String[] basePackages = attrs.getStringArray("basePackages");
    String header = attrs.getString("header");
    String objectReaderName = attrs.getString("objectReaderName");
    String propertyReaderName = attrs.getString("propertyReaderName");
    NameConvention nameConvention = getImplement(attrs, "nameConvention", NameConvention.class);
    MethodAccessorFactory methodAccessorFactory = getImplement(attrs, "methodAccessorFactory",
        MethodAccessorFactory.class);

    Context context = new DvaContext(header, objectReaderName, environment, beanFactory,
        propertyReaderName, nameConvention, methodAccessorFactory);
    Scanner scanner = new Scanner(registry, context);
    scanner.doScan(basePackages);
  }

  private static <T> T getImplement(AnnotationAttributes attrs, String name, Class<T> type) {
    Class<?> cls = attrs.getClass(name);
    if (!type.isAssignableFrom(cls)) {
      throw new IllegalArgumentException(cls + " not implement " + type);
    }
    @SuppressWarnings("unchecked") Class<T> t = (Class<T>) cls;
    try {
      return t.getConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException("Can't instantiate " + cls, e);
    }
  }

  private class Scanner extends ClassPathBeanDefinitionScanner {

    private final Context context;

    private Scanner(BeanDefinitionRegistry registry, Context context) {
      super(registry);
      this.context = context;
      addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
      logger.debug("Scanning " + Arrays.toString(basePackages) + " ...");

      Set<BeanDefinitionHolder> holders = super.doScan(basePackages);
      logger.info("Scanned " + holders.size() + " bean definitions");

      for (BeanDefinitionHolder holder : holders) {
        GenericBeanDefinition definition = (GenericBeanDefinition) holder
            .getBeanDefinition();
        ConstructorArgumentValues args = definition
            .getConstructorArgumentValues();
        args.addGenericArgumentValue(definition.getBeanClassName());
        args.addIndexedArgumentValue(1, context);
        definition.setBeanClass(DvaProxy.class);
      }
      return holders;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
      return beanDefinition.getMetadata().isInterface()
          && beanDefinition.getMetadata().isIndependent()
          && context.getNameConvention().accept(beanDefinition.getMetadata().getClassName());
    }
  }
}
