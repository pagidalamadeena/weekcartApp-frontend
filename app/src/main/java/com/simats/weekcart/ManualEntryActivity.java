package com.simats.weekcart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.simats.weekcart.models.Bill;
import com.simats.weekcart.models.ManualBillRequest;
import com.simats.weekcart.network.ApiInterface;
import com.simats.weekcart.network.NetworkClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManualEntryActivity extends AppCompatActivity {

    private LinearLayout llItemsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        llItemsContainer = findViewById(R.id.llItemsContainer);

        findViewById(R.id.btnAddItem).setOnClickListener(v -> addNewItemRow());
        findViewById(R.id.btnSaveBill).setOnClickListener(v -> saveBill());

        // Add first item by default
        addNewItemRow();
    }

    private void addNewItemRow() {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_bill_edit, llItemsContainer, false);

        // Setup remove button
        View btnRemove = itemView.findViewById(R.id.btnRemoveItem);
        btnRemove.setVisibility(View.VISIBLE);
        btnRemove.setOnClickListener(v -> {
            if (llItemsContainer.getChildCount() > 1) {
                llItemsContainer.removeView(itemView);
            } else {
                Toast.makeText(this, "At least one item is required", Toast.LENGTH_SHORT).show();
            }
        });

        llItemsContainer.addView(itemView);
    }

    private void saveBill() {
        List<ManualBillRequest.Item> items = new ArrayList<>();

        for (int i = 0; i < llItemsContainer.getChildCount(); i++) {
            View itemView = llItemsContainer.getChildAt(i);
            EditText etName = itemView.findViewById(R.id.etItemName);
            EditText etPrice = itemView.findViewById(R.id.etItemPrice);
            EditText etQuantity = itemView.findViewById(R.id.etItemQuantity);

            String name = etName.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String qtyStr = etQuantity.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Please fill name and price for all items", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            double qty = qtyStr.isEmpty() ? 1.0 : Double.parseDouble(qtyStr);

            items.add(new ManualBillRequest.Item(name, price, qty));
        }

        if (items.isEmpty())
            return;

        ApiInterface api = NetworkClient.getClient(this).create(ApiInterface.class);
        api.saveManualBill(new ManualBillRequest(items)).enqueue(new Callback<Bill>() {
            @Override
            public void onResponse(Call<Bill> call, Response<Bill> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManualEntryActivity.this, "Bill saved successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ManualEntryActivity.this, "Failed to save bill", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Bill> call, Throwable t) {
                Toast.makeText(ManualEntryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
