package io.github.aload0.spring.dva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDva(basePackages = "io.github.aload0.spring.dva", header = "dva")
public class DvaApp {

  public static void main(String[] args) {
    SpringApplication.run(DvaApp.class, args);
  }
}
