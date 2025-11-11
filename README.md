# MacroMind Calorie Calculator & Diet Planner

This project is a practical demonstration of the Model-View-ViewModel (MVVM) architecture in an Android application. It serves as a calorie calculator that provides personalized daily calorie estimates and diet plans based on user input.

## Core Concept

The MVVM pattern separates the application into three interconnected components:

*   **Model**: Represents the data and business logic. In this app, it's the `UserData` class that holds the user's input. It is completely unaware of the UI.
*   **View**: The UI of the application (Activities, Fragments, XML layouts). Its job is to display data and forward user actions (like button clicks) to the ViewModel.
*   **ViewModel**: Acts as a bridge between the Model and the View. It holds the application's state, performs calculations, and is not destroyed during configuration changes (like screen rotations), preventing data loss.

---

## File-by-File Breakdown

This is a detailed walkthrough of the entire setup.

### 1. The Model: `UserData.java`

This is the data class for our application. The Model's only job is to hold the data in a structured way. It is a plain Java class (often called a POJO - Plain Old Java Object) that is created from the user's input.

```java
// File: app/src/main/java/com/example/macromind/model/UserData.java

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

    // Getter methods for all fields...
    public String getName() { return name; }
    public int getAge() { return age; }
    public double getWeight() { return weight; }
    public double getHeight() { return height; }
    public String getGender() { return gender; }
    public String getActivityLevel() { return activityLevel; }
    public String getSpecialNeed() { return specialNeed; }
}
```
**Line-by-line:**
*   `private final ...`: All fields are `private` to ensure they are only set via the constructor (encapsulation) and `final` because this object represents a snapshot of user data at a specific moment; it is immutable.
*   `public UserData(...)`: The constructor. It takes all the user's inputs as arguments and assigns them to the corresponding fields. This is the only way to create a `UserData` object.
*   `public String getName()`: A public "getter" method. This allows other parts of the application (specifically the ViewModel) to safely read the value of the fields without being able to modify them directly. There is a getter for every field.

### 2. The ViewModel: `MainViewModel.java`

The ViewModel acts as the bridge between the Model (the data) and the View (the screen). It holds the application's state and contains the core business logic—in this case, the calorie calculation. The View provides the ViewModel with a `UserData` object, and the ViewModel provides the View with results to display.

```java
// File: app/src/main/java/com/example/macromind/viewmodel/MainViewModel.java

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
        // ... (BMR and calorie calculation logic)

        // Step 4: Set results to LiveData
        rawCalorieValue.setValue(calories);
        String result = "Hello " + userData.getName() + "!\nYour daily calorie need is: " +
                String.format("%.2f", calories) + " kcal/day.";
        calorieResult.setValue(result);
    }
}
```
**Line-by-line:**
*   `extends ViewModel`: This is a special class from Android Jetpack that makes `MainViewModel` lifecycle-aware. It will survive configuration changes like screen rotation, preserving its data.
*   `private final MutableLiveData<String> calorieResult`: A `LiveData` object that will hold the final, formatted string for display (e.g., "Hello Prave! Your daily calorie need is..."). It's `private` and `Mutable` so only the ViewModel can change its value.
*   `private final MutableLiveData<Double> rawCalorieValue`: A second `LiveData` object that holds the raw, unformatted calorie number (e.g., 1855.75). This is used by the View for logic, like deciding which diet plan to show.
*   `public LiveData<String> getCalorieResult()`: The public getter for the formatted result. It returns an immutable `LiveData`, meaning the View can observe but not change the data, enforcing the MVVM pattern.
*   `public LiveData<Double> getRawCalorieValue()`: The public getter for the raw calorie value, also returning an immutable `LiveData`.
*   `public void calculateCalories(UserData userData)`: This public method is called by the View. It takes the `UserData` model, performs all the BMR and activity calculations, and then updates the `LiveData` objects with the results using `setValue()`.

### 3. The View: `activity_main.xml` and `MainActivity.java`

The View is responsible for displaying the data from the ViewModel and handling user input.

#### `activity_main.xml` (The Layout)

This XML file defines the entire user interface, including input fields, spinners for choices, and buttons.

