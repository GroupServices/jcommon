package org.group.jcommon.protobuf.jackson.buildin.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.ListValue;
import com.google.protobuf.Value;

import org.group.jcommon.protobuf.jackson.ProtobufSerializer;

public class ListValueSerializer extends ProtobufSerializer<ListValue> {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static final FieldDescriptor VALUES_FIELD = ListValue.getDescriptor().findFieldByName("values");

  public ListValueSerializer() {
    super(ListValue.class);
  }

  @Override
  public void serialize(ListValue listValue, JsonGenerator generator, SerializerProvider serializerProvider)
      throws IOException {
    generator.writeStartArray();
    for (Value value : listValue.getValuesList()) {
      writeValue(VALUES_FIELD, value, generator, serializerProvider);
    }
    generator.writeEndArray();
  }
}
