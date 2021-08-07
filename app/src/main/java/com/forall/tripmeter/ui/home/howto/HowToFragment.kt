package com.forall.tripmeter.ui.home.howto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.forall.tripmeter.base.BaseFragment
import com.forall.tripmeter.databinding.FragmentHowToBinding
import com.forall.tripmeter.di.component.FragmentComponent
import com.forall.tripmeter.ui.home.HomeViewModel

class HowToFragment: BaseFragment<HomeViewModel, FragmentHowToBinding>() {
    override fun provideBinding(inflater: LayoutInflater,
                                 container: ViewGroup?): FragmentHowToBinding{
        return FragmentHowToBinding.inflate(inflater, container, false)
    }

    override fun injectDependencies(fc: FragmentComponent) = fc.inject(this)
    override fun setupView(view: View) {}
}