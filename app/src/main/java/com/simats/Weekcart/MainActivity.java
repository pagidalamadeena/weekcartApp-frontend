package com.simats.Weekcart;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.simats.Weekcart.utils.ThemeManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_main);

        ImageView cartLogo = findViewById(R.id.ivCartLogo);
        Button btnGetStarted = findViewById(R.id.btnGetStarted);

        // Apply bounce animation to the cart logo
        Animation bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        cartLogo.startAnimation(bounceAnimation);

        // Navigate to Login Activity
        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
