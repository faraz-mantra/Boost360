package com.nowfloats.Restaurants.API

import com.nowfloats.Restaurants.API.model.AddBookTable.AddBookTableData
import com.nowfloats.Restaurants.API.model.DeleteBookTable.DeleteBookTableData
import com.nowfloats.Restaurants.API.model.GetBookTable.GetBookTableData
import com.nowfloats.Restaurants.API.model.UpdateBookTable.UpdateBookTableData
import org.json.JSONObject
import retrofit.Callback
import retrofit.http.*

interface RestaurantsAPIInterfaces {

  @Headers("X-Auth-Version: 2", "X-User-Id: 5ad9c3d480f2510538ebde38", "Content-Type: application/json")
  @GET("/api/v1/ordernow/get-data")
  fun getBookTable(
    @Query("query") query: JSONObject?,
    @Query("skip") skip: Int,
    @Query("limit") limit: Int,
    response: Callback<GetBookTableData?>?
  )


  @Headers("X-Auth-Version: 2", "X-User-Id: 5ad9c3d480f2510538ebde38", "Content-Type: application/json")
  @POST("/api/v1/ordernow/add-data")
  fun addBookTable(@Body body: AddBookTableData?, response: Callback<String?>?)

  @Headers("X-Auth-Version: 2", "X-User-Id: 5ad9c3d480f2510538ebde38", "Content-Type: application/json")
  @POST("/api/v1/ordernow/update-data")
  fun updateBookTable(@Body body: UpdateBookTableData?, response: Callback<String?>?)

  @Headers("X-Auth-Version: 2", "X-User-Id: 5ad9c3d480f2510538ebde38", "Content-Type: application/json")
  @POST("/api/v1/ordernow/update-data")
  fun deleteBookTable(@Body body: DeleteBookTableData?, response: Callback<String?>?)

}