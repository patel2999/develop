package com.example.kissanhub.user.di.component;

import android.content.Context;

import com.example.kissanhub.MyApplication;
import com.example.kissanhub.user.di.module.ContextModule;
import com.example.kissanhub.user.di.module.RetrofitModule;
import com.example.kissanhub.user.di.qualifier.ApplicationContext;
import com.example.kissanhub.user.di.scopes.ApplicationScope;
import com.example.kissanhub.user.storage.remote.WeatherApiInterface;

import dagger.Component;

@ApplicationScope
@Component(modules = {ContextModule.class, RetrofitModule.class})
public interface ApplicationComponent {

    WeatherApiInterface getApiInterface();

    @ApplicationContext
    Context getContext();

    void injectApplication(MyApplication myApplication);
}
