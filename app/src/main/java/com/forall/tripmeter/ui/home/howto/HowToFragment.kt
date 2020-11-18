package com.forall.tripmeter.ui.home.howto

import android.view.View
import com.forall.tripmeter.R
import com.forall.tripmeter.base.BaseFragment
import com.forall.tripmeter.di.component.FragmentComponent
import com.forall.tripmeter.ui.home.HomeViewModel

class HowToFragment: BaseFragment<HomeViewModel>() {
    override fun provideLayoutId() = R.layout.fragment_how_to
    override fun injectDependencies(fc: FragmentComponent) = fc.inject(this)
    override fun setupView(view: View) {}
}