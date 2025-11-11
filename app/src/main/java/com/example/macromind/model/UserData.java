package com.example.macromind.model;

public class UserData {
    private final String name;
    private final int age;
    private final double weight;
    private final double height;
    private final String gender;
    private final String activityLevel;
    private final String specialNeed;

    public UserData(String name, int age, double weight, double height, String gender, String activityLevel, String specialNeed) {
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
        this.activityLevel = activityLevel;
        this.specialNeed = specialNeed;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public String getGender() {
        return gender;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public String getSpecialNeed() {
        return specialNeed;
    }
}
