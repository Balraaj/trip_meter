package com.forall.tripmeter.ui.home.aboutus

import android.view.View
import com.forall.tripmeter.R
import com.forall.tripmeter.base.BaseFragment
import com.forall.tripmeter.di.component.FragmentComponent
import com.forall.tripmeter.ui.home.HomeViewModel

class AboutUsFragment: BaseFragment<HomeViewModel>() {
    override fun provideLayoutId() = R.layout.fragment_about_us
    override fun injectDependencies(fc: FragmentComponent) = fc.inject(this)
    override fun setupView(view: View) {
    }
}