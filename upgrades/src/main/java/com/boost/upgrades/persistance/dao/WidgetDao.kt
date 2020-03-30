package com.biz2.nowfloats.boost.updates.persistance.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.boost.upgrades.data.model.WidgetModel
import io.reactivex.Single

@Dao
interface WidgetDao {

    @Query("SELECT * FROM Widget")
    fun queryUpdates(): Single<List<WidgetModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUpdates(updates: WidgetModel)

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    fun insertAllUPdates(updates: List<WidgetModel>)
}