package com.simats.weekcart;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.simats.weekcart.adapters.GroceryAdapter;
import com.simats.weekcart.models.GroceryItem;
import com.simats.weekcart.network.ApiInterface;
import com.simats.weekcart.network.NetworkClient;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFragment extends Fragment {

    private RecyclerView rvGroceryItems;
    private GroceryAdapter groceryAdapter;
    private List<GroceryItem> groceryList = new ArrayList<>();

    private EditText etNewItem;
    private android.widget.Button btnAddItem;
    private ImageButton btnSetReminder;
    private android.widget.TextView tvToBuyCount, tvCompletedCount;

    private Calendar reminderCalendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        view.findViewById(R.id.ivBack).setOnClickListener(v -> {
            if (getActivity() instanceof DashboardActivity) {
                ((DashboardActivity) getActivity()).switchToHome();
            }
        });

        rvGroceryItems = view.findViewById(R.id.rvGroceryItems);
        etNewItem = view.findViewById(R.id.etNewItem);
        btnAddItem = view.findViewById(R.id.btnAddItem);
        btnSetReminder = view.findViewById(R.id.btnSetReminder);
        tvToBuyCount = view.findViewById(R.id.tvToBuyCount);
        tvCompletedCount = view.findViewById(R.id.tvCompletedCount);

        if (rvGroceryItems != null) {
            rvGroceryItems.setLayoutManager(new LinearLayoutManager(getContext()));
            groceryAdapter = new GroceryAdapter(groceryList, getContext(), this::updateCounts);
            rvGroceryItems.setAdapter(groceryAdapter);
            fetchGroceries();
        }

        if (btnAddItem != null) {
            btnAddItem.setOnClickListener(v -> addGroceryItem());
        }

        if (btnSetReminder != null) {
            btnSetReminder.setOnClickListener(v -> showDateTimePicker());
        }

        return view;
    }

    private void showDateTimePicker() {
        final Calendar current = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            reminderCalendar.set(Calendar.YEAR, year);
            reminderCalendar.set(Calendar.MONTH, month);
            reminderCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            new TimePickerDialog(getContext(), (view1, hourOfDay, minute) -> {
                reminderCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                reminderCalendar.set(Calendar.MINUTE, minute);
                reminderCalendar.set(Calendar.SECOND, 0);

                if (reminderCalendar.before(Calendar.getInstance())) {
                    Toast.makeText(getContext(), "Please select a future time", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1002);
                        return;
                    }
                }

                setReminder();
            }, current.get(Calendar.HOUR_OF_DAY), current.get(Calendar.MINUTE), false).show();
        }, current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setReminder() {
        String itemName = etNewItem.getText().toString().trim();
        if (itemName.isEmpty()) {
            Toast.makeText(getContext(), "Enter an item name first", Toast.LENGTH_SHORT).show();
            return;
        }

        String isoDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                .format(reminderCalendar.getTime());

        // 1. Backend Sync
        ApiInterface apiService = NetworkClient.getClient(getContext()).create(ApiInterface.class);
        Map<String, String> body = new HashMap<>();
        body.put("message", "Time to buy: " + itemName);
        body.put("reminder_date", isoDate);

        apiService.createReminder(body).enqueue(new Callback<com.simats.weekcart.models.ReminderItem>() {
            @Override
            public void onResponse(Call<com.simats.weekcart.models.ReminderItem> call,
                    Response<com.simats.weekcart.models.ReminderItem> response) {
                if (response.isSuccessful()) {
                    // 2. Schedule Local Alarm
                    scheduleLocalNotification(itemName, reminderCalendar.getTimeInMillis());
                    Toast.makeText(getContext(), "Reminder set for " + itemName, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to sync reminder to cloud", Toast.LENGTH_SHORT).show();
                    // Still schedule local if cloud fails? Maybe safer.
                    scheduleLocalNotification(itemName, reminderCalendar.getTimeInMillis());
                }
            }

            @Override
            public void onFailure(Call<com.simats.weekcart.models.ReminderItem> call, Throwable t) {
                Toast.makeText(getContext(), "Offline: Setting local reminder only", Toast.LENGTH_SHORT).show();
                scheduleLocalNotification(itemName, reminderCalendar.getTimeInMillis());
            }
        });
    }

    private void scheduleLocalNotification(String itemName, long timeInMillis) {
        Context context = getContext();
        if (context == null) return;

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("MESSAGE", "Time to buy: " + itemName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)timeInMillis, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                } else {
                    // Fallback to inexact if permission not granted
                    alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                    Toast.makeText(context, "Reminder set (may be slightly delayed due to system optimization)", Toast.LENGTH_SHORT).show();
                    
                    // Optionally direct user to settings
                    Intent intentSettings = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    context.startActivity(intentSettings);
                }
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            }
        }
    }

    private void updateCounts() {
        int toBuy = 0;
        int completed = 0;
        for (GroceryItem item : groceryList) {
            if (item.isPurchased()) {
                completed++;
            } else {
                toBuy++;
            }
        }
        if (tvToBuyCount != null)
            tvToBuyCount.setText(String.valueOf(toBuy));
        if (tvCompletedCount != null)
            tvCompletedCount.setText(String.valueOf(completed));
    }

    private void fetchGroceries() {
        ApiInterface apiService = NetworkClient.getClient(getContext()).create(ApiInterface.class);
        apiService.getGroceryList().enqueue(new Callback<List<GroceryItem>>() {
            @Override
            public void onResponse(Call<List<GroceryItem>> call, Response<List<GroceryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    groceryList.clear();
                    groceryList.addAll(response.body());
                    groceryAdapter.notifyDataSetChanged();
                    updateCounts();
                }
            }

            @Override
            public void onFailure(Call<List<GroceryItem>> call, Throwable t) {
            }
        });
    }

    private void addGroceryItem() {
        String itemName = etNewItem.getText().toString().trim();
        if (itemName.isEmpty())
            return;

        ApiInterface apiService = NetworkClient.getClient(getContext()).create(ApiInterface.class);
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("item_name", itemName);

        apiService.addGroceryItem(body).enqueue(new Callback<GroceryItem>() {
            @Override
            public void onResponse(Call<GroceryItem> call, Response<GroceryItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    groceryList.add(0, response.body());
                    groceryAdapter.notifyItemInserted(0);
                    if (rvGroceryItems != null) rvGroceryItems.scrollToPosition(0);
                    etNewItem.setText("");
                    updateCounts();
                } else {
                    if (getContext() != null) {
                        android.widget.Toast
                                .makeText(getContext(), "Failed to add item", android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GroceryItem> call, Throwable t) {
                if (getContext() != null) {
                    android.widget.Toast
                            .makeText(getContext(), "Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }
}