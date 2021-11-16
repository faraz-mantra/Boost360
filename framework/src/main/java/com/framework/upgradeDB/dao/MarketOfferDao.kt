package com.framework.upgradeDB.dao

import androidx.room.*
import com.framework.upgradeDB.model.MarketOfferModel
import io.reactivex.Single

@Dao
interface MarketOfferDao {

    @Query("SELECT * FROM MarketOffers")
    fun getAllMarketOffers(): Single<List<MarketOfferModel>>

    @Query("SELECT * FROM MarketOffers WHERE coupon_code=:couponCode")
    fun getMarketOffersByCouponCode(couponCode: String): Single<MarketOfferModel>

    @Query("SELECT * FROM MarketOffers WHERE _kid=:id")
    fun getMarketOffersById(id: String): Single<MarketOfferModel>

    @Insert
    fun insertToMarketOffers(vararg features: MarketOfferModel?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMarketOffers(data: List<MarketOfferModel>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAllMarketOffers(vararg data: MarketOfferModel)

    @Query("SELECT EXISTS(SELECT * FROM MarketOffers WHERE coupon_code=:couponCode )")
    fun checkOffersTableKeyExist(couponCode: String): Single<Int>

    @Query("SELECT EXISTS(SELECT * FROM MarketOffers WHERE _kid =:id )")
    fun checkOffersIDExist(id: String): Single<Int>

    @Query("DELETE FROM MarketOffers")
    fun emptyMarketOffers()

}
