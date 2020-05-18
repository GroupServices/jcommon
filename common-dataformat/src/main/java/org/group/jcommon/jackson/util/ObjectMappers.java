package org.group.jcommon.jackson.util;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Maps;
import com.google.protobuf.Message;

import org.group.jcommon.protobuf.jackson.ProtobufModule;

public class ObjectMappers {

    private static final ObjectMapper DEFAULT_INSTANCE = new ObjectMapper();

    private static final ObjectMapper SNAKE_CASE_INSTANCE = new ObjectMapper();

    private static final ObjectMapper COMPACT_INSTANCE = new ObjectMapper();

    private static final ObjectMapper YAML_INSTANCE = new ObjectMapper(new YAMLFactory());

    private static final XmlMapper XML_INSTANCE = new XmlMapper();

    static {
        DEFAULT_INSTANCE.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        DEFAULT_INSTANCE.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DEFAULT_INSTANCE.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        DEFAULT_INSTANCE.registerModule(new ProtobufModule());

        SNAKE_CASE_INSTANCE.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SNAKE_CASE_INSTANCE.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        SNAKE_CASE_INSTANCE.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SNAKE_CASE_INSTANCE.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);

        COMPACT_INSTANCE.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        COMPACT_INSTANCE.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        COMPACT_INSTANCE.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        COMPACT_INSTANCE.registerModule(new ProtobufModule());

        XML_INSTANCE.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private static final ConcurrentMap<Class<? extends Message>, Method> PROTOBUF_DEFAULT_INSTANCE_METHOD_CACHE = Maps
            .newConcurrentMap();

    public static ObjectMapper get() {
        return DEFAULT_INSTANCE;
    }

    public static ObjectMapper snakeCase() {
        return SNAKE_CASE_INSTANCE;
    }

    public static ObjectMapper yaml() {
        return YAML_INSTANCE;
    }

    public static ObjectMapper xml() {
        return XML_INSTANCE;
    }

}