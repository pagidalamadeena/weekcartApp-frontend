package com.simats.frontend;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.simats.frontend.adapters.CategoryAdapter;
import com.simats.frontend.models.CategoryItem;
import com.simats.frontend.network.ApiInterface;
import com.simats.frontend.network.NetworkClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpendingByCategoryActivity extends AppCompatActivity {

    private HorizontalBarChart hBarChart;
    private RecyclerView rvCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spending_by_category);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        hBarChart = findViewById(R.id.hBarChart);
        rvCategories = findViewById(R.id.rvCategories);

        fetchData();
    }

    private void fetchData() {
        ApiInterface api = NetworkClient.getClient(this).create(ApiInterface.class);
        api.getSpendingByCategory().enqueue(new Callback<List<CategoryItem>>() {
            @Override
            public void onResponse(Call<List<CategoryItem>> call, Response<List<CategoryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CategoryItem> data = response.body();
                    setupChart(data);
                    setupRecycler(data);
                } else {
                    Toast.makeText(SpendingByCategoryActivity.this, "No data available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CategoryItem>> call, Throwable t) {
                Toast.makeText(SpendingByCategoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupChart(List<CategoryItem> data) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            entries.add(new BarEntry(i, data.get(i).getAmount()));
            labels.add(capitalize(data.get(i).getCategory()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Spending (₹)");
        
        ArrayList<Integer> colors = new ArrayList<>();
        for (CategoryItem item : data) {
            colors.add(item.getCategoryColor());
        }
        dataSet.setColors(colors);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        hBarChart.setData(barData);
        hBarChart.getDescription().setEnabled(false);
        hBarChart.getLegend().setEnabled(false);
        hBarChart.setDrawGridBackground(false);

        XAxis xAxis = hBarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(data.size());

        hBarChart.getAxisLeft().setDrawGridLines(false);
        hBarChart.getAxisRight().setEnabled(false);
        hBarChart.animateY(800);
        hBarChart.invalidate();
    }

    private void setupRecycler(List<CategoryItem> data) {
        CategoryAdapter adapter = new CategoryAdapter(data, true);
        rvCategories.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty())
            return "Other";
        return s.substring(0, 1).toUpperCase() + s.substring(1).replace("_", " ");
    }
}
