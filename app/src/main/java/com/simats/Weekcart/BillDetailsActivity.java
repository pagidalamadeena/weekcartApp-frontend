package com.simats.Weekcart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.simats.Weekcart.models.Bill;
import com.simats.Weekcart.models.BillItem;
import com.simats.Weekcart.network.ApiInterface;
import com.simats.Weekcart.network.NetworkClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BillDetailsActivity extends AppCompatActivity {

    private TextView tvBillSummaryId, tvBillSummaryDate, tvBillSummaryTotal;
    private LinearLayout llBillItemsContainer;
    private int billId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_details);

        billId = getIntent().getIntExtra("BILL_ID", -1);
        if (billId == -1) {
            Toast.makeText(this, "Error: Bill ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvBillSummaryId = findViewById(R.id.tvBillSummaryId);
        tvBillSummaryDate = findViewById(R.id.tvBillSummaryDate);
        tvBillSummaryTotal = findViewById(R.id.tvBillSummaryTotal);
        llBillItemsContainer = findViewById(R.id.llBillItemsContainer);

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        fetchBillDetails();
    }

    private void fetchBillDetails() {
        ApiInterface apiService = NetworkClient.getClient(this).create(ApiInterface.class);
        apiService.getBill(billId).enqueue(new Callback<Bill>() {
            @Override
            public void onResponse(Call<Bill> call, Response<Bill> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayBillDetails(response.body());
                } else {
                    Toast.makeText(BillDetailsActivity.this, "Failed to load bill details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Bill> call, Throwable t) {
                Toast.makeText(BillDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayBillDetails(Bill bill) {
        tvBillSummaryId.setText("Bill #" + bill.getId());
        tvBillSummaryDate.setText("Date: " + (bill.getUploadedAt() != null ? bill.getUploadedAt() : "--"));
        tvBillSummaryTotal.setText("₹" + String.format("%.2f", bill.getTotalAmount()));

        llBillItemsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        if (bill.getItems() != null) {
            for (BillItem item : bill.getItems()) {
                View itemView = inflater.inflate(R.layout.item_bill_detail_row, llBillItemsContainer, false);
                
                TextView tvName = itemView.findViewById(R.id.tvItemName);
                TextView tvQty = itemView.findViewById(R.id.tvItemQuantity);
                TextView tvPrice = itemView.findViewById(R.id.tvItemPrice);

                tvName.setText(item.getItemName());
                tvQty.setText("qty: " + (int)item.getQuantity());
                tvPrice.setText("₹" + String.format("%.2f", item.getPrice()));

                llBillItemsContainer.addView(itemView);
            }
        }
    }
}
