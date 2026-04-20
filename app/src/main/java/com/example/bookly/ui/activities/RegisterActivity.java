package com.example.bookly.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bookly.R;
import com.example.bookly.data.api.RetrofitClient;
import com.example.bookly.data.api.SharedPrefsManager;
import com.example.bookly.data.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etPassword, etRegion, etDistrict, etCity;
    private Button btnCreate;
    private ProgressBar progressBar;
    private SharedPrefsManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Реєстрація");
        }

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etRegion = findViewById(R.id.etRegion);
        etDistrict = findViewById(R.id.etDistrict);
        etCity = findViewById(R.id.etCity);
        btnCreate = findViewById(R.id.btnCreateAccount);
        progressBar = findViewById(R.id.progressBar);

        prefs = SharedPrefsManager.getInstance(this);

        btnCreate.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String region = etRegion.getText().toString().trim();
        String district = etDistrict.getText().toString().trim();
        String city = etCity.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()
                || region.isEmpty() || district.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Заповни всі поля", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnCreate.setEnabled(false);

        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        RetrofitClient.getInstance()
                .getApiService()
                .signUp(RetrofitClient.API_KEY, "application/json", body)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        if (response.isSuccessful()) {
                            User user = new User();
                            user.setName(name);
                            user.setEmail(email);
                            user.setPhone(phone);
                            user.setPassword(password);
                            user.setRegion(region);
                            user.setDistrict(district);
                            user.setCity(city);
                            user.setRole("user");

                            RetrofitClient.getInstance()
                                    .getApiService()
                                    .createUser(
                                            RetrofitClient.API_KEY,
                                            "Bearer " + RetrofitClient.API_KEY,
                                            "return=representation",
                                            user
                                    )
                                    .enqueue(new Callback<List<User>>() {
                                        @Override
                                        public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                                            progressBar.setVisibility(View.GONE);

                                            if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                                                User createdUser = response.body().get(0);

                                                prefs.saveSession(
                                                        createdUser.getId(),
                                                        createdUser.getName(),
                                                        createdUser.getEmail(),
                                                        createdUser.getRole(),
                                                        createdUser.getCity() != null ? createdUser.getCity() : ""
                                                );
                                                prefs.saveToken(RetrofitClient.API_KEY);

                                                Toast.makeText(RegisterActivity.this, "Акаунт створено", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            } else {
                                                btnCreate.setEnabled(true);
                                                Toast.makeText(RegisterActivity.this, "Помилка створення профілю", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<List<User>> call, Throwable t) {
                                            progressBar.setVisibility(View.GONE);
                                            btnCreate.setEnabled(true);
                                            Toast.makeText(RegisterActivity.this, "Помилка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            progressBar.setVisibility(View.GONE);
                            btnCreate.setEnabled(true);
                            Toast.makeText(RegisterActivity.this, "Помилка реєстрації: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        btnCreate.setEnabled(true);
                        Toast.makeText(RegisterActivity.this, "Помилка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}