package com.biz2.nowfloats.boost.updates.persistance.dao

import androidx.room.*
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.data.model.MarketOfferModel
import io.reactivex.Single
import kotlin.collections.ArrayList

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

    @Query("SELECT EXISTS(SELECT * FROM MarketOffers WHERE coupon_code=:couponCode )")
    fun checkOffersTableKeyExist(couponCode: String): Single<Int>

}
