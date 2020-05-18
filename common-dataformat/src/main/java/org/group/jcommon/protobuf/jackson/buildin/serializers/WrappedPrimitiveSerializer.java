package org.group.jcommon.protobuf.jackson.buildin.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.MessageOrBuilder;

import org.group.jcommon.protobuf.jackson.ProtobufSerializer;

public class WrappedPrimitiveSerializer<T extends MessageOrBuilder> extends ProtobufSerializer<T> {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public WrappedPrimitiveSerializer(Class<T> wrapperType) {
    super(wrapperType);
  }

  @Override
  public void serialize(MessageOrBuilder message, JsonGenerator generator, SerializerProvider serializerProvider)
      throws IOException {
    FieldDescriptor field = message.getDescriptorForType().findFieldByName("value");
    Object value = message.getField(field);
    writeValue(field, value, generator, serializerProvider);
  }
}
