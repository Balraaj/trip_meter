package com.forall.tripmeter.di.component

import android.app.Application
import com.forall.tripmeter.di.module.ApplicationModule
import com.forall.tripmeter.prefs.TripMeterSharedPrefs
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun getTripMeterSharedPrefs(): TripMeterSharedPrefs

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): ApplicationComponent
    }
}