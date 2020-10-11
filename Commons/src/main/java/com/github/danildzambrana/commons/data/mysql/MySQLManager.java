package com.github.danildzambrana.commons.data.mysql;

import com.github.danildzambrana.commons.data.IConnection;
import com.github.danildzambrana.commons.data.IDataManager;
import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Optional;

/**
 * Mysql Adapter of {@link IDataManager}
 * @param <I> Type of ID.
 * @param <T> Type of object to save.
 */
public class MySQLManager<I extends Serializable, T extends Serializable> implements IDataManager<I, T> {
    protected IConnection<Session> connection;

    public MySQLManager(IConnection<Session> connection) {
        this.connection = connection;
    }

    public IConnection<Session> getConnection() {
        return connection;
    }

    @Override
    public boolean save(@NotNull T t) {
        Session session = connection.getConnection();

        if (session == null) {
            return false;
        }

        try {
            session.beginTransaction();
            session.saveOrUpdate(t);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            return false;
        } finally {
            session.close();
        }

        return true;
    }

    @Override
    public Optional<T> get(@NotNull I id, Class<T> clazz) {
        Session session = connection.getConnection();
        if (session == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(session.get(clazz, id));
    }

    @Override
    public boolean delete(@NotNull T t) {
        Session session = connection.getConnection();
        if (session == null) {
            return false;
        }
        try {
            session.beginTransaction();
            session.delete(t);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            return false;
        } finally {
            session.close();
        }

        return true;
    }
}
