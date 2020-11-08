package com.forall.tripmeter.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.forall.tripmeter.common.AppComponentProvider
import com.forall.tripmeter.di.component.DaggerFragmentComponent
import com.forall.tripmeter.di.component.FragmentComponent
import com.forall.tripmeter.di.module.FragmentModule
import javax.inject.Inject

abstract class BaseFragment<VM : BaseViewModel> : Fragment() {

    @Inject
    lateinit var viewModel: VM

    @LayoutRes
    protected abstract fun provideLayoutId(): Int
    protected abstract fun injectDependencies(fc: FragmentComponent)
    protected abstract fun setupView(view: View)

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies(buildFragmentComponent())
        super.onCreate(savedInstanceState)
        setupObservers()
    }

    private fun buildFragmentComponent(): FragmentComponent {
        return DaggerFragmentComponent.factory()
            .create((activity!!.application as AppComponentProvider).getAppComponent(),
                    FragmentModule(this))
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(provideLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
    }

    protected open fun setupObservers() {
        viewModel.messageString.observe(this, Observer {
            it?.let{ showMessage(it); viewModel.messageString.value = null }
        })

        viewModel.messageStringId.observe(this, Observer {
            it?.let { showMessage(it); viewModel.messageStringId.value = null }
        })
    }

    fun showMessage(message: String) = context?.let {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showMessage(@StringRes resId: Int) = showMessage(getString(resId))
}