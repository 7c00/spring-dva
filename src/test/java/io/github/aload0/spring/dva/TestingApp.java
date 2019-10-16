package io.github.aload0.spring.dva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDva(basePackages = "io.github.aload0.spring.dva", header = "dva",
    objectReaderName = "testingObjectReader", propertyReaderName = "localFile")
public class TestingApp {

  public static void main(String[] args) {
    SpringApplication.run(TestingApp.class, args);
  }
}
