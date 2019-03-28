package com.example.hares.movies.Retrofit;

import com.example.hares.movies.BuildConfig;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InitializingRetrofit {
    private static final String API_KEY = BuildConfig.API_KEY;

    private static Interceptor interceptor = chain -> {
        Request baseRequest = chain.request();
        HttpUrl baseHttpUrl = baseRequest.url();

        HttpUrl url = baseHttpUrl.newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .build();

        Request.Builder requestBuilder = baseRequest.newBuilder().url(url);

        Request request = requestBuilder.build();
        return chain.proceed(request);
    };

    public static Retrofit getClient() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        httpBuilder.addInterceptor(interceptor);
        httpBuilder.addInterceptor(loggingInterceptor);

        OkHttpClient client = httpBuilder.build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }
}

