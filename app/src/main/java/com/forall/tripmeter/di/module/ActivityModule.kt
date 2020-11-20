package com.forall.tripmeter.di.module

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.forall.tripmeter.common.ViewModelProviderFactory
import com.forall.tripmeter.repository.Repository
import com.forall.tripmeter.ui.home.HomeViewModel
import com.forall.tripmeter.ui.splash.SplashViewModel
import dagger.Module
import dagger.Provides

@Module
class ActivityModule{

    @Provides
    fun provideHomeViewModel(activity: AppCompatActivity, repo: Repository): HomeViewModel {
        return ViewModelProvider(activity,
            ViewModelProviderFactory(HomeViewModel::class){
                HomeViewModel(repo) }).get(HomeViewModel::class.java)
    }

    @Provides
    fun provideSplashViewModel(activity: AppCompatActivity, repo: Repository): SplashViewModel {
        return ViewModelProvider(activity,
            ViewModelProviderFactory(SplashViewModel::class){
                SplashViewModel(repo) }).get(SplashViewModel::class.java)
    }
}