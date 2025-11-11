package com.example.macromind.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.macromind.R;
import com.example.macromind.model.UserData;
import com.example.macromind.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private EditText etName, etAge, etWeight, etHeight;
    private Spinner spGender, spActivity, spNeed;
    private Button btnCalculate, btnDietPlan;
    private TextView tvResult, tvDietPlan;
    private ScrollView scrollView;
    private Double lastCalorieResult = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Linking UI elements
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        spGender = findViewById(R.id.spGender);
        spActivity = findViewById(R.id.spActivity);
        spNeed = findViewById(R.id.spNeed);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvResult = findViewById(R.id.tvResult);
        btnDietPlan = findViewById(R.id.btnDietPlan);
        tvDietPlan = findViewById(R.id.tvDietPlan);
        scrollView = findViewById(R.id.scrollView);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Observe LiveData
        viewModel.getCalorieResult().observe(this, result -> {
            tvResult.setText(result);
            btnDietPlan.setVisibility(View.VISIBLE);
            tvDietPlan.setVisibility(View.GONE); // Hide previous plan on new calculation
        });

        viewModel.getRawCalorieValue().observe(this, calories -> {
            lastCalorieResult = calories;
        });

        btnCalculate.setOnClickListener(v -> calculate());

        btnDietPlan.setOnClickListener(v -> showDietPlan());
    }

    private void calculate() {
        if (spGender.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spActivity.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a physical activity level", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spNeed.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a goal", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get input values
        String name = etName.getText().toString().trim();
        String gender = spGender.getSelectedItem().toString();
        String activity = spActivity.getSelectedItem().toString();
        String need = spNeed.getSelectedItem().toString();

        if (name.isEmpty() || etAge.getText().toString().isEmpty() ||
                etWeight.getText().toString().isEmpty() ||
                etHeight.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(etAge.getText().toString());
        double weight = Double.parseDouble(etWeight.getText().toString());
        double height = Double.parseDouble(etHeight.getText().toString());

        UserData userData = new UserData(name, age, weight, height, gender, activity, need);
        viewModel.calculateCalories(userData);
    }

    private void showDietPlan() {
        String dietPlan;
        if (lastCalorieResult < 1500) {
            dietPlan = getLowCalorieDietPlan();
        } else if (lastCalorieResult >= 1500 && lastCalorieResult <= 2200) {
            dietPlan = getMediumCalorieDietPlan();
        } else {
            dietPlan = getHighCalorieDietPlan();
        }
        tvDietPlan.setText(dietPlan);
        tvDietPlan.setVisibility(View.VISIBLE);

        // Scroll to the bottom to make the diet plan visible
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private String getLowCalorieDietPlan() {
        return "Diet Plan for Weight Loss (Under 1500 Calories):\n\n"
                + "🌅 Breakfast (8:00–9:00 AM)\n"
                + "1 bowl oatmeal with fruits and nuts\n"
                + "🧠 Nutrients: High in fiber and good carbs for sustained energy.\n\n"
                + "--------------------\n\n"
                + "🍛 Lunch (12:00–1:00 PM)\n"
                + "1 cup mixed vegetables (steamed or sautéed) + 1 small bowl salad + 1 cup curd/yogurt\n"
                + "🧠 Nutrients: Vitamins, fiber, and probiotics for gut health.\n\n"
                + "--------------------\n\n"
                + "🍲 Dinner (6:30–8:30 PM)\n"
                + "Soup + salad + whole grain bread\n"
                + "🧠 Keep dinner light to ease digestion.";
    }

    private String getMediumCalorieDietPlan() {
        return "Balanced Diet Plan (1500–2200 Calories):\n\n"
                + "🌅 Breakfast (8:00–9:00 AM)\n"
                + "Options: 2 boiled or scrambled eggs, 1–2 slices whole wheat toast, 1 cup milk or green tea OR 2 vegetable parathas (less oil) + curd\n"
                + "🧠 Nutrients: High in protein, fiber, and good carbs.\n\n"
                + "--------------------\n\n"
                + "🍛 Lunch (12:00–1:00 PM)\n"
                + "Balanced Plate: 1 cup brown rice or 2 chapatis, 1 cup dal (lentils), 1 cup mixed vegetables, 1 small bowl salad, 1 cup curd/yogurt\n"
                + "🧠 Nutrients: A mix of protein, carbs, fiber, vitamins, and probiotics.\n\n"
                + "--------------------\n\n"
                + "🍲 Dinner (6:30–8:30 PM)\n"
                + "Options: 2 chapatis + 1 cup dal + vegetable curry OR Grilled chicken/fish/tofu + stir-fried veggies\n"
                + "🧠 A satisfying meal that's not too heavy on carbs.";
    }

    private String getHighCalorieDietPlan() {
        return "Diet Plan for Weight Gain (Over 2200 Calories):\n\n"
                + "🌅 Breakfast (8:00–9:00 AM)\n"
                + "2 vegetable parathas (made with ghee) + curd + a handful of nuts\n"
                + "🧠 Nutrients: Energy-dense with healthy fats and complex carbs.\n\n"
                + "--------------------\n\n"
                + "🍛 Lunch (12:00–1:00 PM)\n"
                + "Balanced Plate: 1.5 cups brown rice or 3 chapatis, 1 large cup dal or grilled chicken/fish, 1 cup mixed vegetables, 1 small bowl salad, 1 cup full-fat yogurt\n"
                + "🧠 Nutrients: Increased portions of protein and carbs for muscle growth.\n\n"
                + "--------------------\n\n"
                + "🍲 Dinner (6:30–8:30 PM)\n"
                + "3 chapatis + 1 cup dal + vegetable curry + a side of paneer/tofu\n"
                + "🧠 A hearty, protein-rich meal to support muscle repair and growth overnight.";
    }
}
