package io.github.sagarvns2003.warden.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.Objects;

public class JsonUtil {

  public static final ObjectMapper objectMapper;

  static {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  private JsonUtil() {
  }

  public static String toJsonString(Object obj) {
    if (Objects.isNull(obj)) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException jpe) {
      jpe.printStackTrace();
      return null;
    }
  }

  public static JsonNode toJsonNode(String jsonString) {
    if (Objects.isNull(jsonString)) {
      return null;
    }
    try {
      return objectMapper.readTree(jsonString);
    } catch (JsonProcessingException jpe) {
      jpe.printStackTrace();
      return null;
    }
  }

  public static <T> T fromJson(String jsonString, TypeReference<T> typeRef) {
    if (Objects.isNull(jsonString)) {
      return null;
    }
    try {
      return objectMapper.readValue(jsonString, typeRef);
    } catch (JsonProcessingException jpe) {
      jpe.printStackTrace();
      return null;
    }
  }

  public static <T> T fromJson(final byte[] payload, TypeReference<T> typeRef) {
    try {
      return objectMapper.readValue(payload, typeRef);
    } catch (IOException jpe) {
      jpe.printStackTrace();
      return null;
    }
  }

  public static <T> T fromJson(final byte[] payload, Class<T> clazz) {
    try {
      return objectMapper.readValue(payload, clazz);
    } catch (IOException jpe) {
      jpe.printStackTrace();
      return null;
    }
  }

  public static <T> T fromJson(String jsonString, Class<T> clazz) {
    if (Objects.isNull(jsonString)) {
      return null;
    }
    try {
      return objectMapper.readValue(jsonString, clazz);
    } catch (JsonProcessingException jpe) {
      jpe.printStackTrace();
      return null;
    }
  }

  public static JsonNode getJsonNode(JsonNode data, String param) {
    if (null == data || data.isNull()) {
      return null;
    }
    JsonNode node = data.get(param);
    if (null != node && !node.isNull()) {
      return node;
    }
    return null;
  }

  public static String getTextFromJsonNode(JsonNode data, String param) {
    if (null == data || data.isNull() || !data.has(param)) {
      return null;
    }
    return data.get(param).asText();
  }

  public static boolean getBooleanFromJsonNode(JsonNode data, String param) {
    if (null == data || data.isNull() || !data.has(param)) {
      return false;
    }
    return data.get(param).asBoolean();
  }

  public static Integer getIntegerFromJsonNode(JsonNode data, String param) {
    if (null == data || data.isNull() || !data.has(param)) {
      return null;
    }
    return data.get(param).asInt();
  }

  public static Long getLongFromJsonNode(JsonNode data, String param) {
    if (null == data || data.isNull() || !data.has(param)) {
      return null;
    }
    return data.get(param).asLong();
  }

  public static Double getDoubleFromJsonNode(JsonNode data, String param) {
    if (null == data || data.isNull() || !data.has(param)) {
      return null;
    }
    return data.get(param).asDouble();
  }
}
