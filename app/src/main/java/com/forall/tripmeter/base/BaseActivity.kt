package com.forall.tripmeter.base

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.forall.tripmeter.R
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
            .create(this, (application as AppComponentProvider).getAppComponent())
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

    /**
     * Display a confirmation dialog which pops up from bottom using animation.
     * @param message confirmation message to be displayed
     * @param onConfirm lambda to be executed on click of 'OK' button
     * @param onCancel lambda to be executed on click of 'CANCEL' button
     * @author VE00YM023
     */
    protected fun showConfirmationDialog(message: String, onConfirm: () -> Unit, onCancel: () -> Unit){
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirmation, null, false)
        val tvMessage = dialogView.findViewById(R.id.tv_message) as TextView
        val btnCancel = dialogView.findViewById(R.id.btn_cancel) as Button
        val btnConfirm = dialogView.findViewById(R.id.btn_confirm) as Button

        val qrConfirmDialog = AlertDialog.Builder(this, R.style.DialogSlideAnim)
            .setCancelable(false)
            .setView(dialogView)
            .create()

        tvMessage.text = message
        btnCancel.setOnClickListener { qrConfirmDialog.dismiss(); onCancel() }
        btnConfirm.setOnClickListener { qrConfirmDialog.dismiss(); onConfirm() }
        qrConfirmDialog.show()
        qrConfirmDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val params = qrConfirmDialog.window?.attributes
        params?.gravity = Gravity.BOTTOM
        params?.flags = params!!.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        qrConfirmDialog.window?.attributes = params
    }

    /**
     * Display a notification dialog which pops up from bottom using animation.
     * @param message confirmation message to be displayed
     * @param onConfirm lambda to be executed on click of 'OK' button
     * @author VE00YM023
     */
    protected fun showNotificationDialog(message: String, onConfirm: () -> Unit){
        val dialogView = layoutInflater.inflate(R.layout.dialog_notification, null, false)
        val tvMessage = dialogView.findViewById(R.id.tv_message) as TextView
        val btnConfirm = dialogView.findViewById(R.id.btn_confirm) as Button

        val qrConfirmDialog = AlertDialog.Builder(this, R.style.DialogSlideAnim)
            .setCancelable(false)
            .setView(dialogView)
            .create()

        tvMessage.text = message
        btnConfirm.setOnClickListener { qrConfirmDialog.dismiss(); onConfirm() }
        qrConfirmDialog.show()
        qrConfirmDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val params = qrConfirmDialog.window?.attributes
        params?.gravity = Gravity.BOTTOM
        params?.flags = params!!.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        qrConfirmDialog.window?.attributes = params
    }
}