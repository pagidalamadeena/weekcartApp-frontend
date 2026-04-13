package com.simats.frontend.network;

import android.content.Context;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;

public class NetworkClient {
    private static final String BASE_URL = "http://180.235.121.253:8177/"; // Updated for physical phone testing on local
                                                                        // network
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            TokenManager tokenManager = new TokenManager(context);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        String token = tokenManager.getToken();

                        if (token != null) {
                            Request request = original.newBuilder()
                                    .header("Authorization", "Bearer " + token)
                                    .build();
                            Response response = chain.proceed(request);

                            // If token is invalid/expired (401), we should notify the app to logout
                            if (response.code() == 401) {
                                tokenManager.clearToken();

                                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                                    android.content.Intent intent = new android.content.Intent(context,
                                            com.simats.frontend.LoginActivity.class);
                                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                            | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(intent);
                                });
                            }
                            return response;
                        }
                        return chain.proceed(original);
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
