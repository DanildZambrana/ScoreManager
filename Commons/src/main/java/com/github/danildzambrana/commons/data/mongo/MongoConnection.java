package com.github.danildzambrana.commons.data.mongo;

import com.github.danildzambrana.commons.data.IConnection;
import com.github.danildzambrana.commons.utils.FieldUtils;
import com.mongodb.*;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class MongoConnection implements IConnection<Datastore> {
    private final MongoClient client;
    private final Morphia     morphia;
    private final Datastore   datastore;
    private final String      database;

    private MongoConnection() throws Exception {
        throw new IllegalAccessException("This constructor cannot be used. Please use "
                + Builder.class.getName());
    }

    public MongoConnection(Builder builder) {
        this.database = builder.getDatabaseName();
        MongoClientOptions options =
                MongoClientOptions.builder().connectTimeout(3000).sslEnabled(builder.isSsl()).build();

        MongoCredential credential;

        if (builder.getUser() != null && builder.getPassword() != null && builder.getAuthDatabase() != null) {
            credential = MongoCredential.createCredential(builder.getUser(), builder.getAuthDatabase(),
                    builder.getPassword());

            this.client = new MongoClient(new ServerAddress(builder.getHost(), builder.getPort()), credential, options);
        } else {
            this.client = new MongoClient(new ServerAddress(builder.getHost(), builder.getPort()), options);
        }
        this.morphia = new Morphia();

        if (builder.getMapPackage() != null) {
            this.morphia.mapPackage(builder.getMapPackage());
        }

        if (builder.getMappedClazz() != null) {
            this.morphia.map(builder.getMappedClazz());
        }

        if (builder.getClassLoader() != null) {
            this.morphia.getMapper().setOptions(MapperOptions.builder().classLoader(builder.getClassLoader()).build());
        }

        FieldUtils.requireArgument(!builder.getDatabaseName().isEmpty());
        FieldUtils.requireArgument(isConnected(), "error connecting to mongodb. Host: '%s', Database Name: '%s'",
                builder.getHost(), builder.getDatabaseName());
        this.datastore = morphia.createDatastore(this.client, builder.getDatabaseName());
        datastore.ensureIndexes();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public @Nullable Datastore getConnection() {
        return this.datastore;
    }

    @Override
    public boolean isConnected() {
        try {
            Bson ping = new BasicDBObject("ping", "1");
            client.getDatabase(database).runCommand(ping);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Morphia getMorphia() {
        return morphia;
    }

    public static class Builder {
        private String  user;
        private char[]  password     = null;
        private String  host;
        private String  authDatabase = "admin";
        private int     port         = 27017;
        private String  databaseName;
        private boolean ssl;

        private String      mapPackage;
        private Class<?>[]  mappedClazz;
        private ClassLoader classLoader;

        private Builder() {
        }

        public String getUser() {
            return user;
        }

        public Builder setUser(String user) {
            this.user = user;
            return this;
        }

        public char[] getPassword() {
            return password;
        }

        public Builder setPassword(char[] password) {
            this.password = password;
            return this;
        }

        public Builder setPassword(@NotNull String password) {
            this.password = password.toCharArray();
            return this;
        }

        public String getHost() {
            return host;
        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public String getAuthDatabase() {
            return authDatabase;
        }

        public Builder setAuthDatabase(String authDatabase) {
            this.authDatabase = authDatabase;
            return this;
        }

        public int getPort() {
            return port;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public String getDatabaseName() {
            return databaseName;
        }

        public Builder setDatabaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        public boolean isSsl() {
            return ssl;
        }

        public Builder setSsl(boolean ssl) {
            this.ssl = ssl;
            return this;
        }

        public String getMapPackage() {
            return mapPackage;
        }

        public Builder setMapPackage(String mapPackage) {
            this.mapPackage = mapPackage;
            return this;
        }

        public Class<?>[] getMappedClazz() {
            return mappedClazz;
        }

        public Builder setMappedClazz(Class<?>... mappedClazz) {
            this.mappedClazz = mappedClazz;
            return this;
        }

        public ClassLoader getClassLoader() {
            return classLoader;
        }

        public Builder setClassLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "user='" + user + '\'' +
                    ", password=" + Arrays.toString(password) +
                    ", host='" + host + '\'' +
                    ", authDatabase='" + authDatabase + '\'' +
                    ", port=" + port +
                    ", databaseName='" + databaseName + '\'' +
                    ", ssl=" + ssl +
                    ", mapPackage='" + mapPackage + '\'' +
                    ", mappedClazz=" + Arrays.toString(mappedClazz) +
                    ", classLoader=" + classLoader +
                    '}';
        }

        public MongoConnection build() {
            return new MongoConnection(this);
        }
    }
}
