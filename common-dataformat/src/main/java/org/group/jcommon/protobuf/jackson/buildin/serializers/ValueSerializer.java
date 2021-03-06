package org.group.jcommon.protobuf.jackson.buildin.serializers;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Value;

import org.group.jcommon.protobuf.jackson.ProtobufSerializer;

public class ValueSerializer extends ProtobufSerializer<Value> {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ValueSerializer() {
    super(Value.class);
  }

  @Override
  public void serialize(Value value, JsonGenerator generator, SerializerProvider serializerProvider)
      throws IOException {
    Map<FieldDescriptor, Object> fields = value.getAllFields();
    if (fields.isEmpty()) {
      generator.writeNull();
    } else {
      // should only have 1 entry
      for (Entry<FieldDescriptor, Object> entry : fields.entrySet()) {
        writeValue(entry.getKey(), entry.getValue(), generator, serializerProvider);
      }
    }
  }
}
