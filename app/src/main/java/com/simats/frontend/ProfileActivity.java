package com.simats.frontend;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.simats.frontend.models.User;
import com.simats.frontend.network.ApiInterface;
import com.simats.frontend.network.NetworkClient;
import com.simats.frontend.network.TokenManager;
import com.simats.frontend.utils.ThemeManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvEmail, tvPhone, tvAdults, tvChildren, tvTotalFamily;
    private ImageView btnEditFamily;
    private SwitchMaterial switchNotifications;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Handle Status Bar Overlap
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content),
                (v, insets) -> {
                    int statusBarHeight = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.statusBars()).top;
                    v.setPadding(0, statusBarHeight, 0, 0);
                    return insets;
                });

        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAdults = findViewById(R.id.tvAdults);
        tvChildren = findViewById(R.id.tvChildren);
        tvTotalFamily = findViewById(R.id.tvTotalFamily);
        btnEditFamily = findViewById(R.id.btnEditFamily);
        switchNotifications = findViewById(R.id.switchNotifications);

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            new TokenManager(this).clearToken();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.btnDeleteAccount).setOnClickListener(v -> showDeleteAccountConfirmation());

        btnEditFamily.setOnClickListener(v -> showEditFamilyDialog());

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (currentUser != null && currentUser.isNotificationEnabled() != isChecked) {
                updateProfilePreference(isChecked);
            }
        });

        fetchProfile();
    }

    private void fetchProfile() {
        ApiInterface apiService = NetworkClient.getClient(this).create(ApiInterface.class);
        apiService.getProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    updateUIWithUser(currentUser);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIWithUser(User user) {
        tvUsername.setText(user.getUsername());
        tvEmail.setText(user.getEmail());
        tvPhone.setText(user.getPhoneNumber());
        tvAdults.setText(String.valueOf(user.getAdultCount()));
        tvChildren.setText(String.valueOf(user.getChildCount()));
        tvTotalFamily.setText(String.valueOf(user.getAdultCount() + user.getChildCount()));

        // Temporarily remove listeners to avoid triggering updates while setting
        // checked state
        switchNotifications.setOnCheckedChangeListener(null);

        switchNotifications.setChecked(user.isNotificationEnabled());

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (currentUser != null && currentUser.isNotificationEnabled() != isChecked) {
                updateProfilePreference(isChecked);
            }
        });
    }

    private void showEditFamilyDialog() {
        if (currentUser == null)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Family Details");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText etAdults = new EditText(this);
        etAdults.setHint("Number of Adults");
        etAdults.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etAdults.setText(String.valueOf(currentUser.getAdultCount()));
        layout.addView(etAdults);

        final EditText etChildren = new EditText(this);
        etChildren.setHint("Number of Children");
        etChildren.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etChildren.setText(String.valueOf(currentUser.getChildCount()));
        layout.addView(etChildren);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            try {
                int adults = Integer.parseInt(etAdults.getText().toString());
                int children = Integer.parseInt(etChildren.getText().toString());

                User updateRequest = new User();
                updateRequest.setAdultCount(adults);
                updateRequest.setChildCount(children);
                // Also send current notification preferences to maintain state
                updateRequest.setNotificationEnabled(switchNotifications.isChecked());

                updateProfile(updateRequest);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showDeleteAccountConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action is permanent and cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteAccount())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccount() {
        // Returned to Mock version: Clear session locally
        new TokenManager(this).clearToken();
        Toast.makeText(this, "Account Deleted Successfully", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void updateProfilePreference(boolean pushEnabled) {
        User updateRequest = new User();
        // Just send preferences, backend supports partial updates
        updateRequest.setNotificationEnabled(pushEnabled);
        updateProfile(updateRequest);
    }

    private void updateProfile(User updateRequest) {
        ApiInterface apiService = NetworkClient.getClient(this).create(ApiInterface.class);
        apiService.updateProfile(updateRequest).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    updateUIWithUser(currentUser);
                    Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                    // Revert UI to current user state on failure
                    if (currentUser != null) {
                        updateUIWithUser(currentUser);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                if (currentUser != null) {
                    updateUIWithUser(currentUser);
                }
            }
        });
    }
}
