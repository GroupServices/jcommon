package org.group.jcommon.protobuf.jackson;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;
import com.google.common.base.Ascii;

@SuppressWarnings("serial")
public class PropertyNamingStrategyWrapper extends PropertyNamingStrategyBase {
    private static final PropertyNamingStrategyBase SNAKE_TO_CAMEL = new SnakeToCamelNamingStrategy();

    private final PropertyNamingStrategyBase delegate;

    public PropertyNamingStrategyWrapper(PropertyNamingStrategy delegate) {
        if (delegate instanceof PropertyNamingStrategyBase) {
            this.delegate = (PropertyNamingStrategyBase) delegate;
        } else {
            this.delegate = SNAKE_TO_CAMEL;
        }
    }

    @Override
    public String translate(String fieldName) {
        return delegate.translate(fieldName);
    }

    public static class SnakeToCamelNamingStrategy extends PropertyNamingStrategyBase {

        @Override
        public String translate(String fieldName) {
            StringBuilder r = new StringBuilder();
            boolean wordStart = true;
            for (int i = 0; i < fieldName.length(); i++) {
                char ch = fieldName.charAt(i);
                if (ch == '_') {
                    wordStart = true;
                    continue;
                }
                if (wordStart) {
                    r.append(r.length() > 0 ? Ascii.toUpperCase(ch) : Ascii.toLowerCase(ch));
                    wordStart = false;
                } else {
                    r.append(ch);
                }
            }
            return r.toString();
        }
    }
}
