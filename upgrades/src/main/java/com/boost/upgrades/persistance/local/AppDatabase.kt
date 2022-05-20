package com.biz2.nowfloats.boost.updates.persistance.local


import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.biz2.nowfloats.boost.updates.persistance.dao.*
import com.boost.upgrades.data.model.*

import com.boost.upgrades.utils.Constants

@Database(entities = [FeaturesModel::class, WidgetModel::class, BundlesModel::class, CartModel::class, CouponsModel::class, YoutubeVideoModel::class, MarketOfferModel::class], version = 19, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        private var instance: AppDatabase? = null
        fun getInstance(context: Application): AppDatabase? {
            if (instance == null) {
                synchronized(AppDatabase::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context,
                            AppDatabase::class.java, Constants.DATABASE_NAME
                        ).fallbackToDestructiveMigration().build()
                    }
                }
            }
            return instance
        }
    }

    abstract fun widgetDao(): WidgetDao

    abstract fun featuresDao(): FeaturesDao

    abstract fun bundlesDao(): BundlesDao

    abstract fun couponsDao(): CouponsDao

    abstract fun youtubeVideoDao(): YoutubeVideoDao

    abstract fun cartDao(): CartDao

    abstract fun marketOffersDao(): MarketOfferDao
}