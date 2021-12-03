package com.boost.marketplace.infra.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import com.boost.marketplace.infra.db.AppDatabase
import com.boost.marketplace.MarketplaceApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule(private val application: MarketplaceApplication) {
    @Provides
    fun provideContext(): Context {
        return application
    }

    @Provides
    fun provideApplication(): Application {
        return application
    }

    @Provides
    fun provideDefaultSharedPreferences(context: Context?): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): AppDatabase {
        return AppDatabase.create(application)
    }

    @Provides
    fun provideWorkManager(): WorkManager {
        return WorkManager.getInstance(application)
    }
}


