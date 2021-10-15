package com.forall.tripmeter.di.component

import com.forall.tripmeter.di.FragmentScope
import com.forall.tripmeter.di.module.FragmentModule
import com.forall.tripmeter.ui.home.HomeFragment
import com.forall.tripmeter.ui.home.trips.TripsFragment
import dagger.Component

@FragmentScope
@Component(dependencies = [ApplicationComponent::class], modules = [FragmentModule::class])
interface FragmentComponent {
    fun inject(homeFragment: HomeFragment)
    fun inject(tripsFragment: TripsFragment)

    @Component.Factory
    interface Factory{
        fun create(appComponent: ApplicationComponent,
                   fragmentModule: FragmentModule): FragmentComponent
    }
}