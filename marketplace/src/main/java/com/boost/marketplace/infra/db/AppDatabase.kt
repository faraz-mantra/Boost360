package com.boost.marketplace.infra.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.boost.marketplace.infra.db.dao.OffersDao
import com.boost.marketplace.infra.db.entity.OffersModel


@Database(entities = [OffersModel::class], version = 1)
public abstract class AppDatabase : RoomDatabase() {

    companion object {
        private const val DATABASE_NAME = "marketplace.db"
        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun offersDap(): OffersDao
}


