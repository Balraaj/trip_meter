package com.forall.tripmeter.di.module

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.forall.tripmeter.ui.home.HomeViewModel
import dagger.Module
import dagger.Provides

@Module
class FragmentModule(private val fragment: Fragment) {

    @Provides
    fun provideHomeViewModel(): HomeViewModel {
        return ViewModelProvider(fragment.activity!!).get(HomeViewModel::class.java)
    }
}