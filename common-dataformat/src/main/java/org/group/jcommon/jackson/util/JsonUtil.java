package org.group.jcommon.jackson.util;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

public class JsonUtil {
    public static String getFieldAsTextOr(JsonNode n, String f, String d) {
        if (n != null && n.has(f)) {
            String v = n.get(f).asText();
            if (v != null) {
                return v;
            }
        }
        return d;
    }

    public static String getFieldAsTextOrNull(JsonNode n, String f) {
        return getFieldAsTextOr(n, f, null);
    }

    public static int getFieldAsIntOr(JsonNode n, String f, int d) {
        if (n != null && n.has(f)) {
            return n.get(f).asInt(d);
        }
        return d;
    }

    public static long getFieldAsLongOr(JsonNode n, String f, long d) {
        if (n != null && n.has(f)) {
            return n.get(f).asLong(d);
        }
        return d;
    }

    public static double getFieldAsDoubleOr(JsonNode n, String f, double d) {
        if (n != null && n.has(f)) {
            return n.get(f).asDouble(d);
        }
        return d;
    }

    public static List<String> getStringsFieldOr(JsonNode n, String f, List<String> d) {
        if (n == null || !n.has(f)) {
            return d;
        }
        JsonNode v = n.get(f);
        if (!v.isArray()) {
            return d;
        }

        ArrayNode array = (ArrayNode) v;
        List<String> r = Lists.newArrayList();
        if (array.size() == 0) {
            return r;
        }
        boolean valid = false;
        for (JsonNode o : array) {
            if (o.getNodeType() == JsonNodeType.STRING) {
                String s = o.asText();
                if (s != null) {
                    r.add(o.asText());
                    valid = true;
                }
            }
        }
        return valid ? r : d;
    }

    public static JsonNode forKV(String... kv) {
        ObjectNode r = ObjectMappers.get().createObjectNode();
        for (int i = 0; i < kv.length - 1; i += 2) {
            r.put(kv[i], kv[i + 1]);
        }
        return r;
    }
}