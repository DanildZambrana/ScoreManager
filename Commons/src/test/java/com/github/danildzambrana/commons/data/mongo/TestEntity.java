package com.github.danildzambrana.commons.data.mongo;


import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity
public class TestEntity {
    @Id
    private Long   id;
    private String name;
    private String lastName;
    private String age;

    public TestEntity(String name, String lastName, String age) {
        this.name     = name;
        this.lastName = lastName;
        this.age      = age;
    }

    public TestEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}
