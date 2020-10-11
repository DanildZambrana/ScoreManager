package com.github.danildzambrana.commons.utils;

public class FieldUtils {
    /**
     * Ensures that the argument expression is true.
     */
    public static void requireArgument(boolean expression, String template, Object... args) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(template, args));
        }
    }

    /**
     * Ensures that the argument expression is true.
     */
    public static void requireArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Ensures that the state expression is true.
     */
    public static void requireState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }

    /**
     * Ensures that the state expression is true.
     */
    public static void requireState(boolean expression, String template, Object... args) {
        if (!expression) {
            throw new IllegalStateException(String.format(template, args));
        }
    }
}