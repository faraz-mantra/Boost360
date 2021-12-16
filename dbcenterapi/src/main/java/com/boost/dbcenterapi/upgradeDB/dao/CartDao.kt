package com.boost.dbcenterapi.upgradeDB.dao

import androidx.room.*
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import io.reactivex.Single

@Dao
interface CartDao {
  @Query("SELECT * FROM Cart")
  fun getCartItems(): Single<List<CartModel>>

  @Query("SELECT * FROM Cart WHERE item_id=:item_id")
  fun getCartItemById(item_id: String): Single<CartModel>

  @Query("SELECT * FROM Cart WHERE item_id IN (:itemIds)")
  fun getCartsByIds(itemIds: List<String>): Single<List<CartModel>>

  @Query("SELECT COUNT(*)from Cart")
  fun countCartItems(): Int

  @Query("DELETE FROM Cart")
  fun emptyCart()

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertToCart(vararg carts: CartModel?)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAllUPdates(updates: List<CartModel>)

  @Update
  fun updateCart(vararg carts: CartModel?)

  @Query("DELETE FROM Cart WHERE item_id=:itemId")
  fun deleteCartItem(vararg itemId: String)

  @Query("SELECT * FROM Cart WHERE feature_code IN (:list)")
  fun getAllCartItemsInList(list: List<String>): Single<List<CartModel>>

  @Query("SELECT EXISTS(SELECT * FROM Cart WHERE boost_widget_key IS NULL )")
  fun checkCartFeatureTableKeyExist(): Single<Int>

  @Query("SELECT EXISTS(SELECT * FROM Cart WHERE item_id=:kid )")
  fun checkCartBundleExist(kid: String): Single<Int>
}
