package com.example.kissanhub.user.storage.remote;


import com.example.kissanhub.user.storage.models.WeatherDataEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface WeatherApiInterface {


    @GET("{metrics}-{location}.json")
    Call<List<WeatherDataEntity>> getWeatherList(@Path("metrics") String metrics,
                                                 @Path("location") String location);

    @GET
    Call<WeatherDataEntity> getWeatherYearlyData(@Url String url, @Query("format") String format);
}
