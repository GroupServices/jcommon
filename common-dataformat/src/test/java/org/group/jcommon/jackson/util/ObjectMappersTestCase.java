package org.group.jcommon.jackson.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.group.jcommon.jackson.DataFormatException;
import org.group.jcommon.proto.text.TextBullet;
import org.junit.jupiter.api.Test;

public class ObjectMappersTestCase {
    static class Order {
        @JsonProperty
        String id;
        @JsonProperty
        int cents;
    }

    @Test
    public void testWriteCompact() {
        Order order = new Order();
        order.id = "001";
        order.cents = 0;
        assertEquals("{\"id\":\"001\"}", ObjectMappers.writeObjectCompact(order));

        order.cents = 10;
        assertEquals("{\"id\":\"001\",\"cents\":10}", ObjectMappers.writeObjectCompact(order));

        order.id = null;
        order.cents = 0;
        assertEquals("{}", ObjectMappers.writeObjectCompact(order));
    }

    private static class TestStruct {
        @JsonProperty
        Boolean valid;

        void setValid(Boolean valid) {
            this.valid = valid;
        }
    }

    @Test
    public void testWriteCompactBoolean() {
        TestStruct t = new TestStruct();
        assertEquals("{}", ObjectMappers.writeObjectCompact(t));

        t.setValid(true);
        assertEquals("{\"valid\":true}", ObjectMappers.writeObjectCompact(t));

        t.setValid(false);
        assertEquals("{}", ObjectMappers.writeObjectCompact(t));
    }

    static class Response {
        @JsonProperty
        List<String> screenshotURLs;
    }

    @Test
    public void testSnakeCase() throws Exception {
        Response response = new Response();
        response.screenshotURLs = Lists.newArrayList("https://one.com");
        String json = ObjectMappers.snakeCase().writeValueAsString(response);
        assertEquals("{\"screenshot_urls\":[\"https://one.com\"]}", json);

        response = ObjectMappers.snakeCase().readValue(json, Response.class);
        assertEquals(Lists.newArrayList("https://one.com"), response.screenshotURLs);
    }

    @Test
    public void testMixinTypeReference() {
        String json = "[[\"product0\", 5], [\"product1\", 3]]";
        List<ArrayNode> prods = ObjectMappers.mustReadValue(json, new TypeReference<List<ArrayNode>>() {
        });
        assertEquals(2, prods.size());
        assertEquals("product0", prods.get(0).get(0).asText());
        assertEquals(5, prods.get(0).get(1).asInt());
        assertEquals("product1", prods.get(1).get(0).asText());
        assertEquals(3, prods.get(1).get(1).asInt());
    }

    private static class TestStruct2 {
        @JsonProperty
        Set<String> tags;
    }

    @Test
    public void testFieldSetValues() {
        TestStruct2 t = new TestStruct2();
        t.tags = Sets.newHashSet("CONCIERGE", "CANCELLED", "Chengdu");
        String json = ObjectMappers.mustWriteValue(t);
        assertTrue(json.contains("\"tags\":[\"C"));

        TestStruct2 t2 = ObjectMappers.mustReadValue(json, TestStruct2.class);
        assertEquals(t.tags, t2.tags);
    }

    static class WithUnknown {
        @JsonProperty
        String value;
    }

    @Test
    public void testWithUnknown() {
        String json = "{\"value\":\"foo\",\"int\":12}";
        WithUnknown w = ObjectMappers.mustReadValue(json, WithUnknown.class);
        assertEquals("foo", w.value);

        json = "{\"value\":{\"foo\":12}}";
        boolean caught = false;
        try {
            ObjectMappers.mustReadValue(json, WithUnknown.class);
        } catch (DataFormatException e) {
            caught = true;
        }
        assertTrue(caught);
    }

    private enum Alphabet {
        A, B
    };

    static class WithInvalidEnum {
        @JsonProperty
        Alphabet alphabet;
    }

    @Test
    public void testIgnoreInvalidEnum() {
        String json = "{\"alphabet\":\"ZZ\",\"int\":12}";
        WithInvalidEnum w = ObjectMappers.mustReadValue(json, WithInvalidEnum.class);
        assertNull(w.alphabet);

        json = "{\"alphabet\":{\"int\":22}}";
        boolean caught = false;
        try {
            ObjectMappers.mustReadValue(json, WithInvalidEnum.class);
        } catch (DataFormatException e) {
            caught = true;
        }
        assertTrue(caught);
    }

    static class Wrapper {
        @JsonProperty
        String stringField;
        @JsonProperty
        List<TextBullet> bullets;
    }

    @Test
    public void testEmbeddedProtobuf() {
        Wrapper wrapper = new Wrapper();
        wrapper.stringField = "hello world";
        wrapper.bullets = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            wrapper.bullets.add(TextBullet.newBuilder().setText(String.format("event_%d", i)).build());
        }
        String json = ObjectMappers.mustWriteValue(wrapper);

        Wrapper deserialized = ObjectMappers.mustReadValue(json, Wrapper.class);
        assertEquals("hello world", deserialized.stringField);
        assertEquals(1000, wrapper.bullets.size());
        for (int i = 0; i < 1000; i++) {
            assertEquals(String.format("event_%d", i), wrapper.bullets.get(i).getText());
        }
    }

    @Test
    public void testOmitDefaultValue() {
        Wrapper wrapper = new Wrapper();
        wrapper.stringField = "hello world";
        wrapper.bullets = Lists.newArrayList();
        wrapper.bullets.add(TextBullet.newBuilder().setText("bullet").build());

        String json = ObjectMappers.mustWriteValue(wrapper);
        assertEquals(-1, json.indexOf("highlight"));
    }

    @Test
    public void testReadWriteProtobuf() {
        TextBullet ui = TextBullet.newBuilder().setText("FIRST_USE").build();
        String json = ObjectMappers.mustWriteValue(ui);

        TextBullet deserialized = ObjectMappers.mustReadValue(json, TextBullet.class);
        assertEquals("FIRST_USE", deserialized.getText());
        assertEquals("", deserialized.getColor());

        deserialized = ObjectMappers.mustReadProto(json, TextBullet.class);
        assertEquals("FIRST_USE", deserialized.getText());
        assertEquals("", deserialized.getColor());
    }
}