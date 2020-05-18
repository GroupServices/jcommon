package org.group.jcommon.protobuf.jackson.buildin.deserializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;

import org.group.jcommon.protobuf.jackson.ProtobufDeserializer;

public class WrappedPrimitiveDeserializer<T extends Message, V extends Builder> extends ProtobufDeserializer<T, V> {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public WrappedPrimitiveDeserializer(Class<T> wrapperType) {
    super(wrapperType);
  }

  @Override
  protected void populate(V builder, JsonParser parser, DeserializationContext context) throws IOException {
    FieldDescriptor field = builder.getDescriptorForType().findFieldByName("value");
    Object value = readValue(builder, field, null, parser, context);
    builder.setField(field, value);
  }
}
