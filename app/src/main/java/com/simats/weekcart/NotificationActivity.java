package com.simats.weekcart;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.simats.weekcart.models.Notification;
import com.simats.weekcart.network.ApiInterface;
import com.simats.weekcart.network.NetworkClient;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    // I'll need a NotificationsAdapter, but for now I can reuse a simple one or
    // create it.
    // Given the time, I'll create a simple inner class or helper.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Handle Status Bar Overlap
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content),
                (v, insets) -> {
                    int statusBarHeight = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.statusBars()).top;
                    v.setPadding(0, statusBarHeight, 0, 0);
                    return insets;
                });

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.rvNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchNotifications();
    }

    private void fetchNotifications() {
        ApiInterface apiService = NetworkClient.getClient(this).create(ApiInterface.class);
        apiService.getNotifications().enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (com.simats.weekcart.adapters.NotificationAdapter.class != null) {
                        com.simats.weekcart.adapters.NotificationAdapter adapter = new com.simats.weekcart.adapters.NotificationAdapter(
                                response.body());
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Toast.makeText(NotificationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
