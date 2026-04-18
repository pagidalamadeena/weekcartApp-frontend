package com.simats.weekcart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.simats.weekcart.models.AnalyticsResponse;
import com.simats.weekcart.network.ApiInterface;
import com.simats.weekcart.network.NetworkClient;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalysisFragment extends Fragment {

    private TextView tvThisWeekAmount, tvThisWeekBills, tvLastWeekAmount, tvLastWeekBills, tvChange, tvAvgBill;
    private BarChart barChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);

        view.findViewById(R.id.ivBack).setOnClickListener(v -> {
            if (getActivity() instanceof DashboardActivity) {
                ((DashboardActivity) getActivity()).switchToHome();
            }
        });

        tvThisWeekAmount = view.findViewById(R.id.tvTotalSpending);
        tvThisWeekBills = view.findViewById(R.id.tvThisWeekBills);
        tvLastWeekAmount = view.findViewById(R.id.tvLastWeekAmount);
        tvLastWeekBills = view.findViewById(R.id.tvLastWeekBills);
        tvChange = view.findViewById(R.id.tvChange);
        tvAvgBill = view.findViewById(R.id.tvAvgBill);
        barChart = view.findViewById(R.id.barChart);

        fetchAnalytics();

        return view;
    }

    private void fetchAnalytics() {
        ApiInterface apiService = NetworkClient.getClient(getContext()).create(ApiInterface.class);
        apiService.getWeeklySpending().enqueue(new Callback<AnalyticsResponse>() {
            @Override
            public void onResponse(Call<AnalyticsResponse> call, Response<AnalyticsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AnalyticsResponse stats = response.body();

                    tvThisWeekAmount.setText("₹" + String.format("%.2f", stats.getThisWeekTotal()));
                    tvThisWeekBills.setText(stats.getThisWeekCount() + " bills");

                    tvLastWeekAmount.setText("₹" + String.format("%.2f", stats.getLastWeekTotal()));
                    tvLastWeekBills.setText(stats.getLastWeekCount() + " bills");

                    float change = stats.getPercentageChange();
                    tvChange.setText((change >= 0 ? "+" : "") + String.format("%.1f", change) + "%");

                    if (change > 0) {
                        tvChange.setTextColor(getResources().getColor(R.color.heart_red));
                    } else if (change < 0) {
                        tvChange.setTextColor(getResources().getColor(R.color.icon_green));
                    }

                    tvAvgBill.setText("₹" + String.format("%.2f", stats.getAvgBill()));

                    setupChart((float) stats.getLastWeekTotal(), (float) stats.getThisWeekTotal());
                }
            }

            @Override
            public void onFailure(Call<AnalyticsResponse> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupChart(float lastWeekTotal, float thisWeekTotal) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, lastWeekTotal));
        entries.add(new BarEntry(1f, thisWeekTotal));

        BarDataSet dataSet = new BarDataSet(entries, "Weekly Spending");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setDrawGridBackground(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        ArrayList<String> labels = new ArrayList<>();
        labels.add("Last Week");
        labels.add("This Week");
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate(); // refresh
    }
}