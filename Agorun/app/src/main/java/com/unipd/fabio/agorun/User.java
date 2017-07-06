package com.unipd.fabio.agorun;

/**
 * Created by fabio on 15/05/17.
 */

public class User {

    private String name;
    private String surname;
    private char sex;
    private int age;
    private int weight;
    private String experience;
    private String rank;

    public User(String name,
                String surname,
                char sex,
                int age,
                int weight,
                String experience,
                String rank) {

        this.name = name;
        this.surname = surname;
        this.sex = sex;
        this.age = age;
        this.weight = weight;
        this.experience = experience;
        this.rank = rank;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getName() {
        return this.name;
    }

    public String getSurname() {
        return this.surname;
    }

    public char getSex() {
        return this.sex;
    }

    public int getAge() {
        return this.age;
    }

    public int getWeight() {
        return this.weight;
    }

    public String getExperience() {
        return this.experience;
    }

    public String getRank() {
        return this.rank;
    }

}
