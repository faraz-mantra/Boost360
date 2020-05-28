package com.biz2.nowfloats.boost.updates.persistance.dao

import androidx.room.*
import com.boost.upgrades.data.model.CartModel
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface CartDao {
    @Query("SELECT * FROM Cart")
    fun getCartItems(): Single<List<CartModel>>

    @Query("SELECT * FROM Cart WHERE boost_widget_key=:item_id")
    fun getCartItemById(item_id: String): Single<CartModel>

    @Query("SELECT COUNT(*)from Cart")
    fun countCartItems(): Int

    @Query("DELETE FROM Cart")
    fun emptyCart()

    @Insert
    fun insertToCart(vararg carts: CartModel?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllUPdates(updates: List<CartModel>)

    @Update
    fun updateCart(vararg carts: CartModel?)

    @Query("DELETE FROM Cart WHERE boost_widget_key=:itemId")
    fun deleteCartItem(vararg itemId: String)
}
