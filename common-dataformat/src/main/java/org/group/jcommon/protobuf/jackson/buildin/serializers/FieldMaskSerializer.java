package org.group.jcommon.protobuf.jackson.buildin.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.FieldMask;
import com.google.protobuf.util.FieldMaskUtil;

import org.group.jcommon.protobuf.jackson.ProtobufSerializer;

public class FieldMaskSerializer extends ProtobufSerializer<FieldMask> {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public FieldMaskSerializer() {
    super(FieldMask.class);
  }

  @Override
  public void serialize(FieldMask fieldMask, JsonGenerator generator, SerializerProvider serializerProvider)
      throws IOException {
    generator.writeString(FieldMaskUtil.toJsonString(fieldMask));
  }
}
