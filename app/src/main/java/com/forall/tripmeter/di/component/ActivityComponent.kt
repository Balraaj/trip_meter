package com.forall.tripmeter.di.component

import com.forall.tripmeter.di.ActivityScope
import com.forall.tripmeter.di.module.ActivityModule
import com.forall.tripmeter.ui.home.HomeActivity
import com.forall.tripmeter.ui.splash.SplashActivity
import dagger.Component

@ActivityScope
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {
    fun inject(homeActivity: HomeActivity)
    fun inject(splashActivity: SplashActivity)

    @Component.Factory
    interface Factory{
        fun create(activityModule: ActivityModule,
                   applicationComponent: ApplicationComponent): ActivityComponent
    }
}