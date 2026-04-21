package com.simats.Weekcart.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.Weekcart.R;
import com.simats.Weekcart.models.GroceryItem;
import com.simats.Weekcart.network.ApiInterface;
import com.simats.Weekcart.network.NetworkClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> {

    private List<GroceryItem> items;
    private Context context;
    private OnListChangedListener listener;

    public interface OnListChangedListener {
        void onListChanged();
    }

    public GroceryAdapter(List<GroceryItem> items, Context context, OnListChangedListener listener) {
        this.items = items;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grocery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroceryItem item = items.get(position);

        holder.tvItemName.setText(item.getItemName());
        holder.cbIsPurchased.setOnCheckedChangeListener(null); // Prevent unwanted triggers
        holder.cbIsPurchased.setChecked(item.isPurchased());

        if (item.isPurchased()) {
            holder.tvItemName.setPaintFlags(holder.tvItemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvItemName.setPaintFlags(holder.tvItemName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.cbIsPurchased.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleItemStatus(item, isChecked, holder);
        });

        holder.ivDeleteBtn.setOnClickListener(v -> deleteItem(item, position));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    private void toggleItemStatus(GroceryItem item, boolean isChecked, ViewHolder holder) {
        ApiInterface apiService = NetworkClient.getClient(context).create(ApiInterface.class);
        Map<String, Boolean> body = new HashMap<>();
        body.put("is_purchased", isChecked);

        apiService.toggleGroceryItem(item.getId(), body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    item.setPurchased(isChecked);
                    if (isChecked) {
                        holder.tvItemName
                                .setPaintFlags(holder.tvItemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        holder.tvItemName
                                .setPaintFlags(holder.tvItemName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                    if (listener != null)
                        listener.onListChanged();
                } else {
                    holder.cbIsPurchased.setChecked(!isChecked); // Revert
                    Toast.makeText(context, "Failed to update item", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                holder.cbIsPurchased.setChecked(!isChecked); // Revert
                Toast.makeText(context, "Error updating item", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteItem(GroceryItem item, int position) {
        ApiInterface apiService = NetworkClient.getClient(context).create(ApiInterface.class);
        apiService.deleteGroceryItem(item.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    items.remove(position);
                    notifyItemRemoved(position);
                    if (listener != null)
                        listener.onListChanged();
                } else {
                    Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error deleting item", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbIsPurchased;
        TextView tvItemName;
        ImageView ivDeleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbIsPurchased = itemView.findViewById(R.id.cbIsPurchased);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            ivDeleteBtn = itemView.findViewById(R.id.ivDeleteBtn);
        }
    }
}
