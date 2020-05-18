package org.group.jcommon.protobuf.jackson.buildin.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.protobuf.NullValue;

public class NullValueSerializer extends StdSerializer<NullValue> {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public NullValueSerializer() {
    super(NullValue.class);
  }

  @Override
  public void serialize(NullValue value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    gen.writeNull();
  }
}
