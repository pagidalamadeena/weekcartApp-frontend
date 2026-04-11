package com.simats.frontend.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.frontend.R;
import com.simats.frontend.models.CategoryItem;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final List<CategoryItem> items;
    private final boolean showAmount; // true = show ₹ amount, false = show item count

    // Healthy category colors
    private static final java.util.Set<String> HEALTHY_CATS = new java.util.HashSet<>(
            java.util.Arrays.asList("fruit", "vegetable", "grain", "dairy", "protein"));

    public CategoryAdapter(List<CategoryItem> items, boolean showAmount) {
        this.items = items;
        this.showAmount = showAmount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryItem item = items.get(position);
        String category = item.getCategory() != null ? item.getCategory() : "other";
        String displayName = category.substring(0, 1).toUpperCase() + category.substring(1).replace("_", " ");

        holder.tvCategoryName.setText(displayName);
        holder.tvPercentage.setText(item.getPercentage() + "%");

        if (showAmount) {
            holder.tvValue.setText("₹" + String.format("%.2f", item.getAmount()));
        } else {
            holder.tvValue.setText(item.getItemCount() + " items");
        }

        // Color dot by category
        int dotColor = item.getCategoryColor();
        holder.tvDot.setTextColor(dotColor);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDot, tvCategoryName, tvValue, tvPercentage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDot = itemView.findViewById(R.id.tvDot);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvValue = itemView.findViewById(R.id.tvValue);
            tvPercentage = itemView.findViewById(R.id.tvPercentage);
        }
    }
}
