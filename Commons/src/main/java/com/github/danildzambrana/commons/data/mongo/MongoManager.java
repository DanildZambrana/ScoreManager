package com.github.danildzambrana.commons.data.mongo;

import com.github.danildzambrana.commons.data.IConnection;
import com.github.danildzambrana.commons.data.IDataManager;
import dev.morphia.Datastore;
import dev.morphia.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Mongo Adapter of {@link IDataManager}
 *
 * @param <I> type of ID
 * @param <T> Type of object to save.
 */
public class MongoManager<I, T> implements IDataManager<I, T> {
    private final IConnection<Datastore> connection;

    /**
     * Construct instance of manager. See {@link MongoConnection}
     *
     * @param connection the connection to this manager.
     */
    public MongoManager(IConnection<Datastore> connection) {
        this.connection = connection;
    }

    @Override
    public boolean save(@NotNull T t) {
        Datastore connection = this.connection.getConnection();
        if (connection == null) {
            return false;
        }

        Key<T> save = connection.save(t);
        return save != null;
    }

    @Override
    public Optional<T> get(@NotNull I id, Class<T> clazz) {
        Datastore connection = this.connection.getConnection();
        if (connection == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(
                connection.createQuery(clazz).field("_id").equal(id).first()
        );
    }

    @Override
    public boolean delete(@NotNull T t) {
        Datastore connection = this.connection.getConnection();
        if (connection == null) {
            return false;
        }

        return connection.delete(t).isUpdateOfExisting();
    }

    @Override
    public IConnection<Datastore> getConnection() {
        return connection;
    }
}
