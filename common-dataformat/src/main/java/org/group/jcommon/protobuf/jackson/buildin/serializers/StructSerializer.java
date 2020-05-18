package org.group.jcommon.protobuf.jackson.buildin.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.Descriptors.FieldDescriptor;

import org.group.jcommon.protobuf.jackson.ProtobufSerializer;

import com.google.protobuf.Struct;

public class StructSerializer extends ProtobufSerializer<Struct> {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static final FieldDescriptor FIELDS_FIELD = Struct.getDescriptor().findFieldByName("fields");

  public StructSerializer() {
    super(Struct.class);
  }

  @Override
  public void serialize(Struct struct, JsonGenerator generator, SerializerProvider serializerProvider)
      throws IOException {
    writeMap(FIELDS_FIELD, struct.getField(FIELDS_FIELD), generator, serializerProvider);
  }
}
