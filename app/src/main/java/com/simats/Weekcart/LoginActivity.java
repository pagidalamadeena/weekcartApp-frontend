package com.simats.Weekcart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.EditText;
import android.widget.Toast;
import com.simats.Weekcart.models.LoginRequest;
import com.simats.Weekcart.models.LoginResponse;
import com.simats.Weekcart.network.ApiInterface;
import com.simats.Weekcart.network.NetworkClient;
import com.simats.Weekcart.network.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Standard window insets handling for edge-to-edge
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content),
                (v, windowInsets) -> {
                    androidx.core.graphics.Insets systemBars = windowInsets
                            .getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return windowInsets;
                });

        tokenManager = new TokenManager(this);

        // Redirect if already logged in - but validate session first
        if (tokenManager.getToken() != null) {
            validateSessionAndRedirect();
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        TextView tvSignUp = findViewById(R.id.tvSignUp);
        Button btnLogin = findViewById(R.id.btnLogin);

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            performLogin(email, password);
        });
    }

    private void validateSessionAndRedirect() {
        ApiInterface apiService = NetworkClient.getClient(this).create(ApiInterface.class);
        apiService.getProfile().enqueue(new Callback<com.simats.Weekcart.models.User>() {
            @Override
            public void onResponse(Call<com.simats.Weekcart.models.User> call, Response<com.simats.Weekcart.models.User> response) {
                if (response.isSuccessful()) {
                    // Valid token, proceed to subscription page
                    startActivity(new Intent(LoginActivity.this, SubscriptionActivity.class));
                    finish();
                } else {
                    // Invalid token, clear it and stay on login
                    tokenManager.clearToken();
                    Toast.makeText(LoginActivity.this, "Session expired, please login again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.simats.Weekcart.models.User> call, Throwable t) {
                // Network error or server down
                Toast.makeText(LoginActivity.this, "Connection Error: Please check if backend is running", Toast.LENGTH_LONG).show();
                // Stay on login page so user can see what's wrong
            }
        });
    }

    private void performLogin(String email, String password) {
        ApiInterface apiService = NetworkClient.getClient(this).create(ApiInterface.class);
        Call<LoginResponse> call = apiService.login(new LoginRequest(email, password));

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tokenManager.saveToken(response.body().getAccessToken());
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, SubscriptionActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Connection Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
