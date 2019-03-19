package com.example.kissanhub.user.presenter;


import com.example.kissanhub.user.storage.models.WeatherDataEntity;
import com.example.kissanhub.user.storage.enums.LocationType;
import com.example.kissanhub.user.storage.enums.MetricsType;

import java.util.List;


public interface IWeatherPresenter {

    /**
     * Notify to UI ready.
     */
    void notifyUiReady();

    /**
     * To set Metrics and Location
     *
     * @param metricsType  metrics type
     * @param locationType location type
     */
    void setMetricsAndLocation(MetricsType metricsType, LocationType locationType);

    /**
     * To get yearly weather report.
     *
     * @param year year
     */
    void getYearlyWeatherReport(String year);

    /**
     * Populate the weather data from database.
     *
     * @param weatherDataEntities weather data entities list
     */
    void populateDataFromDB(List<WeatherDataEntity> weatherDataEntities);
}
