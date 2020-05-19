package org.group.jcommon.jackson.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Maps;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;

import org.group.jcommon.jackson.DataFormatException;
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

    /**
     * Deserialize <code>json</code> to an object of type <code>clazz</code>.
     *
     * @throws DataFormatException
     */
    public static <T> T mustReadValue(@Nullable String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            return DEFAULT_INSTANCE.readValue(json, clazz);
        } catch (IOException e) {
            throw new DataFormatException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Message> T mustReadProto(@Nullable String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            Method method = PROTOBUF_DEFAULT_INSTANCE_METHOD_CACHE.get(clazz);
            if (method == null) {
                method = clazz.getMethod("getDefaultInstance");
                PROTOBUF_DEFAULT_INSTANCE_METHOD_CACHE.put(clazz, method);
            }
            Message.Builder b = ((T) method.invoke(null)).toBuilder();
            JsonFormat.parser().ignoringUnknownFields().merge(json, b);
            return (T) (b.build());
        } catch (IOException e) {
            throw new DataFormatException(e);
        } catch (SecurityException | ReflectiveOperationException e) {
            throw new DataFormatException(e);
        }
    }

    /**
     * Deserialize <code>json</code> to an object, using <code>typeRef</code> to
     * determine class of the object.
     *
     * @throws DataFormatException
     */
    public static <T> T mustReadValue(@Nullable String json, TypeReference<T> typeRef) {
        if (json == null) {
            return null;
        }
        try {
            return DEFAULT_INSTANCE.readValue(json, typeRef);
        } catch (IOException e) {
            throw new DataFormatException(e);
        }
    }

    /**
     * Serialize <code>o</code> to a string.
     *
     * @throws DataFormatException
     */
    public static String mustWriteValue(@Nullable Object o) {
        if (o == null) {
            return null;
        }
        try {
            if (o instanceof MessageOrBuilder) {
                return JsonFormat.printer().omittingInsignificantWhitespace().print((MessageOrBuilder) o);
            } else {
                return DEFAULT_INSTANCE.writeValueAsString(o);
            }
        } catch (IOException e) {
            throw new DataFormatException(e);
        }
    }

    public static String mustWriteValuePretty(@Nullable Object o) {
        if (o == null) {
            return null;
        }
        try {
            if (o instanceof MessageOrBuilder) {
                return JsonFormat.printer().print((MessageOrBuilder) o);
            } else {
                return DEFAULT_INSTANCE.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            }
        } catch (IOException e) {
            throw new DataFormatException(e);
        }
    }

    public static JsonNode mustReadTree(@Nullable String json) {
        if (json == null) {
            return null;
        }
        try {
            return DEFAULT_INSTANCE.readTree(json);
        } catch (IOException e) {
            throw new DataFormatException(e);
        }
    }

    /**
     * Serialize <code>o</code> to a string using compact JSON serializer (i.e.,
     * NON_DEFAULT only) if possible.
     *
     * @throws DataFormatException
     */
    public static String writeObjectCompact(@Nullable Object o) {
        try {
            if (o instanceof MessageOrBuilder) {
                return JsonFormat.printer().omittingInsignificantWhitespace().print((MessageOrBuilder) o);
            } else {
                try {
                    // Not all objects can be serialized using COMPACT_INSTANCE.
                    // E.g., objects without default constructor. So we fall back
                    // to standard serializer.
                    return COMPACT_INSTANCE.writeValueAsString(o);
                } catch (JsonMappingException e) {
                    return DEFAULT_INSTANCE.writeValueAsString(o);
                }
            }
        } catch (IOException e) {
            throw new DataFormatException(e);
        }
    }
}