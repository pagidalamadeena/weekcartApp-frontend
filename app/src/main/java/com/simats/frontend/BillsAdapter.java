package com.simats.frontend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.simats.frontend.models.Bill;
import java.util.List;

public class BillsAdapter extends RecyclerView.Adapter<BillsAdapter.BillViewHolder> {

    private List<Bill> bills;
    private OnBillClickListener listener;

    public interface OnBillClickListener {
        void onBillClick(Bill bill);
    }

    public BillsAdapter(List<Bill> bills) {
        this.bills = bills;
    }

    public BillsAdapter(List<Bill> bills, OnBillClickListener listener) {
        this.bills = bills;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = bills.get(position);
        holder.tvBillId.setText("Order #" + bill.getId());
        holder.tvBillAmount.setText("₹" + String.format("%,.2f", bill.getTotalAmount()));
        
        String status = bill.getStatus() != null ? bill.getStatus().toUpperCase() : "PAID";
        holder.tvBillStatus.setText(status);
        
        if (status.contains("PENDING")) {
            holder.tvBillStatus.setTextColor(android.graphics.Color.parseColor("#F59E0B"));
        } else if (status.contains("CANCEL")) {
            holder.tvBillStatus.setTextColor(android.graphics.Color.parseColor("#EF4444"));
        } else {
            holder.tvBillStatus.setTextColor(android.graphics.Color.parseColor("#10B981"));
        }

        if (bill.getUploadedAt() != null) {
            // Simplify date if it's too long
            String date = bill.getUploadedAt();
            if (date.contains("T")) date = date.split("T")[0];
            holder.tvBillDate.setText(date);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBillClick(bill);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    public static class BillViewHolder extends RecyclerView.ViewHolder {
        TextView tvBillId, tvBillDate, tvBillAmount, tvBillStatus;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBillId = itemView.findViewById(R.id.tvBillId);
            tvBillDate = itemView.findViewById(R.id.tvBillDate);
            tvBillAmount = itemView.findViewById(R.id.tvBillAmount);
            tvBillStatus = itemView.findViewById(R.id.tvBillStatus);
        }
    }
}
