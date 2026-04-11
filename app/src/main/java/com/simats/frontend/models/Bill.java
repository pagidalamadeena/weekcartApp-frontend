package com.simats.frontend.models;

import java.util.List;

public class Bill {
    private int id;
    private float total_amount;
    private String status;
    private String uploaded_at;
    private List<BillItem> items;

    public int getId() {
        return id;
    }

    public float getTotalAmount() {
        return total_amount;
    }

    public String getStatus() {
        return status;
    }

    public List<BillItem> getItems() {
        return items;
    }

    public String getUploadedAt() {
        return uploaded_at;
    }
}
