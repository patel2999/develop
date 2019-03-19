package com.example.kissanhub.user.di.module;


import com.example.kissanhub.user.di.scopes.ApplicationScope;
import com.example.kissanhub.user.storage.remote.NullOnEmptyConverterFactory;
import com.example.kissanhub.user.storage.remote.WeatherApiInterface;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class RetrofitModule {

    private static final String BASE_URL =
            "https://s3.eu-west-2.amazonaws.com/interview-question-data/metoffice/";

    @Provides
    @ApplicationScope
    WeatherApiInterface getApiInterface(Retrofit retroFit) {
        return retroFit.create(WeatherApiInterface.class);
    }

    /**
     * Create an instance of Retrofit object
     */
    @Provides
    @ApplicationScope
    Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(new NullOnEmptyConverterFactory(GsonConverterFactory.create()))
                .client(getOkHttpClient(getHttpLoggingInterceptor()))
                .build();
    }

    @Provides
    @ApplicationScope
    OkHttpClient getOkHttpClient(HttpLoggingInterceptor httpLoggingInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();
    }

    @Provides
    @ApplicationScope
    HttpLoggingInterceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }
}
