package com.biz2.nowfloats.boost.updates.persistance.dao

import androidx.room.*
import com.boost.upgrades.data.model.CartModel
import com.boost.upgrades.data.model.FeaturesModel
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
}
