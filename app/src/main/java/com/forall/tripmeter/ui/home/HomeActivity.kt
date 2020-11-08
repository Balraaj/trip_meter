package com.forall.tripmeter.ui.home

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.forall.tripmeter.R
import com.forall.tripmeter.base.BaseActivity
import com.forall.tripmeter.di.component.ActivityComponent
import kotlinx.android.synthetic.main.content_main.*

class HomeActivity : BaseActivity<HomeViewModel>() {


    override fun provideLayoutId() = R.layout.content_main
    override fun injectDependencies(ac: ActivityComponent) = ac.inject(this)

    override fun setupView(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val navController = findNavController(R.id.nav_host_fragment)
        nav_bar_bottom.setupWithNavController(navController)
    }

}