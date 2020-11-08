package com.forall.tripmeter.base

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.forall.tripmeter.common.AppComponentProvider
import com.forall.tripmeter.di.component.ActivityComponent
import com.forall.tripmeter.di.component.DaggerActivityComponent
import com.forall.tripmeter.di.module.ActivityModule
import javax.inject.Inject

abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity(){

    @Inject
    lateinit var viewModel: VM

    @LayoutRes
    protected abstract fun provideLayoutId(): Int
    protected abstract fun setupView(savedInstanceState: Bundle?)
    protected abstract fun injectDependencies(ac: ActivityComponent)

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?){
        injectDependencies(buildActivityComponent())
        super.onCreate(savedInstanceState)
        setContentView(provideLayoutId())
        setupObservers()
        setupView(savedInstanceState)
    }

    private fun buildActivityComponent(): ActivityComponent{
        return DaggerActivityComponent.factory()
            .create(ActivityModule(this), (application as AppComponentProvider).getAppComponent())
    }

    protected open fun setupObservers() {
        viewModel.messageString.observe(this, Observer {
            it?.let { showMessage(it); viewModel.messageString.value = null }
        })

        viewModel.messageStringId.observe(this, Observer {
            it?.let { showMessage(it); viewModel.messageStringId.value = null }
        })
    }

    fun showMessage(@StringRes resId: Int) = showMessage(getString(resId))

    fun showMessage(message: String) = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
}