package com.simats.weekcart;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.simats.weekcart.adapters.CategoryAdapter;
import com.simats.weekcart.models.CategoryItem;
import com.simats.weekcart.network.ApiInterface;
import com.simats.weekcart.network.NetworkClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryDistributionActivity extends AppCompatActivity {

    private PieChart pieChart;
    private RecyclerView rvDistribution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_distribution);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        pieChart = findViewById(R.id.pieChart);
        rvDistribution = findViewById(R.id.rvDistribution);

        fetchData();
    }

    private void fetchData() {
        ApiInterface api = NetworkClient.getClient(this).create(ApiInterface.class);
        api.getCategoryDistribution().enqueue(new Callback<List<CategoryItem>>() {
            @Override
            public void onResponse(Call<List<CategoryItem>> call, Response<List<CategoryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CategoryItem> data = response.body();
                    setupPieChart(data);
                    setupRecycler(data);
                } else {
                    Toast.makeText(CategoryDistributionActivity.this, "No data available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CategoryItem>> call, Throwable t) {
                Toast.makeText(CategoryDistributionActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void setupPieChart(List<CategoryItem> data) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (CategoryItem item : data) {
            entries.add(new PieEntry(item.getPercentage(), capitalize(item.getCategory())));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(11f);
        dataSet.setSliceSpace(2f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setCenterText("Items");
        pieChart.setCenterTextSize(14f);
        pieChart.getLegend().setEnabled(true);
        pieChart.animateY(900);
        pieChart.invalidate();
    }

    private void setupRecycler(List<CategoryItem> data) {
        CategoryAdapter adapter = new CategoryAdapter(data, false);
        rvDistribution.setAdapter(adapter);
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
