package com.github.danildzambrana.commons.data.mysql;

import com.github.danildzambrana.commons.data.IConnection;
import com.github.danildzambrana.commons.utils.FieldUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * MySQL Adapter of {@link IConnection}
 */
public class MySQLConnection implements IConnection<Session> {
    private final SessionFactory sessionFactory;

    /**
     * Use {@link Builder} instead.
     */
    private MySQLConnection() {
        throw new RuntimeException(new IllegalAccessException("This constructor cannot be used. Please use "
                + Builder.class.getName()));
    }

    private MySQLConnection(Builder builder) {
        Configuration configuration = new Configuration();

        FieldUtils.requireArgument(!builder.getDriver().isEmpty());
        configuration.setProperty("hibernate.connector.driver_class", builder.getDriver());

        String url = buildURL(builder.getHost(), builder.getPort(), builder.getDataBaseName(),
                builder.getUrlProperties());
        configuration.setProperty("hibernate.connection.url", url);

        configuration.setProperty("hibernate.connection.username", builder.getUser());
        configuration.setProperty("hibernate.connection.password", builder.getPassword());

        FieldUtils.requireArgument(!builder.getDialect().isEmpty());
        configuration.setProperty("hibernate.dialect", builder.getDialect());
        configuration.setProperty("show_sql", builder.isDebug() + "");
        configuration.setProperty("hibernate.hbm2ddl.auto", builder.getHbm2ddl());

        builder.getHibernateProperties().forEach(configuration::setProperty);


        for (Class<?> mappedClazz : builder.mappedClazz) {
            configuration.addAnnotatedClass(mappedClazz);
        }

        //TODO Improve this code.
        if (builder.isPool()) {
            builder.poolProperties.forEach(configuration::setProperty);
        }

        StandardServiceRegistry serviceRegistry =
                new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();

        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }

    @Override
    public @Nullable Session getConnection() {
        return sessionFactory.openSession();
    }

    @Override
    public boolean isConnected() {
        return !sessionFactory.isClosed();
    }

    private String buildURL(String host, int port, String dataBaseName, Map<String, String> properties) {
        StringBuilder url = new StringBuilder("jdbc:mysql://");
        FieldUtils.requireArgument(!host.isEmpty());
        url.append(host);

        FieldUtils.requireArgument(port > 0);
        if (port != 3306) {
            url.append(":").append(port);
        }

        if (!dataBaseName.isEmpty()) {
            url.append("/").append(dataBaseName);
        }

        if (properties.size() > 0) {
            url.append("?");

            Iterator<Map.Entry<String, String>> iterator = properties.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, String> entry    = iterator.next();
                String                    property = entry.getKey();
                String                    value    = entry.getValue();

                url.append(property).append("=").append(value);

                if (iterator.hasNext()) {
                    url.append("&");
                }
            }
        }

        return url.toString();
    }

    @NotNull
    public static MySQLConnection.Builder builder() {
        return new Builder();
    }

    /**
     * Builder adapter to {@link MySQLConnection}
     */
    public static class Builder {
        private String user;
        private String password;
        private String host;
        private int    port = 3306;
        private String dataBaseName;

        private String  driver  = "com.mysql.jdbc.Driver";
        private String  dialect = "org.hibernate.dialect.MySQL8Dialect";
        private boolean debug   = false;

        private Class<?>[]  mappedClazz;
        private String hbm2ddl = "update";

        public String getHbm2ddl() {
            return hbm2ddl;
        }

        private Builder() {
        }

        private final Map<String, String> poolProperties = new HashMap<>();
        private final Map<String, String> urlProperties  = new HashMap<>();

        public Map<String, String> getPoolProperties() {
            return poolProperties;
        }

        public Builder setHbm2ddl(String hbm2ddl) {
            this.hbm2ddl = hbm2ddl;
            return this;
        }

        private final Map<String, String> hibernateProperties = new HashMap<>();
        //Pool
        private       boolean             pool                = false;

        public Map<String, String> getHibernateProperties() {
            return hibernateProperties;
        }

        public Builder addPoolProperty(String property, String value) {
            this.poolProperties.put(property, value);
            return this;
        }

        public Builder addHibernateProperty(String property, String value) {
            this.hibernateProperties.put(property, value);
            return this;
        }


        public String getUser() {
            return user;
        }

        public Builder setUser(String user) {
            this.user = user;
            return this;
        }

        public String getPassword() {
            return password;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public String getHost() {
            return host;
        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public int getPort() {
            return port;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public String getDataBaseName() {
            return dataBaseName;
        }

        public Builder setDataBaseName(String dataBaseName) {
            this.dataBaseName = dataBaseName;
            return this;
        }

        public String getDriver() {
            return driver;
        }

        public Builder setDriver(String driver) {
            this.driver = driver;
            return this;
        }

        public String getDialect() {
            return dialect;
        }

        public Builder setDialect(String dialect) {
            this.dialect = dialect;
            return this;
        }

        public boolean isDebug() {
            return debug;
        }

        public Builder setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Class<?>[] getMappedClazz() {
            return mappedClazz;
        }

        public Builder setMappedClazz(Class<?>... mappedClazz) {
            this.mappedClazz = mappedClazz;
            return this;
        }

        public Map<String, String> getUrlProperties() {
            return urlProperties;
        }

        public Builder addURLProperty(String property, String value) {
            this.urlProperties.put(property, value);
            return this;
        }


        //POOL

        public boolean isPool() {
            return pool;
        }

        public Builder setPool(boolean pool) {
            this.pool = pool;
            return this;
        }

        /**
         * build a instance of {@link MySQLConnection}
         *
         * @return {@link MySQLConnection} instance.
         */
        public @NotNull MySQLConnection build() {
            return new MySQLConnection(this);
        }
    }
}
