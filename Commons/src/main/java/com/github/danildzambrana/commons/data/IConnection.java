package com.github.danildzambrana.commons.data;

import org.jetbrains.annotations.Nullable;

/**
 * Connection class scheme.
 * @param <T> The type of connection.
 */
public interface IConnection<T> {

    /**
     * Get the connection.
     * @return The instance of the connection was established, otherwise it returns null.
     */
    @Nullable
    T getConnection();

    /**
     * Check if the connection is available.
     * @return True if the connection is available, otherwise it returns false.
     */
    boolean isConnected();
}
