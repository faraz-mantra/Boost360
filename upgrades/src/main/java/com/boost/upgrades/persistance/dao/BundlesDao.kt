package com.biz2.nowfloats.boost.updates.persistance.dao

import androidx.room.*
import com.boost.upgrades.data.model.BundlesModel
import io.reactivex.Single

@Dao
interface BundlesDao {
  @Query("SELECT * FROM Bundles")
  fun getBundleItems(): Single<List<BundlesModel>>

  @Query("SELECT * FROM Bundles WHERE bundle_id=:item_id")
  fun getBundleItemById(item_id: String): Single<BundlesModel>

  @Query("SELECT COUNT(*) from Bundles")
  fun countBundlesItems(): Single<Int>

  @Query("SELECT included_features from Bundles where bundle_id=:item_id")
  fun getIncludedKeysInBundle(item_id: String): Single<String?>

  @Query("DELETE FROM Bundles")
  fun emptyBundles()

  @Query("SELECT EXISTS(SELECT * FROM Bundles WHERE bundle_id=:item_id)")
  fun checkBundleKeyExist(item_id: String): Single<Int>

  @Insert
  fun insertToBundles(vararg features: BundlesModel?)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAllBundles(data: List<BundlesModel>)

  @Update
  fun updateBundles(vararg bundlesModel: BundlesModel?)

  @Query("DELETE FROM Bundles WHERE bundle_id=:itemId")
  fun deleteBundlesItem(vararg itemId: String)

}
