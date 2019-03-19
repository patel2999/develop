package com.example.kissanhub.user.ui.view;

import com.example.kissanhub.base.view.MvpView;
import com.example.kissanhub.user.storage.models.WeatherDataEntity;

import java.util.List;

public interface WeatherView extends MvpView {

    /**
     * Show Refresh view
     */
    void showLoadingView();

    /**
     * Hide Refresh view
     */
    void hideLoadingView();

    /**
     * Show snack bar when no internet connection available
     */
    void showSnackBarWhenNoInternet();

    /**
     * Set year list on Spinner view
     *
     * @param weatherDataResponse weather data response
     */
    void setYearDataOnView(List<WeatherDataEntity> weatherDataResponse);

    /**
     * Populate weather data on Graph view.
     *
     * @param weatherDataResponse weather data response
     */
    void updateWeatherReportOnGraph(List<WeatherDataEntity> weatherDataResponse);
}
