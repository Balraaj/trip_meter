package com.forall.tripmeter.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.forall.tripmeter.common.Constants.TRIP_METER_PREFS
import com.forall.tripmeter.prefs.TripMeterSharedPrefs
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    fun providesContext(app: Application): Context = app

    @Provides
    fun provideAppSharedPrefs(app: Application): SharedPreferences {
        return app.getSharedPreferences(TRIP_METER_PREFS, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideTripMeterSharedPrefs(prefs: SharedPreferences) = TripMeterSharedPrefs(prefs)
}