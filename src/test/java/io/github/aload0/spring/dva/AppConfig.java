package io.github.aload0.spring.dva;

public interface AppConfig {

  String name();

  int port();

  boolean debug();

  String[] allowedUsers();

  DataStore dataStore();
}
