package com.forall.tripmeter.di.module

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.forall.tripmeter.ui.home.HomeViewModel
import com.forall.tripmeter.common.ViewModelProviderFactory
import com.forall.tripmeter.prefs.TripMeterSharedPrefs
import com.forall.tripmeter.ui.splash.SplashViewModel
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private val activity: AppCompatActivity){

    @Provides
    fun provideHomeViewModel(sharedPrefs: TripMeterSharedPrefs): HomeViewModel {
        return ViewModelProvider(activity,
            ViewModelProviderFactory(HomeViewModel::class){
                HomeViewModel(sharedPrefs) }).get(HomeViewModel::class.java)
    }

    @Provides
    fun provideSplashViewModel(sharedPrefs: TripMeterSharedPrefs): SplashViewModel {
        return ViewModelProvider(activity,
            ViewModelProviderFactory(SplashViewModel::class){
                SplashViewModel(sharedPrefs) }).get(SplashViewModel::class.java)
    }
}