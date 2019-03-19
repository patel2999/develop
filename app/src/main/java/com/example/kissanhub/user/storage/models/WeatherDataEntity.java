package com.example.kissanhub.user.storage.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.example.kissanhub.user.storage.local.DBConstant;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = DBConstant.USERS_TABLE_NAME)
public class WeatherDataEntity {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @ColumnInfo(name = "id")
    private long id;

    @SerializedName("metrics")
    @ColumnInfo(name = "metrics")
    public String mMetricsType;

    @SerializedName("location")
    @ColumnInfo(name = "location")
    public String mLocation;

    @SerializedName("value")
    @ColumnInfo(name = "value")
    public String mTemperature;

    @SerializedName("year")
    @ColumnInfo(name = "year")
    public String mYear;

    @SerializedName("month")
    @ColumnInfo(name = "month")
    public String mMonth;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMetricsType() {
        return mMetricsType;
    }

    public void setMetricsType(String mMetricsType) {
        this.mMetricsType = mMetricsType;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String mWeatherLocation) {
        this.mLocation = mWeatherLocation;
    }

    public String getTemperature() {
        return mTemperature;
    }

    public void setTemperature(String mTemperature) {
        this.mTemperature = mTemperature;
    }

    public String getYear() {
        return mYear;
    }

    public void setYear(String mYear) {
        this.mYear = mYear;
    }

    public String getMonth() {
        return mMonth;
    }

    public void setMonth(String mMonth) {
        this.mMonth = mMonth;
    }
}
