package com.github.danildzambrana.commons.data.mysql;

import com.github.danildzambrana.commons.data.IConnection;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.internal.BootstrapServiceRegistryImpl;
import org.hibernate.cfg.Configuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.spi.ServiceRegistry;
import java.util.*;


/**
 * MySQL Adapter of {@link IConnection}
 */
public class MySQLConnection implements IConnection<Session> {
    private final String         URL;
    private       SessionFactory sessionFactory;

    /**
     * Use {@link MySQLConnectionBuilder} instead.
     */
    private MySQLConnection() {
        URL = null;
        throw new RuntimeException(new IllegalAccessException("This constructor cannot be used. Please use "
                + MySQLConnectionBuilder.class.getName()));
    }

    private MySQLConnection(MySQLConnectionBuilder builder) {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connector.driver_class", builder.getDriver());

        URL = buildURL(builder.getHost(), builder.getPort(), builder.getDataBaseName(), builder.getUrlProperties());
        configuration.setProperty("hibernate.connection.url", URL);

        configuration.setProperty("hibernate.connection.username", builder.getUser());
        configuration.setProperty("hibernate.connection.password", builder.getPassword());
        configuration.setProperty("hibernate.dialect", builder.getDialect());
        configuration.setProperty("show_sql", builder.isDebug() + "");
        configuration.setProperty("hibernate.hbm2ddl.auto", builder.getHbm2ddl());


        for (Class<?> mappedClazz : builder.mappedClazz) {
            configuration.addAnnotatedClass(mappedClazz);
        }

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
        url.append(host);
        if (port > 0 && port != 3306) {
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
    public static MySQLConnectionBuilder builder() {
        return new MySQLConnectionBuilder();
    }

    /**
     * Builder adapter to {@link MySQLConnection}
     */
    public static class MySQLConnectionBuilder {
        private String user;
        private String password;
        private String host;
        private int    port;
        private String dataBaseName;

        private String  driver  = "com.mysql.jdbc.Driver";
        private String  dialect = "org.hibernate.dialect.MySQL8Dialect";
        private boolean debug   = false;

        private Class<?>[]  mappedClazz;
        private String hbm2ddl = "update";

        public String getHbm2ddl() {
            return hbm2ddl;
        }

        public MySQLConnectionBuilder setHbm2ddl(String hbm2ddl) {
            this.hbm2ddl = hbm2ddl;
            return this;
        }

        //Pool
        private boolean             pool           = false;
        private Map<String, String> poolProperties = new HashMap<>();

        public Map<String, String> getPoolProperties() {
            return poolProperties;
        }

        public MySQLConnectionBuilder addPoolProperty(String property, String value) {
            this.poolProperties.put(property, value);
            return this;
        }

        private Map<String, String> urlProperties = new HashMap<>();

        private MySQLConnectionBuilder() {
        }


        public String getUser() {
            return user;
        }

        public MySQLConnectionBuilder setUser(String user) {
            this.user = user;
            return this;
        }

        public String getPassword() {
            return password;
        }

        public MySQLConnectionBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public String getHost() {
            return host;
        }

        public MySQLConnectionBuilder setHost(String host) {
            this.host = host;
            return this;
        }

        public int getPort() {
            return port;
        }

        public MySQLConnectionBuilder setPort(int port) {
            this.port = port;
            return this;
        }

        public String getDataBaseName() {
            return dataBaseName;
        }

        public MySQLConnectionBuilder setDataBaseName(String dataBaseName) {
            this.dataBaseName = dataBaseName;
            return this;
        }

        public String getDriver() {
            return driver;
        }

        public MySQLConnectionBuilder setDriver(String driver) {
            this.driver = driver;
            return this;
        }

        public String getDialect() {
            return dialect;
        }

        public MySQLConnectionBuilder setDialect(String dialect) {
            this.dialect = dialect;
            return this;
        }

        public boolean isDebug() {
            return debug;
        }

        public MySQLConnectionBuilder setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Class<?>[] getMappedClazz() {
            return mappedClazz;
        }

        public MySQLConnectionBuilder setMappedClazz(Class<?>... mappedClazz) {
            this.mappedClazz = mappedClazz;
            return this;
        }

        public Map<String, String> getUrlProperties() {
            return urlProperties;
        }

        public MySQLConnectionBuilder addProperty(String property, String value) {
            this.urlProperties.put(property, value);
            return this;
        }


        //POOL

        public boolean isPool() {
            return pool;
        }

        public MySQLConnectionBuilder setPool(boolean pool) {
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
