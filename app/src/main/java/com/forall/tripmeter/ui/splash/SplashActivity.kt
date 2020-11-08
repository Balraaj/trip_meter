package com.forall.tripmeter.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager.LayoutParams.*
import com.forall.tripmeter.R
import com.forall.tripmeter.base.BaseActivity
import com.forall.tripmeter.di.component.ActivityComponent
import com.forall.tripmeter.ui.home.HomeActivity

class SplashActivity : BaseActivity<SplashViewModel>() {

    override fun provideLayoutId() = R.layout.activity_splash
    override fun injectDependencies(ac: ActivityComponent) = ac.inject(this)

    override fun setupView(savedInstanceState: Bundle?) {
        window.setFlags(FLAG_LAYOUT_NO_LIMITS, FLAG_LAYOUT_NO_LIMITS)
        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(SplashActivity@this, HomeActivity::class.java)
            startActivity(intent)
        }, 1000)
    }

}