package io.github.aload0.spring.dva;

public class DataStore {

  private final String type;
  private final String url;

  static DataStore valueOf(String s) {
    if (s == null || s.isEmpty()) {
      throw new IllegalArgumentException("Null or empty string");
    }
    String[] parts = s.split(",", 2);
    return new DataStore(parts[0], parts.length == 2 ? parts[1] : "");
  }

  private DataStore(String type, String url) {
    this.type = type;
    this.url = url;
  }

  String getType() {
    return type;
  }

  String getUrl() {
    return url;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("DataStore{");
    sb.append("type='").append(type).append('\'');
    sb.append(", url='").append(url).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
