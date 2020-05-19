package org.group.jcommon.jackson.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;

@Tag("fastjson")
public class JsonUtilTestCase {
    private class Struct {
        @JsonProperty
        String s;
        @JsonProperty
        int i;

        Struct(String s, int i) {
            this.s = s;
            this.i = i;
        }
    }

    private class TestStringsField {
        @JsonProperty
        List<String> strings;
        @JsonProperty
        List<Integer> ints;
        @JsonProperty
        List<Struct> structs;
    }

    @Test
    public void testGetStringsField() {
        TestStringsField tsf = new TestStringsField();
        tsf.strings = Lists.newArrayList("hello", "", "world");
        tsf.ints = Lists.newArrayList(12, 25);
        tsf.structs = Lists.newArrayList(new Struct("xu'er", 1), new Struct("weizheng", 2));

        JsonNode n = ObjectMappers.mustReadTree(ObjectMappers.mustWriteValue(tsf));

        List<String> sentinel = Lists.newArrayList("sentinel");

        Assertions.assertEquals(tsf.strings, JsonUtil.getStringsFieldOr(n, "strings", sentinel));
        Assertions.assertEquals(sentinel, JsonUtil.getStringsFieldOr(n, "crap", sentinel));

        Assertions.assertEquals(sentinel, JsonUtil.getStringsFieldOr(n, "ints", sentinel));

        Assertions.assertEquals(sentinel, JsonUtil.getStringsFieldOr(n, "structs", sentinel));

        tsf = new TestStringsField();
        tsf.strings = Lists.newArrayList("", "");
        n = ObjectMappers.mustReadTree(ObjectMappers.mustWriteValue(tsf));
        Assertions.assertEquals(tsf.strings, JsonUtil.getStringsFieldOr(n, "strings", sentinel));

        // This is where it's getting a little confusing:
        Assertions.assertNull(tsf.ints);
        n = ObjectMappers.mustReadTree(ObjectMappers.mustWriteValue(tsf));
        Assertions.assertEquals(sentinel, JsonUtil.getStringsFieldOr(n, "ints", sentinel));

        tsf.ints = Lists.newArrayList();
        n = ObjectMappers.mustReadTree(ObjectMappers.mustWriteValue(tsf));
        Assertions.assertEquals(Lists.newArrayList(), JsonUtil.getStringsFieldOr(n, "ints", sentinel));
    }

    @Test
    public void testGetFieldAsDouble() {
        String json = "{\"rate\":102.2,\"label\":\"crap\"}";
        double v = JsonUtil.getFieldAsDoubleOr(ObjectMappers.mustReadTree(json), "rate", 0);

        v = JsonUtil.getFieldAsDoubleOr(ObjectMappers.mustReadTree(json), "label", 2.0);
        assertEquals(2.0, v, 0.01);

        v = JsonUtil.getFieldAsDoubleOr(ObjectMappers.mustReadTree(json), "poof", 3.0);
        assertEquals(3.0, v, 0.01);
    }

    @Test
    public void testForKV() {
        JsonNode n = JsonUtil.forKV("hello", "world");
        assertEquals("world", n.get("hello").asText());
        assertNull(n.get("hello2"));

        n = JsonUtil.forKV("k0", "v0", "k1", "v1", "k2");
        assertEquals("v0", n.get("k0").asText());
        assertEquals("v1", n.get("k1").asText());
        assertNull(n.get("k2"));

        assertEquals(0, JsonUtil.forKV().size());
        assertEquals(0, JsonUtil.forKV("k").size());

        assertEquals("null", JsonUtil.forKV("hello", null).get("hello").asText());
    }
}