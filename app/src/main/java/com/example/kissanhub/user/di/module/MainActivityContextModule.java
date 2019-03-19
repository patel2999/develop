package com.example.kissanhub.user.di.module;

import android.content.Context;

import com.example.kissanhub.user.di.qualifier.ActivityContext;
import com.example.kissanhub.user.di.scopes.ActivityScope;
import com.example.kissanhub.user.ui.activity.WeatherReportActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityContextModule {
    private WeatherReportActivity weatherReportActivity;

    public Context context;

    public MainActivityContextModule(WeatherReportActivity weatherReportActivity) {
        this.weatherReportActivity = weatherReportActivity;
        context = weatherReportActivity;
    }

    @Provides
    @ActivityScope
    public WeatherReportActivity providesMainActivity() {
        return weatherReportActivity;
    }

    @Provides
    @ActivityScope
    @ActivityContext
    public Context provideContext() {
        return context;
    }
}
