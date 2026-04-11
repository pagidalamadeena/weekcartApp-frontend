package com.simats.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.simats.frontend.models.User;
import com.simats.frontend.network.ApiInterface;
import com.simats.frontend.network.NetworkClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.RelativeLayout;
import android.widget.ProgressBar;
import java.util.List;

public class HomeFragment extends Fragment {
    private TextView tvWelcome, tvHealthScoreValue, tvHealthRecommendation;
    private ProgressBar pbHealth;
    private RecyclerView rvRecentBills;
    private BillsAdapter billsAdapter;
    private RelativeLayout llHealthScoreBox;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvWelcome = view.findViewById(R.id.tvWelcomeGreeting);
        tvHealthScoreValue = view.findViewById(R.id.tvHealthScoreValue);
        tvHealthRecommendation = view.findViewById(R.id.tvHealthRecommendation);
        pbHealth = view.findViewById(R.id.pbHealth);
        rvRecentBills = view.findViewById(R.id.rvRecentBills);
        llHealthScoreBox = view.findViewById(R.id.llHealthScoreBox);

        if (rvRecentBills != null) {
            rvRecentBills.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        }

        ImageView profileIcon = view.findViewById(R.id.ivProfileIcon);
        if (profileIcon != null) {
            profileIcon.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            });
        }

        if (llHealthScoreBox != null) {
            llHealthScoreBox.setOnClickListener(v -> {
                if (getActivity() instanceof DashboardActivity) {
                    ((DashboardActivity) getActivity()).switchToHealth();
                }
            });
        }

        View tvViewHealthInsights = view.findViewById(R.id.tvViewHealthInsights);
        if (tvViewHealthInsights != null) {
            tvViewHealthInsights.setOnClickListener(v -> {
                if (getActivity() instanceof DashboardActivity) {
                    ((DashboardActivity) getActivity()).switchToHealth();
                }
            });
        }

        fetchGreeting();
        fetchHealthScore();
        fetchRecentBills();

        View uploadBtn = view.findViewById(R.id.btnUploadBill);
        if (uploadBtn != null) {
            uploadBtn.setOnClickListener(v -> {
                if (getActivity() instanceof DashboardActivity) {
                    ((DashboardActivity) getActivity()).switchToUpload();
                }
            });
        }

        View manualBtn = view.findViewById(R.id.btnManualEntry);
        if (manualBtn != null) {
            manualBtn.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ManualEntryActivity.class);
                startActivity(intent);
            });
        }

        return view;
    }

    private void fetchGreeting() {
        ApiInterface apiService = NetworkClient.getClient(getContext()).create(ApiInterface.class);
        apiService.getProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvWelcome.setText("Welcome back, " + response.body().getFullName() + "!");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Ignore failure for greeting, keep static welcome
            }
        });
    }

    private void fetchHealthScore() {
        ApiInterface apiService = NetworkClient.getClient(getContext()).create(ApiInterface.class);
        apiService.getHealthScore().enqueue(new Callback<com.simats.frontend.models.HealthScoreResponse>() {
            @Override
            public void onResponse(Call<com.simats.frontend.models.HealthScoreResponse> call, Response<com.simats.frontend.models.HealthScoreResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int score = response.body().getScore();
                    if (tvHealthScoreValue != null) tvHealthScoreValue.setText(String.valueOf(score));
                    if (pbHealth != null) pbHealth.setProgress(score);

                    if (tvHealthRecommendation != null) {
                        if (score >= 80) {
                            tvHealthRecommendation.setText("Great job! Your diet is very healthy.");
                        } else if (score >= 60) {
                            tvHealthRecommendation.setText("Good score, but could be better with more greens.");
                        } else {
                            tvHealthRecommendation.setText("Consider adding more healthy foods to your diet.");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<com.simats.frontend.models.HealthScoreResponse> call, Throwable t) {
                if (tvHealthRecommendation != null) tvHealthRecommendation.setText("Failed to load health score.");
            }
        });
    }

    private void fetchRecentBills() {
        ApiInterface apiService = NetworkClient.getClient(getContext()).create(ApiInterface.class);
        apiService.getBills().enqueue(new Callback<List<com.simats.frontend.models.Bill>>() {
            @Override
            public void onResponse(Call<List<com.simats.frontend.models.Bill>> call, Response<List<com.simats.frontend.models.Bill>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<com.simats.frontend.models.Bill> bills = response.body();
                    // Show only top 5 for home screen
                    if (bills.size() > 5) {
                        bills = bills.subList(0, 5);
                    }
                    billsAdapter = new BillsAdapter(bills, bill -> {
                        Intent intent = new Intent(getActivity(), BillDetailsActivity.class);
                        intent.putExtra("BILL_ID", bill.getId());
                        startActivity(intent);
                    });
                    if (rvRecentBills != null) {
                        rvRecentBills.setAdapter(billsAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<com.simats.frontend.models.Bill>> call, Throwable t) {
                // Silently fail for history
            }
        });
    }
}