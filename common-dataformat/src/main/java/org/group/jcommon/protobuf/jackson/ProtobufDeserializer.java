package org.group.jcommon.protobuf.jackson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.NullValue;

import org.group.jcommon.protobuf.jackson.buildin.deserializers.MessageDeserializer;

public abstract class ProtobufDeserializer<T extends Message, V extends Message.Builder> extends StdDeserializer<V> {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static final String NULL_VALUE_FULL_NAME = NullValue.getDescriptor().getFullName();
  private static final EnumValueDescriptor NULL_VALUE_DESCRIPTOR = NullValue.NULL_VALUE.getValueDescriptor();

  private final T defaultInstance;
  private final Map<FieldDescriptor, JsonDeserializer<Object>> deserializerCache;

  @SuppressWarnings("unchecked")
  public ProtobufDeserializer(final Class<T> messageType) {
    super(messageType);

    try {
      this.defaultInstance = (T) messageType.getMethod("getDefaultInstance").invoke(null);
    } catch (final Exception e) {
      throw new RuntimeException("Unable to get default instance for type " + messageType, e);
    }

    this.deserializerCache = new ConcurrentHashMap<>();
  }

  protected abstract void populate(V builder, JsonParser parser, DeserializationContext context) throws IOException;

  public JsonDeserializer<T> buildAtEnd() {

    @SuppressWarnings("unchecked")
    final Class<T> messageType = (Class<T>) handledType();
    return new BuildingDeserializer<T, V>(messageType) {

      /**
       *
       */
      private static final long serialVersionUID = 1L;

      @Override
      public JsonDeserializer<V> getWrappedDeserializer() {
        return ProtobufDeserializer.this;
      }
    };
  }

  @Override
  @SuppressWarnings("unchecked")
  public V deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
    final V builder = (V) defaultInstance.newBuilderForType();

    populate(builder, parser, context);

