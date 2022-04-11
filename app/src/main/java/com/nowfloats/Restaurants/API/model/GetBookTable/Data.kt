package com.nowfloats.Restaurants.API.model.GetBookTable

data class Data(
  val ActionId: String? = null,
  val CreatedOn: String? = null,
  val IsArchived: Boolean? = null,
  val UpdatedOn: String,
  val UserId: String? = null,
  val WebsiteId: String? = null,
  val _id: String? = null,
  val date: String? = null,
  val message: String? = null,
  val name: String? = null,
  val number: String? = null,
  val time: String? = null,
  val totalPeople: String? = null
){

  fun getTotalPeopleN():String{
    return if (totalPeople.isNullOrEmpty() || totalPeople.equals("null",true)) "0" else totalPeople
  }
}