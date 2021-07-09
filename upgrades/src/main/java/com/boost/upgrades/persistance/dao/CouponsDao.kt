package com.biz2.nowfloats.boost.updates.persistance.dao

import androidx.room.*
import com.boost.upgrades.data.model.CouponsModel
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface CouponsDao {
    @Query("SELECT * FROM Coupons")
    fun getCouponItems(): Single<List<CouponsModel>>

    @Query("SELECT * FROM Coupons WHERE coupon_key=:item_id")
    fun getCouponItemById(item_id: String): Single<CouponsModel>

    @Query("SELECT COUNT(*) from Coupons")
    fun countCouponsItems(): Single<Int>

    @Query("DELETE FROM Coupons")
    fun emptyCoupons()

    @Insert
    fun insertToCoupons(vararg features: CouponsModel?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCoupons(data: List<CouponsModel>)

    @Update
    fun updateCoupons(vararg CouponsModel: CouponsModel?)

    @Query("DELETE FROM Coupons WHERE coupon_key=:itemId")
    fun deleteCouponsItem(vararg itemId: String)

}
