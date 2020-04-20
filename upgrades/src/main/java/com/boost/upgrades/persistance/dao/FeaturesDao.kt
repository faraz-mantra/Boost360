package com.biz2.nowfloats.boost.updates.persistance.dao

import androidx.room.*
import com.boost.upgrades.data.api_model.GetAllFeatures.response.GetAllFeaturesResponse
import com.boost.upgrades.data.model.FeaturesModel
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface FeaturesDao {
    @Query("SELECT * FROM Features WHERE is_premium = :premiumType ORDER BY feature_importance DESC")
    fun getFeaturesItems(premiumType: Boolean): Single<List<FeaturesModel>>

    @Query("SELECT * FROM Features WHERE boost_widget_key=:item_id")
    fun getFeaturesItemById(item_id: String): Single<FeaturesModel>


    //true for premium type
    //false for non-premium type
    @Query("SELECT COUNT(*) from Features WHERE is_premium = :premiumType")
    fun countFeaturesItems(premiumType: Boolean): Single<Int>

    @Query("DELETE FROM Features")
    fun emptyFeatures()

    @Insert
    fun insertToFeatures(vararg features: FeaturesModel?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllFeatures(data: List<FeaturesModel>)

    @Update
    fun updateFeatures(vararg featuress: FeaturesModel?)

//    @Query("SELECT * FROM Features Where boost_widget_key IN (:list) AND is_premium = :premiumType ORDER BY feature_importance DESC")
    @Query("SELECT * FROM Features Where is_premium = :premiumType AND boost_widget_key IN (:list)")
    fun getallActiveFeatures(list: List<String>, premiumType: Boolean): Single<List<FeaturesModel>>

    @Query("DELETE FROM Features WHERE boost_widget_key=:itemId")
    fun deleteFeaturesItem(vararg itemId: String)

    @Query("SELECT COUNT(*) FROM Features Where boost_widget_key IN (:list)")
    fun getallActivefeatureCount(list: List<String>): Single<Int>

    @Query("SELECT * FROM Features Where boost_widget_key IN (:list)")
    fun getallFeaturesInList(list: List<String>): Single<List<FeaturesModel>>


}
