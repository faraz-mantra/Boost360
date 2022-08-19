package com.boost.dbcenterapi.upgradeDB.dao

import androidx.room.*
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import io.reactivex.Single

@Dao
interface FeaturesDao {

  @Query("SELECT * FROM Features")
  fun getAllFeatures(): Single<List<FeaturesModel>>

  @Query("SELECT * FROM Features WHERE is_premium = :premiumType ORDER BY feature_importance DESC")
  fun getFeaturesItems(premiumType: Boolean): Single<List<FeaturesModel>>

  //    @Query("SELECT * FROM Features WHERE target_business_usecase = :categoryType  ORDER BY feature_importance DESC")
  @Query("SELECT * FROM Features WHERE target_business_usecase = :categoryType AND feature_code != :excludeFeature AND is_premium = :premiumType ORDER BY feature_importance DESC")
  fun getFeaturesItemsByTypePremium(
    categoryType: String,
    excludeFeature: String,
    premiumType: Boolean
  ): Single<List<FeaturesModel>>

  @Query("SELECT * FROM Features WHERE target_business_usecase = :categoryType AND feature_code != :excludeFeature ORDER BY feature_importance DESC")
  fun getFeaturesItemsByType(
    categoryType: String,
    excludeFeature: String
  ): Single<List<FeaturesModel>>

  @Query("SELECT * FROM Features WHERE feature_id=:item_id")
  fun getFeaturesItemById(item_id: String): Single<FeaturesModel>

  @Query("SELECT * FROM Features WHERE boost_widget_key = :widgetKey")
  fun getFeaturesItemByWidgetKey(widgetKey: String): Single<FeaturesModel>

  @Query("SELECT * FROM Features WHERE feature_code=:featureCode")
  fun getFeaturesItemByFeatureCode(featureCode: String): Single<FeaturesModel>

  @Query("SELECT * FROM Features WHERE feature_code=:featureCode AND is_premium = :premiumType")
  fun getFeaturesItemByFeatureCode1(featureCode: String,premiumType: Boolean): Single<FeaturesModel>

  @Query("SELECT EXISTS(SELECT * FROM Features)")
  fun checkEmptyFeatureTable(): Single<Int>

  @Query("SELECT EXISTS(SELECT * FROM Features WHERE feature_code=:featureCode )")
  fun checkFeatureTableKeyExist(featureCode: String): Single<Int>

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
//    @Query("SELECT * FROM Features Where is_premium = :premiumType AND feature_code IN (:list)")
  @Query("SELECT * FROM Features Where is_premium = :premiumType AND boost_widget_key IN (:list)")
  fun getallActiveFeatures(list: List<String>, premiumType: Boolean): Single<List<FeaturesModel>>

  @Query("SELECT * FROM Features Where feature_code IN (:list)")
  fun getallActiveFeatures1(list: List<String>): Single<List<FeaturesModel>>

  @Query("DELETE FROM Features WHERE boost_widget_key=:itemId")
  fun deleteFeaturesItem(vararg itemId: String)

  @Query("SELECT COUNT(*) FROM Features Where boost_widget_key IN (:list)")
//    @Query("SELECT COUNT(*) FROM Features Where feature_code IN (:list)")
  fun getallActivefeatureCount(list: List<String>): Single<Int>

  @Query("SELECT COUNT(*) FROM Features Where target_business_usecase = :featureType AND feature_code != :excludeFeature AND is_premium = :premiumType")
  fun getFeatureTypeCountPremium(featureType: String, excludeFeature: String, premiumType: Boolean): Single<Int>

  @Query("SELECT COUNT(*) FROM Features Where target_business_usecase = :featureType AND feature_code != :excludeFeature")
  fun getFeatureTypeCount(featureType: String, excludeFeature: String): Single<Int>

  @Query("SELECT * FROM Features Where feature_code IN (:list) ORDER BY is_premium DESC, feature_importance DESC")
  fun getallFeaturesInList(list: List<String>): Single<List<FeaturesModel>>

  @Query("SELECT * FROM Features Where feature_code IN (:featureCode) ")
  fun getSpecificFeature(featureCode: List<String>): Single<List<FeaturesModel>>


  @Query("SELECT * FROM Features Where target_business_usecase= :business_usecase AND feature_code IN (:featureCodes) ")
  fun getFeatureListTargetBusiness(
    business_usecase: String?,
    featureCodes: ArrayList<String?>
  ): Single<List<FeaturesModel>>

  @Query("SELECT * FROM Features Where feature_code IN (:featureCodes) AND target_business_usecase= :business_usecase ")
  fun getFeatureListFeature(
    featureCodes: ArrayList<String?>,
    business_usecase: String?
  ): Single<List<FeaturesModel>>

  @Query("SELECT * FROM Features Where feature_code IN (:featureCode) ")
  fun getSpecificFeatureTwo(featureCode: ArrayList<String?>): Single<List<FeaturesModel>>

  @Query("SELECT * FROM Features Where feature_code IN (:featureCodes) AND target_business_usecase IN (:business_usecase) ")
  fun getFeatureListFeatureNew(
    featureCodes: ArrayList<String>,
    business_usecase: MutableList<String?>
  ): Single<List<FeaturesModel>>

  @Query("SELECT * FROM Features Where feature_code IN (:featureCodes) ")
  fun getFeatureListForCompare(featureCodes: ArrayList<String?>): Single<List<FeaturesModel>>

}
