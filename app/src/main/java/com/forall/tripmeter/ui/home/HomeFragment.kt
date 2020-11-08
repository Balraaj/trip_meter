package com.forall.tripmeter.ui.home

import android.view.View
import androidx.lifecycle.Observer
import com.forall.tripmeter.R
import com.forall.tripmeter.base.BaseFragment
import com.forall.tripmeter.common.Constants.EMPTY_STRING
import com.forall.tripmeter.di.component.FragmentComponent
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment<HomeViewModel>() {


    override fun provideLayoutId() = R.layout.fragment_home
    override fun injectDependencies(fc: FragmentComponent) = fc.inject(this)

    override fun setupView(view: View) {
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.averageSpeed.observe(this, Observer {
            tv_speed.text = if(it == EMPTY_STRING) { "---" } else { it }
        })
    }
}