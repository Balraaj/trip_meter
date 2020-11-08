package com.forall.tripmeter

import android.app.Application
import com.forall.tripmeter.common.AppComponentProvider
import com.forall.tripmeter.di.component.ApplicationComponent
import com.forall.tripmeter.di.component.DaggerApplicationComponent
import java.lang.IllegalStateException

/**
 * Project Name : Trip Meter
 * @author  Balraj
 * @date   Oct 26, 2020
 *
 * Description
 * -----------------------------------------------------------------------------------
 * TripMeterApp : Implementation of Application. used to provide application component
 *                which stays the same throughout the execution of application.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */
class TripMeterApp: Application(), AppComponentProvider {

    private lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        createApplicationComponent()
    }

    private fun createApplicationComponent(){
        applicationComponent = DaggerApplicationComponent.factory().create(this)
    }

    override fun getAppComponent(): ApplicationComponent {
//        if(!applicationComponent.isIn){
//            throw IllegalStateException("Application component is not initialized")
//        }
        return applicationComponent
    }

}