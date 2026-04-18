package com.simats.weekcart.network;

import com.simats.weekcart.models.AnalyticsResponse;
import com.simats.weekcart.models.Bill;
import com.simats.weekcart.models.LoginRequest;
import com.simats.weekcart.models.LoginResponse;
import com.simats.weekcart.models.Notification;
import com.simats.weekcart.models.RegisterRequest;
import com.simats.weekcart.models.User;
import java.util.List;
import okhttp3.MultipartBody;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import com.simats.weekcart.models.GroceryItem;

public interface ApiInterface {
    // Auth
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<Void> register(@Body RegisterRequest registerRequest);

    @GET("auth/profile")
    Call<User> getProfile();

    @PUT("auth/profile")
    Call<User> updateProfile(@Body User user);
    
    @POST("auth/forgot-password")
    Call<Map<String, String>> forgotPassword(@Body com.simats.weekcart.models.ForgotPasswordRequest request);
    
    @POST("auth/reset-password")
    Call<Map<String, String>> resetPassword(@Body com.simats.weekcart.models.ResetPasswordRequest request);

    // Bills
    @Multipart
    @POST("bills/upload")
    Call<Bill> uploadBill(@Part MultipartBody.Part file);

    @GET("bills/")
    Call<List<Bill>> getBills();

    @GET("bills/{id}")
    Call<Bill> getBill(@Path("id") int billId);

    @POST("bills/manual")
    Call<Bill> saveManualBill(@Body com.simats.weekcart.models.ManualBillRequest request);

    @GET("analytics/spending/monthly")
    Call<AnalyticsResponse> getMonthlySpending();

    @GET("analytics/spending/weekly")
    Call<AnalyticsResponse> getWeeklySpending();

    // Notifications
    @GET("notifications/")
    Call<List<Notification>> getNotifications();

    @POST("notifications/{id}/read")
    Call<Void> markNotificationRead(@Path("id") int notificationId);

    // Groceries
    @GET("grocery")
    Call<List<GroceryItem>> getGroceryList();

    @POST("grocery")
    Call<GroceryItem> addGroceryItem(@Body Map<String, String> body);

    @PUT("grocery/{id}")
    Call<Void> toggleGroceryItem(@Path("id") int itemId, @Body Map<String, Boolean> body);

    @DELETE("grocery/{id}")
    Call<Void> deleteGroceryItem(@Path("id") int itemId);

    // Health
    @GET("health/score")
    Call<com.simats.weekcart.models.HealthScoreResponse> getHealthScore();

    @GET("health/spending-by-category")
    Call<List<com.simats.weekcart.models.CategoryItem>> getSpendingByCategory();

    @GET("health/category-distribution")
    Call<List<com.simats.weekcart.models.CategoryItem>> getCategoryDistribution();

    @GET("health/weekly-comparison")
    Call<com.simats.weekcart.models.WeeklyHealthComparison> getWeeklyHealthComparison();

    // Reminders
    @GET("reminders/")
    Call<List<com.simats.weekcart.models.ReminderItem>> getReminders();

    @POST("reminders/")
    Call<com.simats.weekcart.models.ReminderItem> createReminder(@Body Map<String, String> body);

    @DELETE("reminders/{id}")
    Call<Void> deleteReminder(@Path("id") int reminderId);
}