    return builder;
  }

  private void checkNullReturn(final FieldDescriptor field, final DeserializationContext context)
      throws JsonProcessingException {
    if (context.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
      throw reportInputMismatch(context, "Can not map JSON null into primitive field " + field.getFullName()
          + " (set DeserializationConfig.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES to 'false' to allow)");
    }
  }

  protected List<Message> readMap(final Message.Builder builder, final FieldDescriptor field, final JsonParser parser,
      final DeserializationContext context) throws IOException {
    if (parser.getCurrentToken() == JsonToken.VALUE_NULL) {
      // Seems like we should treat null as an empty map rather than fail?
      return Collections.emptyList();
    } else if (parser.getCurrentToken() != JsonToken.START_OBJECT) {
      throw reportWrongToken(JsonToken.START_OBJECT, context,
          "Can't parse map field out of " + parser.currentToken() + " token");
    }

    final Descriptor entryDescriptor = field.getMessageType();
    final FieldDescriptor keyDescriptor = entryDescriptor.findFieldByName("key");
    final FieldDescriptor valueDescriptor = entryDescriptor.findFieldByName("value");

    final List<Message> entries = new ArrayList<>();
    while (parser.nextToken() != JsonToken.END_OBJECT) {
      final Message.Builder entryBuilder = builder.newBuilderForField(field);
      final Object key = readKey(keyDescriptor, parser, context);
      parser.nextToken(); // move from key to value
      final Object value = readValue(entryBuilder, valueDescriptor, null, parser, context);

      entryBuilder.setField(keyDescriptor, key);
      entryBuilder.setField(valueDescriptor, value);
      entries.add(entryBuilder.build());
    }
    return entries;
  }

  /**
   * Specialized version of readValue just for reading map keys, because the
   * StdDeserializer methods like _parseIntPrimitive blow up when the current
   * JsonToken is FIELD_NAME
   */
  private Object readKey(final FieldDescriptor field, final JsonParser parser, final DeserializationContext context)
      throws IOException {
    if (parser.getCurrentToken() != JsonToken.FIELD_NAME) {
      throw reportWrongToken(JsonToken.FIELD_NAME, context, "Expected FIELD_NAME token");
    }

    final String fieldName = parser.getCurrentName();
    switch (field.getJavaType()) {
      case INT:
        // lifted from StdDeserializer since there's no method to call
        try {
          return NumberInput.parseInt(fieldName.trim());
        } catch (final IllegalArgumentException iae) {
          final Number number = (Number) context.handleWeirdStringValue(_valueClass, fieldName.trim(),
              "not a valid int value");
          return number == null ? 0 : number.intValue();
        }
      case LONG:
        // lifted from StdDeserializer since there's no method to call
        try {
          return NumberInput.parseLong(fieldName.trim());
        } catch (final IllegalArgumentException iae) {
          final Number number = (Number) context.handleWeirdStringValue(_valueClass, fieldName.trim(),
              "not a valid long value");
          return number == null ? 0L : number.longValue();
        }
      case BOOLEAN:
        // lifted from StdDeserializer since there's no method to call
        final String text = fieldName.trim();
        if ("true".equals(text) || "True".equals(text)) {
          return true;
        }
        if ("false".equals(text) || "False".equals(text)) {
          return false;
        }
        final Boolean b = (Boolean) context.handleWeirdStringValue(_valueClass, text,
            "only \"true\" or \"false\" recognized");
        return Boolean.TRUE.equals(b);
      case STRING:
        return fieldName;
      case ENUM:
        final EnumValueDescriptor enumValueDescriptor = field.getEnumType().findValueByName(parser.getText());

        if (enumValueDescriptor == null && !ignorableEnum(parser.getText().trim(), context)) {
          throw context.weirdStringException(parser.getText(), field.getEnumType().getClass(),
              "value not one of declared Enum instance names");
        }

        return enumValueDescriptor;
      default:
        throw new IllegalArgumentException("Unexpected map key type: " + field.getJavaType());
    }
  }

  protected Object readValue(final Message.Builder builder, final FieldDescriptor field, final Message defaultInstance,
      final JsonParser parser, final DeserializationContext context) throws IOException {
    if (parser.getCurrentToken() == JsonToken.START_ARRAY) {
      if (field.getJavaType() == FieldDescriptor.JavaType.MESSAGE) {
        // might have a custom serializer/deserializer registered
        final JsonDeserializer<Object> deserializer = getMessageDeserializer(builder, field, defaultInstance, context);
        if (!isDefaultMessageDeserializer(deserializer)) {
          return deserializer.deserialize(parser, context);
        }
      }

      throw reportInputMismatch(context, "Encountered START_ARRAY token for non-repeated field " + field.getFullName());
    }

    if (parser.getCurrentToken() == JsonToken.VALUE_NULL) {
      switch (field.getJavaType()) {
        case INT:
        case LONG:
        case FLOAT:
        case DOUBLE:
        case BOOLEAN:
          checkNullReturn(field, context);
          return null;
        case ENUM:
          if (NULL_VALUE_FULL_NAME.equals(field.getEnumType().getFullName())) {
            return NULL_VALUE_DESCRIPTOR;
          } else {
            return null;
          }
        case MESSAGE:
          // don't return null yet, might have a custom serializer/deserializer registered
          final JsonDeserializer<Object> deserializer = getMessageDeserializer(builder, field, defaultInstance,
              context);
          return deserializer.getNullValue(context);
        default:
          return null;
      }
    }

    switch (field.getJavaType()) {
      case INT:
        return _parseIntPrimitive(parser, context);
      case LONG:
        return _parseLongPrimitive(parser, context);
      case FLOAT:
        return _parseFloatPrimitive(parser, context);
      case DOUBLE:
        return _parseDoublePrimitive(parser, context);
      case BOOLEAN:
        return _parseBooleanPrimitive(parser, context);
      case STRING:
        switch (parser.getCurrentToken()) {
          case VALUE_STRING:
            return parser.getText();
          default:
            return _parseString(parser, context);
        }
      case BYTE_STRING:
        switch (parser.getCurrentToken()) {
          case VALUE_STRING:
            return ByteString.copyFrom(context.getBase64Variant().decode(parser.getText()));
          default:
            throw reportWrongToken(field, JsonToken.VALUE_STRING, context);
        }
      case ENUM:
        final EnumValueDescriptor enumValueDescriptor;
        switch (parser.getCurrentToken()) {
          case VALUE_STRING:
            enumValueDescriptor = field.getEnumType().findValueByName(parser.getText());

            if (enumValueDescriptor == null && !ignorableEnum(parser.getText().trim(), context)) {
              throw context.weirdStringException(parser.getText(), field.getEnumType().getClass(),
                  "value not one of declared Enum instance names");
            }

            return enumValueDescriptor;
          case VALUE_NUMBER_INT:
            if (allowNumbersForEnums(context)) {
              enumValueDescriptor = field.getEnumType().findValueByNumber(parser.getIntValue());

              if (enumValueDescriptor == null && !ignoreUnknownEnums(context)) {
                throw context.weirdNumberException(parser.getIntValue(), field.getEnumType().getClass(),
                    "index value outside legal index range " + indexRange(field.getEnumType()));
              }

              return enumValueDescriptor;
            } else {
              throw reportWrongToken(JsonToken.VALUE_STRING, context, "Not allowed to deserialize Enum "
                  + "value out of JSON number (disable DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS to allow)");
            }
          default:
            throw reportWrongToken(field, JsonToken.VALUE_STRING, context);
        }
      case MESSAGE:
        final JsonDeserializer<Object> deserializer = getMessageDeserializer(builder, field, defaultInstance, context);
        return deserializer.deserialize(parser, context);
      default:
        throw new IllegalArgumentException("Unrecognized field type: " + field.getJavaType());
    }
  }

  protected List<Object> readArray(final Message.Builder builder, final FieldDescriptor field,
      final Message defaultInstance, final JsonParser parser, final DeserializationContext context) throws IOException {
    switch (parser.getCurrentToken()) {
      case START_ARRAY:
        final List<Object> values = Lists.newArrayList();
        while (parser.nextToken() != JsonToken.END_ARRAY) {
          final Object value = readValue(builder, field, defaultInstance, parser, context);

          if (value != null) {
            values.add(value);
          }
        }
        return values;
      case VALUE_NULL:
        // Seems like we should treat null as an empty list rather than fail?
        return Collections.emptyList();
      default:
        if (context.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
          final Object value = readValue(builder, field, defaultInstance, parser, context);
          return Collections.singletonList(value);
        } else {
          throw reportInputMismatch(context, "Expected JSON array for repeated field " + field.getFullName());
        }
    }
  }

  private JsonDeserializer<Object> getMessageDeserializer(final Message.Builder builder, final FieldDescriptor field,
      final Message defaultInstance, final DeserializationContext context) throws IOException {
    JsonDeserializer<Object> deserializer = deserializerCache.get(field);
    if (deserializer == null) {
      final Class<?> subType;
      if (defaultInstance == null) {
        final Message.Builder subBuilder = builder.newBuilderForField(field);
        subType = subBuilder.getDefaultInstanceForType().getClass();
      } else {
        subType = defaultInstance.getClass();
      }

      final JavaType type = context.constructType(subType);
      deserializer = context.findContextualValueDeserializer(type, null);
      deserializerCache.put(field, deserializer);
    }

    return deserializer;
  }

  private AssertionError reportInputMismatch(final DeserializationContext context, final String message)
      throws JsonMappingException {
    context.reportInputMismatch(this, message);
    // the previous method should have thrown
    throw new AssertionError();
  }

  private AssertionError reportWrongToken(final FieldDescriptor field, final JsonToken expected,
      final DeserializationContext context) throws JsonMappingException {
    return reportWrongToken(expected, context, wrongTokenMessage(field, context));
  }

  private AssertionError reportWrongToken(final JsonToken expected, final DeserializationContext context,
      final String message) throws JsonMappingException {
    context.reportWrongTokenException(this, expected, message);
    // the previous method should have thrown
    throw new AssertionError();
  }

  private static boolean isDefaultMessageDeserializer(final JsonDeserializer<?> deserializer) {
    final Class<?> deserializerType;
    if (deserializer instanceof BuildingDeserializer) {
      deserializerType = ((BuildingDeserializer) deserializer).getWrappedDeserializer().getClass();
    } else {
      deserializerType = deserializer.getClass();
    }
    return MessageDeserializer.class.equals(deserializerType);
  }

  private static boolean ignorableEnum(final String value, final DeserializationContext context) {
    return (acceptEmptyStringAsNull(context) && value.length() == 0) || ignoreUnknownEnums(context);
  }

  private static boolean acceptEmptyStringAsNull(final DeserializationContext context) {
    return context.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
  }

  private static boolean allowNumbersForEnums(final DeserializationContext context) {
    return !context.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
  }

  private static boolean ignoreUnknownEnums(final DeserializationContext context) {
    return context.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
  }

  private static String indexRange(final EnumDescriptor field) {
    List<Integer> indices = Lists.transform(field.getValues(), new Function<EnumValueDescriptor, Integer>() {
      @Override
      public Integer apply(@Nonnull final EnumValueDescriptor value) {
        return value.getIndex();
      }
    });

    // Guava returns non-modifiable list
    indices = Lists.newArrayList(indices);

    Collections.sort(indices);

    return "[" + Joiner.on(',').join(indices) + "]";
  }

  private static String wrongTokenMessage(final FieldDescriptor field, final DeserializationContext context) {
    return "Can not deserialize instance of " + field.getJavaType() + " out of " + context.getParser().currentToken()
        + " token";
  }
}
