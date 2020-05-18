package org.group.jcommon.protobuf.jackson.buildin.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.Duration;
import com.google.protobuf.util.Durations;

import org.group.jcommon.protobuf.jackson.ProtobufSerializer;

public class DurationSerializer extends ProtobufSerializer<Duration> {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public DurationSerializer() {
    super(Duration.class);
  }

  @Override
  public void serialize(final Duration duration, final JsonGenerator generator,
      final SerializerProvider serializerProvider) throws IOException {
    generator.writeString(Durations.toString(duration));
  }
}
