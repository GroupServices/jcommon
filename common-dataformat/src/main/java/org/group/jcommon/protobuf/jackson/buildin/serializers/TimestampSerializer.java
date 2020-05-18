package org.group.jcommon.protobuf.jackson.buildin.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;

import org.group.jcommon.protobuf.jackson.ProtobufSerializer;

public class TimestampSerializer extends ProtobufSerializer<Timestamp> {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public TimestampSerializer() {
    super(Timestamp.class);
  }

  @Override
  public void serialize(Timestamp timestamp, JsonGenerator generator, SerializerProvider serializerProvider)
      throws IOException {
    generator.writeString(Timestamps.toString(timestamp));
  }
}
