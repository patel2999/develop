package com.example.kissanhub.user.utils;

import android.os.AsyncTask;

import com.example.kissanhub.user.presenter.WeatherPresenter;
import com.example.kissanhub.user.storage.local.dao.WeatherDao;
import com.example.kissanhub.user.storage.models.WeatherDataEntity;
import com.example.kissanhub.user.storage.enums.LocationType;
import com.example.kissanhub.user.storage.enums.MetricsType;

import java.util.List;

public class RetrieveTask extends AsyncTask<Void, Void, List<WeatherDataEntity>> {

    private WeatherPresenter activityReference;
    MetricsType mMetricsType;
    LocationType mLocationType;
    WeatherDao mWeatherDao;
    String mYear;

    // only retain a weak reference to the activity
    public RetrieveTask(WeatherPresenter context, MetricsType metricsType, LocationType locationType,
                        WeatherDao weatherDao, String year) {
        activityReference = context;
        mMetricsType = metricsType;
        mLocationType = locationType;
        mWeatherDao = weatherDao;
        mYear = year;
    }

    @Override
    protected List<WeatherDataEntity> doInBackground(Void... voids) {
        if (activityReference != null)
            return mWeatherDao.findByYear(mMetricsType.getMetricsType(),
                    mLocationType.getLocationName(), mYear);
        else
            return null;
    }

    @Override
    protected void onPostExecute(List<WeatherDataEntity> weatherDataEntityList) {
        if (weatherDataEntityList != null && weatherDataEntityList.size() > 0) {
                activityReference.populateDataFromDB(weatherDataEntityList);
        }
    }
}

