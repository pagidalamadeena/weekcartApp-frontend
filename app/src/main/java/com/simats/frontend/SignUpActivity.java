package com.simats.frontend;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.EditText;
import android.widget.Toast;
import com.simats.frontend.models.RegisterRequest;
import com.simats.frontend.network.ApiInterface;
import com.simats.frontend.network.NetworkClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText etFullName, etPhone, etEmail, etPassword, etAdultCount, etChildCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Standard window insets handling for edge-to-edge
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            androidx.core.graphics.Insets systemBars = windowInsets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return windowInsets;
        });

        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etAdultCount = findViewById(R.id.etAdultCount);
        etChildCount = findViewById(R.id.etChildCount);

        TextView tvLogin = findViewById(R.id.tvLogin);
        Button btnSignUp = findViewById(R.id.btnSignUp);

        tvLogin.setOnClickListener(v -> {
            // Returns to Login Activity
            finish();
        });

        btnSignUp.setOnClickListener(v -> {
            performSignUp();
        });
    }

    private void performSignUp() {
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String adultCountStr = etAdultCount.getText().toString().trim();
        String childCountStr = etChildCount.getText().toString().trim();

        if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || adultCountStr.isEmpty()
                || childCountStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int adultCount = Integer.parseInt(adultCountStr);
        int childCount = Integer.parseInt(childCountStr);

        RegisterRequest request = new RegisterRequest(fullName, phone, email, password, adultCount, childCount);
        ApiInterface apiService = NetworkClient.getClient(this).create(ApiInterface.class);

        apiService.register(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Registration Successful. Please Login.", Toast.LENGTH_LONG)
                            .show();
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "Registration failed: " + response.code(), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
