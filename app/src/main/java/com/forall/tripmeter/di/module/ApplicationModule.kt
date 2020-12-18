package com.forall.tripmeter.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.forall.tripmeter.common.Constants.TRIP_METER_DATABASE
import com.forall.tripmeter.common.Constants.TRIP_METER_PREFS
import com.forall.tripmeter.database.Database
import com.forall.tripmeter.prefs.TripMeterSharedPrefs
import com.forall.tripmeter.repository.Repository
import com.forall.tripmeter.repository.ServiceRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    fun providesContext(app: Application): Context = app

    @Provides
    fun provideAppSharedPrefs(app: Application): SharedPreferences {
        return app.getSharedPreferences(TRIP_METER_PREFS, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideTripMeterSharedPrefs(prefs: SharedPreferences) = TripMeterSharedPrefs(prefs)

    @Singleton
    @Provides
    fun provideDatabase(app: Application): Database {
        return Room.databaseBuilder(app, Database::class.java, TRIP_METER_DATABASE)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries().build()
    }

    @Singleton
    @Provides
    fun provideRepository(database: Database, prefs: TripMeterSharedPrefs):Repository{
        return Repository(database, prefs)
    }

    @Singleton
    @Provides
    fun provideServiceRepository(database: Database,
                                 prefs: TripMeterSharedPrefs): ServiceRepository{
        return ServiceRepository(database, prefs)
    }
}