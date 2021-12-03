package com.boost.marketplace.infra.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.boost.marketplace.infra.db.entity.OffersModel
import io.reactivex.Single

@Dao
interface OffersDao {

    @Query("SELECT * FROM Offers")
    fun queryUpdates(): Single<List<OffersModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUpdates(updates: OffersModel)

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    fun insertAllUPdates(updates: List<OffersModel>)

    @Query("SELECT * FROM Offers WHERE id IN (:OffersKeys)")
    fun getCartItemById(OffersKeys: List<String>): Single<List<OffersModel>>
}