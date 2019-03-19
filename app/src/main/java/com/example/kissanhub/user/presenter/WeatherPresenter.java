package com.example.kissanhub.user.presenter;


import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.kissanhub.base.presenter.BasePresenter;
import com.example.kissanhub.user.storage.local.dao.WeatherDao;
import com.example.kissanhub.user.storage.models.WeatherDataEntity;
import com.example.kissanhub.user.storage.enums.LocationType;
import com.example.kissanhub.user.storage.enums.MetricsType;
import com.example.kissanhub.user.storage.remote.WeatherApiInterface;
import com.example.kissanhub.user.ui.view.WeatherView;
import com.example.kissanhub.user.utils.AppExecutors;
import com.example.kissanhub.user.utils.RetrieveTask;
import com.example.kissanhub.user.utils.Util;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WeatherPresenter extends BasePresenter<WeatherView> implements IWeatherPresenter {

    /**
     * Holds Weather Api Interface reference
     */
    private WeatherApiInterface mWeatherApiInterface;
    /**
     * Hold Weather data access object.
     */
    private WeatherDao mWeatherDao;
    /***
     * Holds Metrics type enum value
     */
    private MetricsType mMetricsType;
    /**
     * Holds Location type enum value
     */
    private LocationType mLocationType;
    /**
     * Hold instance of Executor service
     */
    private AppExecutors mAppExecutors;
    /**
     * Array list to hold weather data from Database.
     */
    private List<WeatherDataEntity> mWeatherDataListFromDB = new ArrayList<>();
    /**
     * Holds context instance
     */
    private Context mContext;

    @Inject
    public WeatherPresenter(Context context, WeatherDao weatherDao,
                            WeatherApiInterface weatherApiInterface) {
        mContext = context;
        mWeatherDao = weatherDao;
        mWeatherApiInterface = weatherApiInterface;
        mAppExecutors = AppExecutors.getAppExecutorInstance();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void notifyUiReady() {
        if (!isViewAttached()) {
            return;
        }
        if (Util.isConnectedToInternet(mContext)) {
            getWeatherReportFromServer();
        } else {
            checkWeatherDataAvailableOnDB();
        }
    }

    @Override
    public void setMetricsAndLocation(MetricsType metricsType, LocationType locationType) {
        mMetricsType = metricsType;
        mLocationType = locationType;
    }

    /**
     * To get Weather report data from server.
     */
    private void getWeatherReportFromServer() {
        getView().showLoadingView();
        mWeatherApiInterface.getWeatherList(mMetricsType.getMetricsType(),
                mLocationType.getLocationName()).enqueue(new Callback<List<WeatherDataEntity>>() {
            @Override
            public void onResponse(@NonNull Call<List<WeatherDataEntity>> call,
                                   @NonNull Response<List<WeatherDataEntity>> response) {
                Log.wtf("URL Called", call.request().url() + "");

                if (response.isSuccessful()) {
                    final List<WeatherDataEntity> weatherData = getWeatherData(response);
                    storeWeatherDataIntoDatabase(weatherData);
                    getView().hideLoadingView();
                    getView().setYearDataOnView(weatherData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<WeatherDataEntity>> call,
                                  @NonNull Throwable t) {
                Log.wtf("URL Called on failure", call.request().url() + "");
                // Stopping swipe refresh
                getView().hideLoadingView();
            }
        });
    }

    /**
     * Fetch Weather data from server response
     *
     * @param response server response
     * @return Weather data entity list.
     */
    private List<WeatherDataEntity> getWeatherData(Response<List<WeatherDataEntity>> response) {
        List<WeatherDataEntity> weatherData = response.body();
        if (weatherData != null && !weatherData.isEmpty()) {
            for (int i = 0; i < weatherData.size(); i++) {
                weatherData.get(i).setId(i + 1);
                weatherData.get(i).setMetricsType(mMetricsType.getMetricsType());
                weatherData.get(i).setLocation(mLocationType.getLocationName());
            }
        }
        return weatherData;
    }

    /**
     * To store weather data into database.
     *
     * @param weatherData weather data entity list object.
     */
    private void storeWeatherDataIntoDatabase(final List<WeatherDataEntity> weatherData) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mAppExecutors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mWeatherDataListFromDB =
                                mWeatherDao.findByLocationAndMetrics
                                        (mMetricsType.getMetricsType(),
                                                mLocationType.getLocationName());
                        if (mWeatherDataListFromDB.isEmpty()) {
                            mWeatherDao.storeWeatherData(weatherData);
                        }
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getYearlyWeatherReport(final String year) {
        getView().showLoadingView();
        new RetrieveTask(this, mMetricsType, mLocationType, mWeatherDao, year).execute();
    }

    @Override
    public void populateDataFromDB(List<WeatherDataEntity> weatherDataResponse) {
        // Stopping swipe refresh
        getView().hideLoadingView();
        getView().updateWeatherReportOnGraph(weatherDataResponse);
    }

    /**
     * Check weather data is available on Database or not.
     */
    private void checkWeatherDataAvailableOnDB() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mAppExecutors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mWeatherDataListFromDB.clear();
                        mWeatherDataListFromDB =
                                mWeatherDao.findByLocationAndMetrics
                                        (mMetricsType.getMetricsType(),
                                                mLocationType.getLocationName());
                        new Handler(mContext.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (!mWeatherDataListFromDB.isEmpty()) {
                                    getView().setYearDataOnView(mWeatherDataListFromDB);
                                } else {
                                    getView().showSnackBarWhenNoInternet();
                                }
                                // Stopping swipe refresh
                                getView().hideLoadingView();
                            }
                        });
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }
}
