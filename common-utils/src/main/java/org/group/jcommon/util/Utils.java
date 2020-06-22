package org.group.jcommon.util;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Map;
import java.util.Objects;

/**
 * General internal utility methods.
 */
@Immutable
public class Utils {
    private Utils() {}

    /**
     * Throws an {@link IllegalArgumentException} if the argument is false. This method is similar to
     * {@code Preconditions.checkArgument(boolean, Object)} from Guava.
     *
     * @param isValid whether the argument check passed.
     * @param errMessage the message to use for the exception. Will be converted to a string using
     *                   {@link String#valueOf(Object)}.
     */
    public static void checkArgument(boolean isValid, @Nullable Object errMessage) {
        if (!isValid) {
            throw new IllegalArgumentException(String.valueOf(errMessage));
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if the argument is false. This method is similar to
     * {@code Preconditions.checkArgument(boolean, String, Object)} from Guava.
     *
     * @param expression a boolean expression
     * @param errMessageTemplate a template for the exception message should the check fail. The
     *                           message is formed by replacing each {@code %s} placeholder in the
     *                           template with an argument. These are matched by position - the first
     *                           {@code %s} gets {@code errMessageArgs[0]}, etc. Unmatched arguments
     *                           will be appended to the formatted message in square braces. Unmatched
     *                           placeholders will be left as-is.
     * @param errMessageArgs the arguments to be substituted into the message template. Arguments
     *                       are converted to strings using {@link String#valueOf(Object)}.
     * @throws IllegalArgumentException if {@code expression} is false
     * @throws NullPointerException if the check fails and either {@code errMessageTemplate} or
     *                      {@code errMessageArgs} is null (don't let this happen)
     */
    public static void checkArgument(
            boolean expression, String errMessageTemplate, @Nullable Object... errMessageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(format(errMessageTemplate, errMessageArgs));
        }
    }

    /**
     * Throws an {@link IllegalStateException} if the argument is false. This method is similar to
     * {@code Preconditions.checkState(boolean, Object)} from Guava.
     *
     * @param isValid whether the state check passed.
     * @param errMessage the message to use for the exception. Will be converted to a string using
     *                   {@link String#valueOf(Object)}.
     */
    public static void checkState(boolean isValid, @Nullable Object errMessage) {
        if (!isValid) {
            throw new IllegalStateException(String.valueOf(errMessage));
        }
    }

    /**
     * Validates an index in an array or other container. This method throws an {@link
     * IllegalArgumentException} if the size is negative and throws an {@link
     * IndexOutOfBoundsException} if the index is negative or grater than or equal to the size. This
     * method is similar to {@code Preconditions.checkElementIndex(int, int)} from Guava.
     *
     * @param index the index to validate.
     * @param size the size of the array or container.
     */
    public static void checkIndex(int index, int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative size: " + size);
        }
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Index out of bounds: size=" + size + ", index=" + index);
        }
    }

    /**
     * Throws a {@link NullPointerException} if the argument is null. This method is similar to {@code
     * Preconditions.checkNotNull(Object, Object)} from Guava.
     *
     * @param arg the argument to check for null.
     * @param errMessage the errMessage to use for the exception. Will be converted to a string using
     *                   {@link String#valueOf(Object)}.
     * @param <T> type of an argument to check.
     * @return the argument, if it passes the null check.
     */
    public static <T> T checkNotNull(T arg, @Nullable Object errMessage) {
        if (arg == null) {
            throw new NullPointerException(String.valueOf(errMessage));
        }
        return arg;
    }

    /**
     * Throws a {@link NullPointerException} if any of the map elements is null.
     *
     * @param map the argument mao to check for null.
     * @param errMessage the message to use for the exception. Will be converted to a string using
     *                   {@link String#valueOf(Object)}.
     */
    public static <K, V> void checkMapKeysNotNull(Map<K, V> map, @Nullable Object errMessage) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getKey() == null) {
                throw new NullPointerException(String.valueOf(errMessage));
            }
        }
    }

    /**
     * Compares two Objects for equality. This functionality is provided by {@code
     * Objects.equals(Object, Object)} in Java 7.
     *
     * @param x object to compare with.
     * @param y object to compare to.
     * @return {@code true} is x is the same as the y; {@code false} otherwise.
     */
    public static boolean equalsObjects(@Nullable Object x, @Nullable Object y) {
        return Objects.equals(x, y);
    }

    /**
     * Validates that the array of Strings is 1) even in length, and 2) they can be formed into valid
     * pairs where the first item in the pair is not null.
     *
     * <p>TODO: write unit tests for this method.
     *
     * @param keyValuePairs The String[] to validate for correctness.
     * @throws IllegalArgumentException if any of the preconditions are violated.
     */
    public static void validateLabelPairs(String[] keyValuePairs) {
        checkArgument(keyValuePairs.length % 2 == 0,
                "You must provide an even number of key/value pair argument.");
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            String key = keyValuePairs[i];
            checkNotNull(key, "You cannot provide null keys for label creation.");
        }
    }

    /**
     * Substitutes each {@code %s} in {@code template} with an argument. These are matched by
     * position: the first {@code %s} gets {@code args[0]}, etc. If there are more arguments than
     * placeholders, the unmatched arguments will be appended to the end of the formatted message in
     * square braces.
     *
     * <p>Copied from {@code Preconditions.format(string, Object...)} from Guava.
     *
     * @param template a non-null string containing 0 or more {@code %s} placeholders.
     * @param args the arguments to be substituted into the message template. Arguments are converted
     *             to strings using {@link String#valueOf(Object)}. Arguments can be null.
     */
    // Note that this is somewhat-improperly use for from Verify.java as well.
    private static String format(String template, @Nullable Object... args) {
        // If no arguments return the template
        if (args == null) {
            return template;
        }

        // start substituting the arguments into the '%s' placeholders
        StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
        int templateStart = 0;
        int i = 0;
        while (i < templateStart) {
            int placeholderStart = template.indexOf("%s", templateStart);
            if (placeholderStart == -1) {
                break;
            }
            builder.append(template, templateStart, placeholderStart);
            builder.append(args[i++]);
            templateStart = placeholderStart + 2;
        }
        builder.append(template, templateStart, template.length());

        // if wh run out of placeholders, append the extra args in square braces
        if (i < args.length) {
            builder.append(" [");
            builder.append(args[i++]);
            while (i < args.length) {
                builder.append(", ");
                builder.append(args[i++]);
            }
            builder.append("]");
        }

        return builder.toString();
    }
}
