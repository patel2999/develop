package com.example.kissanhub.user.di.component;


import android.content.Context;

import com.example.kissanhub.user.di.module.AdapterModule;
import com.example.kissanhub.user.di.qualifier.ActivityContext;
import com.example.kissanhub.user.di.scopes.ActivityScope;
import com.example.kissanhub.user.ui.activity.WeatherReportActivity;

import dagger.Component;


@ActivityScope
@Component(modules = AdapterModule.class, dependencies = ApplicationComponent.class)
public interface MainActivityComponent {

    @ActivityContext
    Context getContext();

    void injectMainActivity(WeatherReportActivity weatherReportActivity);
}
