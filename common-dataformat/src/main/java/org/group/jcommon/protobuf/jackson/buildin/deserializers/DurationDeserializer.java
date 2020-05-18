package org.group.jcommon.protobuf.jackson.buildin.deserializers;

import java.io.IOException;
import java.text.ParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.protobuf.Duration;
import com.google.protobuf.util.Durations;

public class DurationDeserializer extends StdDeserializer<Duration> {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public DurationDeserializer() {
    super(Duration.class);
  }

  @Override
  public Duration deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
    switch (parser.getCurrentToken()) {
      case VALUE_STRING:
        try {
          return Durations.parse(parser.getText());
        } catch (final ParseException e) {
          throw context.weirdStringException(parser.getText(), Duration.class, e.getMessage());
        }
      default:
        context.reportWrongTokenException(Duration.class, JsonToken.VALUE_STRING, wrongTokenMessage(context));
        // the previous method should have thrown
        throw new AssertionError();
    }
  }

  // TODO share this?
  private static String wrongTokenMessage(final DeserializationContext context) {
    return "Can not deserialize instance of com.google.protobuf.Duration out of " + context.getParser().currentToken()
        + " token";
  }
}
