package com.example.kissanhub.user.storage.local.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.kissanhub.user.storage.local.DBConstant;
import com.example.kissanhub.user.storage.models.WeatherDataEntity;

import java.util.List;

@Dao
public interface WeatherDao {

    @Query("SELECT * FROM "+ DBConstant.USERS_TABLE_NAME)
    List<WeatherDataEntity> loadWeatherReport();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void storeWeatherData(List<WeatherDataEntity> localWeatherDatumEntities);

    @Insert
    void insert(WeatherDataEntity... weatherDataEntities);

    @Query("SELECT * FROM "+ DBConstant.USERS_TABLE_NAME + " where location LIKE :locationName " +
            "and metrics LIKE :metrics")
    List<WeatherDataEntity> findByLocationAndMetrics(String metrics, String locationName);

    @Query("SELECT * FROM "+ DBConstant.USERS_TABLE_NAME + " where location LIKE :locationName " +
            "and metrics LIKE :metrics and year LIKE :year")
    List<WeatherDataEntity> findByYear(String metrics, String locationName, String year);

    @Query("SELECT * FROM "+ DBConstant.USERS_TABLE_NAME + " where location LIKE :locationName")
    List<WeatherDataEntity> getItemByLocationAndMetrics(String locationName);
}
