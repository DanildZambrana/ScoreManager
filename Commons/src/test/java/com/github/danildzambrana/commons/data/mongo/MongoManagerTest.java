package com.github.danildzambrana.commons.data.mongo;

import org.junit.jupiter.api.Test;

import java.util.Optional;

class MongoManagerTest {
    private final MongoManager<Long, TestEntity> manager;
    private final TestEntity                     test    = new TestEntity("Juan", "Lopez", "15");
    private       boolean                        execute = true;

    public MongoManagerTest() {
        MongoConnection.Builder builder = MongoConnection.builder()
                .setDatabaseName("test")
                .setAuthDatabase("admin")
                .setHost("35.203.60.248")
                .setPort(27017)
                .setSsl(false)
                .setUser("user")
                .setPassword("password")
                .setMappedClazz(TestEntity.class);
        manager =
                new MongoManager<>(builder.build());
        if (manager.getConnection().isConnected()) {
            System.out.println("La conexion fue establecida");
        } else {
            execute = true;
            System.out.println("La conexion no se pudo establecer");
        }
        test.setId(1L);
    }

    @Test
    void save() {
        if (!execute) {
            return;
        }

        manager.save(test);
    }

    @Test
    void get() {
        if (!execute) {
            return;
        }

        Optional<TestEntity> testEntity = manager.get(1L, TestEntity.class);

        testEntity.ifPresent(o -> System.out.println(o.toString()));
    }

    @Test
    void delete() {
        Optional<TestEntity> testEntity = manager.get(1L, TestEntity.class);

        testEntity.ifPresent(o -> {
            System.out.println(o.toString());
            manager.delete(o);
        });
    }
}