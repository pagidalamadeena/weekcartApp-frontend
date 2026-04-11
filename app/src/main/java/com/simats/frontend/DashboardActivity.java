package com.simats.frontend;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DashboardActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Handle Status Bar Overlap (Edge-to-Edge)
        View mainView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;

            // Set padding to the fragment container so it doesn't go under status bar
            findViewById(R.id.fragment_container).setPadding(0, statusBarHeight, 0, 0);
            // Set padding to bottom nav so it doesn't go under navigation bar
            findViewById(R.id.bottom_navigation).setPadding(0, 0, 0, navBarHeight);

            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_upload) {
                // Bottom nav opens the initial upload state
                selectedFragment = UploadFragment.newInstance(false);
            } else if (itemId == R.id.nav_list) {
                selectedFragment = new ListFragment();
            } else if (itemId == R.id.nav_health) {
                selectedFragment = new HealthFragment();
            } else if (itemId == R.id.nav_analysis) {
                selectedFragment = new AnalysisFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });

        // Set default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void loadFragment(Fragment fragment) {
        // Always show bottom navigation for main tabs
        bottomNavigationView.setVisibility(View.VISIBLE);

        boolean isHome = fragment instanceof HomeFragment;

        // If we're going home, clear the entire backstack
        if (isHome) {
            getSupportFragmentManager().popBackStack(null,
                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragment);

        if (!isHome) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    public void switchToHome() {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    public void switchToUpload() {
        // From Home card, open the preview/selected image state
        loadFragment(UploadFragment.newInstance(true));
        // Manually update bottom nav selection without triggering listener again
        bottomNavigationView.getMenu().findItem(R.id.nav_upload).setChecked(true);
    }

    public void switchToHealth() {
        bottomNavigationView.setSelectedItemId(R.id.nav_health);
    }

    public void switchToList() {
        bottomNavigationView.setSelectedItemId(R.id.nav_list);
    }
}