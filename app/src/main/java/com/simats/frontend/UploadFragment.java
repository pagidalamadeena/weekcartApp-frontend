package com.simats.frontend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;
import android.widget.Toast;
import com.simats.frontend.network.ApiInterface;
import com.simats.frontend.network.NetworkClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadFragment extends Fragment {

    private LinearLayout llEmptyState, llSelectedState, llActionButtons, llItemsContainer, llProcessingState;
    private CardView cvExtractedItems;
    private Button btnChangeImage, btnProcessBill, btnChangeImageOnly, btnSaveBill;
    private TextView tvTotalItemsCount, tvTotalAmountValue;
    private ImageView ivBillPreview;
    private RecyclerView rvBillHistory;
    private BillsAdapter billsAdapter;
    private Uri selectedImageUri;

    public static UploadFragment newInstance(boolean startWithPreview) {
        UploadFragment fragment = new UploadFragment();
        Bundle args = new Bundle();
        args.putBoolean("start_with_preview", startWithPreview);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        // Initialize views
        llEmptyState = view.findViewById(R.id.llEmptyState);
        llSelectedState = view.findViewById(R.id.llSelectedState);
        llActionButtons = view.findViewById(R.id.llActionButtons);
        llProcessingState = view.findViewById(R.id.llProcessingState);
        cvExtractedItems = view.findViewById(R.id.cvExtractedItems);
        llItemsContainer = view.findViewById(R.id.llItemsContainer);
        tvTotalItemsCount = view.findViewById(R.id.tvTotalItemsCount);
        tvTotalAmountValue = view.findViewById(R.id.tvTotalAmountValue);
        ivBillPreview = view.findViewById(R.id.ivBillPreview);

        btnChangeImage = view.findViewById(R.id.btnChangeImage);
        btnProcessBill = view.findViewById(R.id.btnProcessBill);
        btnChangeImageOnly = view.findViewById(R.id.btnChangeImageOnly);
        btnSaveBill = view.findViewById(R.id.btnSaveBill);
        rvBillHistory = view.findViewById(R.id.rvBillHistory);

        rvBillHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        // Back button logic
        view.findViewById(R.id.ivBack).setOnClickListener(v -> {
            if (getActivity() instanceof DashboardActivity) {
                ((DashboardActivity) getActivity()).switchToHome();
            }
        });

        // 1. Initial State: Click to upload
        llEmptyState.setOnClickListener(v -> openImagePicker());

        // 2. Selected State: Change Image
        View.OnClickListener changeImageListener = v -> openImagePicker();
        btnChangeImage.setOnClickListener(changeImageListener);
        btnChangeImageOnly.setOnClickListener(changeImageListener);

        // 3. Process Bill logic
        btnProcessBill.setOnClickListener(v -> uploadBill());

        // 4. Save Bill logic
        btnSaveBill.setOnClickListener(v -> {
            if (getActivity() instanceof DashboardActivity) {
                ((DashboardActivity) getActivity()).switchToHome();
            }
        });

        // Handle initial state from arguments
        if (getArguments() != null && getArguments().getBoolean("start_with_preview", false)) {
            showSelectedState();
        } else {
            showEmptyState();
        }

        fetchBillHistory();

        return view;
    }

    private void fetchBillHistory() {
        ApiInterface apiService = NetworkClient.getClient(getContext()).create(ApiInterface.class);
        apiService.getBills().enqueue(new Callback<List<com.simats.frontend.models.Bill>>() {
            @Override
            public void onResponse(Call<List<com.simats.frontend.models.Bill>> call,
                    Response<List<com.simats.frontend.models.Bill>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    billsAdapter = new BillsAdapter(response.body());
                    rvBillHistory.setAdapter(billsAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<com.simats.frontend.models.Bill>> call, Throwable t) {
                // Silently fail for history
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (ivBillPreview != null) {
                ivBillPreview.setImageURI(selectedImageUri);
            }
            showSelectedState();
        }
    }

    private void uploadBill() {
        if (selectedImageUri == null) {
            Toast.makeText(getContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoadingState();

        try {
            File file = getFileFromUri(selectedImageUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            ApiInterface apiService = NetworkClient.getClient(getContext()).create(ApiInterface.class);
            apiService.uploadBill(body).enqueue(new Callback<com.simats.frontend.models.Bill>() {
                @Override
                public void onResponse(Call<com.simats.frontend.models.Bill> call,
                        Response<com.simats.frontend.models.Bill> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(getContext(), "Bill processed successfully!", Toast.LENGTH_SHORT).show();
                        showProcessedState(response.body());
                    } else {
                        Toast.makeText(getContext(), "Upload failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        showSelectedState();
                    }
                }

                @Override
                public void onFailure(Call<com.simats.frontend.models.Bill> call, Throwable t) {
                    Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    showSelectedState();
                }
            });

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error preparing file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            showSelectedState();
        }
    }

    private File getFileFromUri(Uri uri) throws Exception {
        InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
        File file = new File(getContext().getCacheDir(), "upload_bill.jpg");
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
        }
        return file;
    }

    private void showEmptyState() {
        llEmptyState.setVisibility(View.VISIBLE);
        llSelectedState.setVisibility(View.GONE);
        llProcessingState.setVisibility(View.GONE);
        cvExtractedItems.setVisibility(View.GONE);
    }

    private void showSelectedState() {
        llEmptyState.setVisibility(View.GONE);
        llSelectedState.setVisibility(View.VISIBLE);
        llProcessingState.setVisibility(View.GONE);
        cvExtractedItems.setVisibility(View.GONE);
        llActionButtons.setVisibility(View.VISIBLE);
        btnChangeImageOnly.setVisibility(View.GONE);
    }

    private void showLoadingState() {
        llEmptyState.setVisibility(View.GONE);
        llSelectedState.setVisibility(View.GONE);
        llProcessingState.setVisibility(View.VISIBLE);
        cvExtractedItems.setVisibility(View.GONE);
    }

    private void showProcessedState(com.simats.frontend.models.Bill bill) {
        llProcessingState.setVisibility(View.GONE);
        llSelectedState.setVisibility(View.VISIBLE);
        llActionButtons.setVisibility(View.GONE);
        btnChangeImageOnly.setVisibility(View.VISIBLE);
        cvExtractedItems.setVisibility(View.VISIBLE);

        llItemsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        if (bill.getItems() != null && !bill.getItems().isEmpty()) {
            for (com.simats.frontend.models.BillItem item : bill.getItems()) {
                View itemView = inflater.inflate(R.layout.item_bill_edit, llItemsContainer, false);

                android.widget.EditText etName = itemView.findViewById(R.id.etItemName);
                android.widget.EditText etPrice = itemView.findViewById(R.id.etItemPrice);
                android.widget.EditText etQuantity = itemView.findViewById(R.id.etItemQuantity);

                etName.setText(item.getItemName());
                etPrice.setText(String.valueOf(item.getPrice()));
                etQuantity.setText(String.valueOf(item.getQuantity()));

                llItemsContainer.addView(itemView);
            }
            tvTotalItemsCount.setText("Total Items: " + bill.getItems().size());
            tvTotalAmountValue.setText("Total: ₹" + String.format("%.2f", bill.getTotalAmount()));
        } else {
            Toast.makeText(getContext(), "No items extracted from bill", Toast.LENGTH_SHORT).show();
        }
    }
}
