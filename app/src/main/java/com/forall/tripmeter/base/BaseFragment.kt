package com.forall.tripmeter.base

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.forall.tripmeter.R
import com.forall.tripmeter.common.AppComponentProvider
import com.forall.tripmeter.di.component.DaggerFragmentComponent
import com.forall.tripmeter.di.component.FragmentComponent
import com.forall.tripmeter.di.module.FragmentModule
import javax.inject.Inject

abstract class BaseFragment<VM : BaseViewModel, BD: ViewBinding> : Fragment() {

    @Inject
    lateinit var viewModel: VM

    protected lateinit var binding: BD private set

    protected abstract fun provideBinding(inflater: LayoutInflater, container: ViewGroup?): BD
    protected abstract fun injectDependencies(fc: FragmentComponent)
    protected abstract fun setupView(view: View)

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies(buildFragmentComponent())
        super.onCreate(savedInstanceState)
    }

    private fun buildFragmentComponent(): FragmentComponent {
        return DaggerFragmentComponent.factory()
            .create((activity!!.application as AppComponentProvider).getAppComponent(),
                    FragmentModule(this))
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = provideBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
        setupObservers()
    }

    protected open fun setupObservers() {
        viewModel.messageString.observe(this, Observer {
            it?.let{ showMessage(it); viewModel.messageString.value = null }
        })

        viewModel.messageStringId.observe(this, Observer {
            it?.let { showMessage(it); viewModel.messageStringId.value = null }
        })

        viewModel.showNotificationDialog.observe(viewLifecycleOwner){ stringRes ->
            stringRes?.let {
                showNotificationDialog(getString(stringRes)){}
                viewModel.showNotificationDialog.value = null
            }
        }
    }

    fun showMessage(message: String) = context?.let {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showMessage(@StringRes resId: Int) = showMessage(getString(resId))


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

        val qrConfirmDialog = AlertDialog.Builder(requireContext(), R.style.DialogSlideAnim)
            .setCancelable(false)
            .setView(dialogView)
            .create()

        tvMessage.text = message
        btnCancel.setOnClickListener { qrConfirmDialog.dismiss(); onCancel() }
        btnConfirm.setOnClickListener { qrConfirmDialog.dismiss(); onConfirm() }
        qrConfirmDialog.show()
        qrConfirmDialog.window?.setLayout(MATCH_PARENT, WRAP_CONTENT)
        val params = qrConfirmDialog.window?.attributes
        params?.gravity = Gravity.BOTTOM
        params?.flags = params!!.flags and FLAG_DIM_BEHIND.inv()
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

        val qrConfirmDialog = AlertDialog.Builder(requireContext(), R.style.DialogSlideAnim)
            .setCancelable(false)
            .setView(dialogView)
            .create()

        tvMessage.text = message
        btnConfirm.setOnClickListener { qrConfirmDialog.dismiss(); onConfirm() }
        qrConfirmDialog.show()
        qrConfirmDialog.window?.setLayout(MATCH_PARENT, WRAP_CONTENT)
        val params = qrConfirmDialog.window?.attributes
        params?.gravity = Gravity.BOTTOM
        params?.flags = params!!.flags and FLAG_DIM_BEHIND.inv()
        qrConfirmDialog.window?.attributes = params
    }
}