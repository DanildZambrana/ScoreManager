package com.github.danildzambrana.commons.data.mysql;

import org.junit.jupiter.api.Test;

class MySQLManagerTest {
    private final MySQLManager<Long, TestEntity> manager;
    private final TestEntity                     test    = new TestEntity("Juan", "Lopez", "15");
    private       boolean                        execute = true;

    public MySQLManagerTest() {
        manager =
                new MySQLManager<>(MySQLConnection.builder()
                        .setUser("")
                        .setPassword("")
                        .setDataBaseName("curso_spring")
                        .setHost("db4free.net")
                        .addURLProperty("serverTimezone", "UTC")
                        .addURLProperty("useSSL", "false")
                        /*.setPool(true)
                        .addPoolProperty("connection.provider_class", "org.hibernate.connection.C3P0ConnectionProvider")
                        .addPoolProperty("hibernate.c3p0.min_size", "5")
                        .addPoolProperty("hibernate.x3p0.max_size", "20")
                        .addPoolProperty("hibernate.c3p0.timeout", "300")
                        .addPoolProperty("hibernate.c3p0.max_statements", "50")
                        .addPoolProperty("hibernate.c3p0.idle_test_period", "3000")*/
                        .setMappedClazz(TestEntity.class)
                        .build());

        if (manager.getConnection().isConnected()) {
            System.out.println("La conexion fue establecida");
        } else {
            execute = false;
            System.out.println("la conexion no se pudo establecer");
        }
    }

    @Test
    void save() {
        assert execute;
        manager.save(test);
    }

    @Test
    void get() {
        assert execute;

        System.out.println(manager.get(1L, TestEntity.class).toString());
    }

    @Test
    void delete() {
        assert execute;
        manager.get(1L, TestEntity.class).ifPresent(manager::delete);
    }
}