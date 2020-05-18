package org.group.jcommon.protobuf.jackson.buildin.deserializers;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.Struct;

import org.group.jcommon.protobuf.jackson.ProtobufDeserializer;

public class StructDeserializer extends ProtobufDeserializer<Struct, Struct.Builder> {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static final FieldDescriptor FIELDS_FIELD = Struct.getDescriptor().findFieldByName("fields");

  public StructDeserializer() {
    super(Struct.class);
  }

  @Override
  protected void populate(Struct.Builder builder, JsonParser parser, DeserializationContext context)
      throws IOException {
    List<Message> entries = readMap(builder, FIELDS_FIELD, parser, context);
    for (Message entry : entries) {
      builder.addRepeatedField(FIELDS_FIELD, entry);
    }
  }
}
