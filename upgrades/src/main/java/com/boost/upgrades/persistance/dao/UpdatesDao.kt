package com.biz2.nowfloats.boost.updates.persistance.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.boost.upgrades.data.model.UpdatesModel
import io.reactivex.Single

@Dao
interface UpdatesDao {

    @Query("SELECT * FROM updates")
    fun queryUpdates(): Single<List<UpdatesModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUpdates(updates: UpdatesModel)

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    fun insertAllUPdates(updates: List<UpdatesModel>)
}