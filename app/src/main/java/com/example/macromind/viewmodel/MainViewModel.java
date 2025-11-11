package com.example.macromind.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.macromind.model.UserData;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<String> calorieResult = new MutableLiveData<>();
    private final MutableLiveData<Double> rawCalorieValue = new MutableLiveData<>();

    public LiveData<String> getCalorieResult() {
        return calorieResult;
    }

    public LiveData<Double> getRawCalorieValue() {
        return rawCalorieValue;
    }

    public void calculateCalories(UserData userData) {
        // Step 1: Calculate BMR
        double bmr;
        if (userData.getGender().equalsIgnoreCase("Male")) {
            bmr = 88.36 + (13.4 * userData.getWeight()) + (4.8 * userData.getHeight()) - (5.7 * userData.getAge());
        } else {
            bmr = 447.6 + (9.2 * userData.getWeight()) + (3.1 * userData.getHeight()) - (4.3 * userData.getAge());
        }

        // Step 2: Apply activity multiplier
        double activityFactor = 1.2; // Default for Sedentary
        switch (userData.getActivityLevel()) {
            case "Lightly Active":
                activityFactor = 1.375;
                break;
            case "Moderately Active":
                activityFactor = 1.55;
                break;
            case "Very Active":
                activityFactor = 1.725;
                break;
            case "Extra Active":
                activityFactor = 1.9;
                break;
        }

        double calories = bmr * activityFactor;

        // Step 3: Adjust based on special need
        switch (userData.getSpecialNeed()) {
            case "Weight Loss":
                calories -= 500;
                break;
            case "Weight Gain":
                calories += 500;
                break;
        }

        // Step 4: Set results to LiveData
        rawCalorieValue.setValue(calories);

        String result = "Hello " + userData.getName() + "!\nYour daily calorie need is: " +
                String.format("%.2f", calories) + " kcal/day.";
        calorieResult.setValue(result);
    }
}
