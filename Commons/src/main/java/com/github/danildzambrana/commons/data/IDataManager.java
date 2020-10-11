package com.github.danildzambrana.commons.data;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * This class represents the scheme to follow for the creation of a Data Manager
 * @param <I> Type of ID.
 * @param <T> Type of object to save.
 */
public interface IDataManager<I, T> {

    /**
     * save the value provided.
     * @param t The value to save.
     * @return true if the values has been saved, otherwise return false.
     */
    boolean save(@NotNull T t);


    /**
     * Get the value with provided id.
     * @param id the id to find.
     * @param clazz type of the objet to find.
     * @return An instance of {@link Optional} with the obtained value.
     */
    Optional<T> get(@NotNull I id, Class<T> clazz);

    /**
     * Remove the value.
     * @param t value to remove.
     * @return true if the value was removed, otherwise return false.
     */
    boolean delete(@NotNull T t);
}
