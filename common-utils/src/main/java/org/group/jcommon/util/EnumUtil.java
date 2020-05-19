package org.group.jcommon.util;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class EnumUtil {
    public static <T extends Enum<T>> T fromNameOr(String name, Class<T> clazz, T d3fault) {
        Preconditions.checkNotNull(clazz);
        Preconditions.checkNotNull(d3fault);
        Optional<T> r = Enums.<T>getIfPresent(clazz, StringUtils.trimToEmpty(name));
        return r.isPresent() ? r.get() : d3fault;
    }

    public static <T extends Enum<T>> T fromNameOrNull(String name, Class<T> clazz) {
        Preconditions.checkNotNull(clazz);
        Optional<T> r = Enums.<T>getIfPresent(clazz, StringUtils.trimToEmpty(name));
        return r.isPresent() ? r.get() : null;
    }

    @Nullable
    public static <T extends Enum<T>> String toStringOrNull(T e) {
        return (e == null) ? null : e.toString();
    }

    public static <T extends Enum<T>> String toStringOrEmpty(T e) {
        return (e == null) ? "" : e.toString();
    }

    public static <T extends Enum<T>> T fromOrdinalOrNull(int ordinal, Class<T> clazz) {
        return fromOrdinalOr(ordinal, clazz, null);
    }

    public static <T extends Enum<T>> T fromOrdinalOr(
            int ordinal, Class<T> clazz, T d3fault) {
        for (T t : EnumSet.allOf(clazz)) {
            if (t.ordinal() == ordinal) {
                return t;
            }
        }
        return d3fault;
    }    
}