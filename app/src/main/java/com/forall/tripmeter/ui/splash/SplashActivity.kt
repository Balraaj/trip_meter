package com.forall.tripmeter.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import com.forall.tripmeter.base.BaseActivity
import com.forall.tripmeter.common.Constants
import com.forall.tripmeter.databinding.ActivitySplashBinding
import com.forall.tripmeter.di.component.ActivityComponent
import com.forall.tripmeter.ui.home.HomeActivity
import com.google.firebase.messaging.FirebaseMessaging


class SplashActivity : BaseActivity<SplashViewModel, ActivitySplashBinding>() {

    override fun provideBinding() = ActivitySplashBinding.inflate(layoutInflater)
    override fun injectDependencies(ac: ActivityComponent) = ac.inject(this)

    override fun setupView(savedInstanceState: Bundle?) {
        window.setFlags(FLAG_LAYOUT_NO_LIMITS, FLAG_LAYOUT_NO_LIMITS)
        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        subscribeToFCM()
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(SplashActivity@this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 400)
    }


    private fun subscribeToFCM(){
        FirebaseMessaging.getInstance()
            .subscribeToTopic(Constants.FCM_TOPIC).addOnCompleteListener {}
    }
}