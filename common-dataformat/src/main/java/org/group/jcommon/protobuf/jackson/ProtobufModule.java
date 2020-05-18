package org.group.jcommon.protobuf.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.google.protobuf.BoolValue;
import com.google.protobuf.BytesValue;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.Duration;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.FieldMask;
import com.google.protobuf.FloatValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.ListValue;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.NullValue;
import com.google.protobuf.StringValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Timestamp;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.UInt64Value;
import com.google.protobuf.Value;

import org.group.jcommon.protobuf.jackson.buildin.deserializers.DurationDeserializer;
import org.group.jcommon.protobuf.jackson.buildin.deserializers.FieldMaskDeserializer;
import org.group.jcommon.protobuf.jackson.buildin.deserializers.ListValueDeserializer;
import org.group.jcommon.protobuf.jackson.buildin.deserializers.NullValueDeserializer;
import org.group.jcommon.protobuf.jackson.buildin.deserializers.StructDeserializer;
import org.group.jcommon.protobuf.jackson.buildin.deserializers.TimestampDeserializer;
import org.group.jcommon.protobuf.jackson.buildin.deserializers.ValueDeserializer;
import org.group.jcommon.protobuf.jackson.buildin.deserializers.WrappedPrimitiveDeserializer;
import org.group.jcommon.protobuf.jackson.buildin.serializers.DurationSerializer;
import org.group.jcommon.protobuf.jackson.buildin.serializers.FieldMaskSerializer;
import org.group.jcommon.protobuf.jackson.buildin.serializers.ListValueSerializer;
import org.group.jcommon.protobuf.jackson.buildin.serializers.MessageSerializer;
import org.group.jcommon.protobuf.jackson.buildin.serializers.NullValueSerializer;
import org.group.jcommon.protobuf.jackson.buildin.serializers.StructSerializer;
import org.group.jcommon.protobuf.jackson.buildin.serializers.TimestampSerializer;
import org.group.jcommon.protobuf.jackson.buildin.serializers.ValueSerializer;
import org.group.jcommon.protobuf.jackson.buildin.serializers.WrappedPrimitiveSerializer;

/**
 * Module to add support for reading and writing Protobufs
 *
 * Register with Jackson via
 * {@link com.fasterxml.jackson.databind.ObjectMapper#registerModule}
 */
public class ProtobufModule extends Module {

    private final ProtobufJacksonConfig config;

    public ProtobufModule() {
        this(ProtobufJacksonConfig.builder().build());
    }

    /**
     * @deprecated use {@link #ProtobufModule(ProtobufJacksonConfig)} instead
     */
    @Deprecated
    public ProtobufModule(final ExtensionRegistry extensionRegistry) {
        this(ProtobufJacksonConfig.builder().extensionRegistry(extensionRegistry).build());
    }

    public ProtobufModule(final ProtobufJacksonConfig config) {
        this.config = config;
    }

    @Override
    public String getModuleName() {
        return "ProtobufModule";
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(final SetupContext context) {
        final SimpleSerializers serializers = new SimpleSerializers();
        serializers.addSerializer(new MessageSerializer(config));
        serializers.addSerializer(new DurationSerializer());
        serializers.addSerializer(new FieldMaskSerializer());
        serializers.addSerializer(new ListValueSerializer());
        serializers.addSerializer(new NullValueSerializer());
        serializers.addSerializer(new StructSerializer());
        serializers.addSerializer(new TimestampSerializer());
        serializers.addSerializer(new ValueSerializer());
        serializers.addSerializer(new WrappedPrimitiveSerializer<>(DoubleValue.class));
        serializers.addSerializer(new WrappedPrimitiveSerializer<>(FloatValue.class));
        serializers.addSerializer(new WrappedPrimitiveSerializer<>(Int64Value.class));
        serializers.addSerializer(new WrappedPrimitiveSerializer<>(UInt64Value.class));
        serializers.addSerializer(new WrappedPrimitiveSerializer<>(Int32Value.class));
        serializers.addSerializer(new WrappedPrimitiveSerializer<>(UInt32Value.class));
        serializers.addSerializer(new WrappedPrimitiveSerializer<>(BoolValue.class));
        serializers.addSerializer(new WrappedPrimitiveSerializer<>(StringValue.class));
        serializers.addSerializer(new WrappedPrimitiveSerializer<>(BytesValue.class));

        context.addSerializers(serializers);

        context.addDeserializers(new MessageDeserializerFactory(config));
        final SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(Duration.class, new DurationDeserializer());
        deserializers.addDeserializer(FieldMask.class, new FieldMaskDeserializer());
        deserializers.addDeserializer(ListValue.class, new ListValueDeserializer().buildAtEnd());
        deserializers.addDeserializer(NullValue.class, new NullValueDeserializer());
        deserializers.addDeserializer(Struct.class, new StructDeserializer().buildAtEnd());
        deserializers.addDeserializer(Timestamp.class, new TimestampDeserializer());
        deserializers.addDeserializer(Value.class, new ValueDeserializer().buildAtEnd());
        deserializers.addDeserializer(DoubleValue.class, wrappedPrimitiveDeserializer(DoubleValue.class));
        deserializers.addDeserializer(FloatValue.class, wrappedPrimitiveDeserializer(FloatValue.class));
        deserializers.addDeserializer(Int64Value.class, wrappedPrimitiveDeserializer(Int64Value.class));
        deserializers.addDeserializer(UInt64Value.class, wrappedPrimitiveDeserializer(UInt64Value.class));
        deserializers.addDeserializer(Int32Value.class, wrappedPrimitiveDeserializer(Int32Value.class));
        deserializers.addDeserializer(UInt32Value.class, wrappedPrimitiveDeserializer(UInt32Value.class));
        deserializers.addDeserializer(BoolValue.class, wrappedPrimitiveDeserializer(BoolValue.class));
        deserializers.addDeserializer(StringValue.class, wrappedPrimitiveDeserializer(StringValue.class));
        deserializers.addDeserializer(BytesValue.class, wrappedPrimitiveDeserializer(BytesValue.class));
        context.addDeserializers(deserializers);
        context.setMixInAnnotations(MessageOrBuilder.class, MessageOrBuilderMixin.class);
    }

    private static <T extends Message> JsonDeserializer<T> wrappedPrimitiveDeserializer(final Class<T> type) {
        return new WrappedPrimitiveDeserializer<>(type).buildAtEnd();
    }

    @JsonAutoDetect(getterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, creatorVisibility = Visibility.NONE, fieldVisibility = Visibility.NONE)
    private static class MessageOrBuilderMixin {
    }
}