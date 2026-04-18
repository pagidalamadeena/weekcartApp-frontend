package com.simats.weekcart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.simats.weekcart.models.HealthScoreResponse;
import com.simats.weekcart.network.ApiInterface;
import com.simats.weekcart.network.NetworkClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HealthFragment extends Fragment {

    private TextView tvScore, tvHealthLabel, tvHealthyPercent, tvUnhealthyPercent;
    private ProgressBar pbHealthScore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health, container, false);

        view.findViewById(R.id.ivBack).setOnClickListener(v -> {
            if (getActivity() instanceof DashboardActivity) {
                ((DashboardActivity) getActivity()).switchToHome();
            }
        });

        tvScore = view.findViewById(R.id.tvScore);
        tvHealthLabel = view.findViewById(R.id.tvHealthLabel);
        tvHealthyPercent = view.findViewById(R.id.tvHealthyPercent);
        tvUnhealthyPercent = view.findViewById(R.id.tvUnhealthyPercent);
        pbHealthScore = view.findViewById(R.id.pbHealthScore);

        // 3 navigation cards
        view.findViewById(R.id.cardSpendingByCategory)
                .setOnClickListener(v -> startActivity(new Intent(getContext(), SpendingByCategoryActivity.class)));

        view.findViewById(R.id.cardCategoryDistribution)
                .setOnClickListener(v -> startActivity(new Intent(getContext(), CategoryDistributionActivity.class)));

        view.findViewById(R.id.cardWeekComparison)
                .setOnClickListener(v -> startActivity(new Intent(getContext(), WeeklyHealthComparisonActivity.class)));

        fetchHealthScore();

        return view;
    }

    private void fetchHealthScore() {
        ApiInterface api = NetworkClient.getClient(getContext()).create(ApiInterface.class);
        api.getHealthScore().enqueue(new Callback<HealthScoreResponse>() {
            @Override
            public void onResponse(Call<HealthScoreResponse> call, Response<HealthScoreResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HealthScoreResponse data = response.body();

                    tvScore.setText(String.valueOf(data.getScore()));
                    pbHealthScore.setProgress(data.getScore());
                    tvHealthLabel.setText(data.getLabel());
                    tvHealthyPercent.setText(data.getHealthyPercent() + "%");
                    tvUnhealthyPercent.setText(data.getUnhealthyPercent() + "%");

                    // Color score by value
                    int scoreColor;
                    if (data.getScore() >= 75) {
                        scoreColor = getResources().getColor(R.color.icon_green);
                    } else if (data.getScore() >= 50) {
                        scoreColor = getResources().getColor(R.color.analysis_blue);
                    } else {
                        scoreColor = getResources().getColor(R.color.heart_red);
                    }
                    tvScore.setTextColor(scoreColor);
                }
            }

            @Override
            public void onFailure(Call<HealthScoreResponse> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error loading health data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}