```xml
<!-- File: app/src/main/res/layout/activity_main.xml -->

<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/scrollView"
    ... >

    <LinearLayout
        android:orientation="vertical" ... >

        <EditText android:id="@+id/etName" ... />
        <EditText android:id="@+id/etAge" ... />
        <EditText android:id="@+id/etWeight" ... />
        <EditText android:id="@+id/etHeight" ... />

        <Spinner android:id="@+id/spGender" ... />
        <Spinner android:id="@+id/spActivity" ... />
        <Spinner android:id="@+id/spNeed" ... />

        <Button
            android:id="@+id/btnCalculate"
            android:text="Calculate Calories" ... />

        <TextView
            android:id="@+id/tvResult" ... />

        <Button
            android:id="@+id/btnDietPlan"
            android:visibility="gone" ... />

        <TextView
            android:id="@+id/tvDietPlan"
            android:visibility="gone" ... />

    </LinearLayout>
</ScrollView>
```
*   Each UI element has a unique `android:id` (e.g., `@+id/etName`). This ID is how the `MainActivity` finds and interacts with specific UI elements.
*   The `Button` and `TextView` for the diet plan have `android:visibility="gone"`, meaning they are hidden by default and will be shown programmatically.

#### `MainActivity.java` (The UI Controller)

This class controls the UI. It connects the XML layout to the code, handles button clicks, and observes the ViewModel for data changes.

```java
// File: app/src/main/java/com/example/macromind/view/MainActivity.java

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    // ... (references for all UI elements: EditText, Spinner, Button, etc.)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Link all UI elements using findViewById()
        etName = findViewById(R.id.etName);
        // ... (linking for all other elements)

        // 2. Get the ViewModel instance
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // 3. Observe the LiveData for the formatted result
        viewModel.getCalorieResult().observe(this, result -> {
            tvResult.setText(result);
            btnDietPlan.setVisibility(View.VISIBLE);
        });

        // 4. Observe the LiveData for the raw calorie value
        viewModel.getRawCalorieValue().observe(this, calories -> {
            lastCalorieResult = calories;
        });

        // 5. Set click listeners for the buttons
        btnCalculate.setOnClickListener(v -> calculate());
        btnDietPlan.setOnClickListener(v -> showDietPlan());
    }

    private void calculate() {
        // ... (validation checks for all input fields and spinners)

        // Create the UserData model object from the inputs
        UserData userData = new UserData(name, age, weight, height, gender, activity, need);
        
        // Pass the data to the ViewModel to perform the calculation
        viewModel.calculateCalories(userData);
    }
    
    private void showDietPlan() {
        // ... (logic to choose a diet plan string based on lastCalorieResult)
        
        tvDietPlan.setText(dietPlan);
        tvDietPlan.setVisibility(View.VISIBLE);

        // Scroll down to make the new content visible
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
    // ... (helper methods to get diet plan strings)
}
```
**Line-by-line:**
1.  `findViewById()`: In `onCreate`, each UI element from the XML is linked to a class variable.
2.  `new ViewModelProvider(this).get(...)`: This gets the single, shared instance of our `MainViewModel`, ensuring data survives screen rotation.
3.  `viewModel.getCalorieResult().observe(...)`: This is the core reactive connection. The code inside this lambda (`result -> { ... }`) is executed automatically whenever the formatted result in the ViewModel changes. It updates the `tvResult` TextView and makes the diet plan button visible.
4.  `viewModel.getRawCalorieValue().observe(...)`: A second observer that saves the raw calorie number into a local variable (`lastCalorieResult`) to be used later by the diet plan logic.
5.  `btnCalculate.setOnClickListener(...)`: When the "Calculate" button is clicked, it calls the local `calculate()` method.
6.  `calculate()`: This method gathers all text from the `EditText` and `Spinner` fields, performs validation, packages it all into a `UserData` object, and sends it to the ViewModel via `viewModel.calculateCalories(userData)`.
7.  `showDietPlan()`: This method is called when the "View Diet Plan" button is clicked. It uses the stored `lastCalorieResult` to build the correct diet plan string, sets it to the `tvDietPlan` TextView, makes the TextView visible, and finally tells the `ScrollView` to scroll down so the user can see the new content.

---

This architecture ensures a clean separation of concerns, making the app robust, scalable, and easy to debug and maintain.
