package com.simats.Weekcart;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.simats.Weekcart.models.WeeklyHealthComparison;
import com.simats.Weekcart.network.ApiInterface;
import com.simats.Weekcart.network.NetworkClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeeklyHealthComparisonActivity extends AppCompatActivity {

    private BarChart barChart;
    private TextView tvTrend, tvThisWeekScore, tvLastWeekScore, tvThisWeekStats, tvLastWeekStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_health_comparison);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        barChart = findViewById(R.id.barChart);
        tvTrend = findViewById(R.id.tvTrend);
        tvThisWeekScore = findViewById(R.id.tvThisWeekScore);
        tvLastWeekScore = findViewById(R.id.tvLastWeekScore);
        tvThisWeekStats = findViewById(R.id.tvThisWeekStats);
        tvLastWeekStats = findViewById(R.id.tvLastWeekStats);

        fetchData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void fetchData() {
        ApiInterface api = NetworkClient.getClient(this).create(ApiInterface.class);
        api.getWeeklyHealthComparison().enqueue(new Callback<WeeklyHealthComparison>() {
            @Override
            public void onResponse(Call<WeeklyHealthComparison> call, Response<WeeklyHealthComparison> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeeklyHealthComparison data = response.body();
                    populateUI(data);
                } else {
                    Toast.makeText(WeeklyHealthComparisonActivity.this, "No data available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeeklyHealthComparison> call, Throwable t) {
                Toast.makeText(WeeklyHealthComparisonActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void populateUI(WeeklyHealthComparison data) {
        WeeklyHealthComparison.WeekData tw = data.getThisWeek();
        WeeklyHealthComparison.WeekData lw = data.getLastWeek();

        // Trend text
        tvTrend.setText(data.getTrend());
        int trendColor = data.getScoreDiff() >= 0
                ? getResources().getColor(R.color.icon_green)
                : getResources().getColor(R.color.heart_red);
        tvTrend.setTextColor(trendColor);

        // Stat cards
        tvThisWeekScore.setText(String.valueOf(tw.getScore()));
        tvLastWeekScore.setText(String.valueOf(lw.getScore()));
        tvThisWeekStats.setText("✅ " + tw.getHealthyCount() + " healthy  ❌ " + tw.getUnhealthyCount() + " unhealthy");
        tvLastWeekStats.setText("✅ " + lw.getHealthyCount() + " healthy  ❌ " + lw.getUnhealthyCount() + " unhealthy");

        // Bar chart
        ArrayList<BarEntry> thisWeekEntries = new ArrayList<>();
        thisWeekEntries.add(new BarEntry(0f, tw.getScore()));

        ArrayList<BarEntry> lastWeekEntries = new ArrayList<>();
        lastWeekEntries.add(new BarEntry(1f, lw.getScore()));

        BarDataSet thisDataSet = new BarDataSet(thisWeekEntries, "This Week");
        thisDataSet.setColor(getResources().getColor(R.color.icon_green));
        thisDataSet.setValueTextSize(12f);

        BarDataSet lastDataSet = new BarDataSet(lastWeekEntries, "Last Week");
        lastDataSet.setColor(getResources().getColor(R.color.analysis_blue));
        lastDataSet.setValueTextSize(12f);

        BarData barData = new BarData(thisDataSet, lastDataSet);
        barData.setBarWidth(0.35f);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.groupBars(-0.5f, 0.4f, 0.05f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[] { "This Week", "Last Week" }));
        xAxis.setCenterAxisLabels(true);

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisLeft().setAxisMaximum(100f);
        barChart.getAxisRight().setEnabled(false);
        barChart.animateY(900);
        barChart.invalidate();
    }
}
