package com.forall.tripmeter.common

import com.forall.tripmeter.di.component.ApplicationComponent

/**
 * Project Name : Trip Meter
 * @author  Balraj
 * @date   Oct 26, 2020
 *
 * Description
 * -----------------------------------------------------------------------------------
 * AppComponentProvider : This interface allows the TripMeterApp class to provide an instance
 *                        of ApplicationComponent.
 *                        we declare an interface because if don't want casts
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */
interface AppComponentProvider {
    fun getAppComponent(): ApplicationComponent
}