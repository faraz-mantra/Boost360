package com.appservice.rest.services

import com.appservice.rest.EndPoints
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface LogotronBuildMyLogoRemoteData {

    @POST(EndPoints.INCLUDES_API)
    fun addLogotronClient(
        @Query("action") action: String? = "AddClient",
        @Query("username") userName: String? = "OSPQRGwatu9ObPffppfMusJy7chWqeCl",
        @Query("password") password: String? = "hcjONdkeG9URHqCeTgLi9YRj4yNxRXCs",
        @Query("firstname") firstName: String? = "John",
        @Query("lastname") lastName: String? = "Doe",
        @Query("email") email: String? = "",
        @Query("address1") address1: String? = "",
        @Query("city") city: String? = "Anytown",
        @Query("state") state: String? = "State",
        @Query("notes") notes: Int? = 6,
        @Query("postcode") postCode: Int? = 0,
        @Query("country") country: String? = "IN",
        @Query("currency") currency: Int? = 2,
        @Query("phonenumber") phoneNumber: String? = "000-000-0000",
        @Query("groupid") groupId: Int? = 2,
        @Query("password2") password2: String? = "password",
        @Query("responsetype") responseType: String? = "json",
    ): Observable<Response<ResponseBody>>

    @POST(EndPoints.INCLUDES_API)
    fun downloadLogotronImage(
        @Query("action") action: String? = "downloadimage",
        @Query("username") userName: String? = "OSPQRGwatu9ObPffppfMusJy7chWqeCl",
        @Query("password") password: String? = "hcjONdkeG9URHqCeTgLi9YRj4yNxRXCs",
        @Query("image_width") imageWidth: Int? = 400,
        @Query("image_height") imageHeight: Int? = 400,
        @Query("affiliate_id") affiliateId: Int? = 6,
        @Query("client_id") clientIdUser: Int? = 104,
        @Query("order_id") orderId: Int? = 277,
        @Query("responsetype") responseType: String? = "json",
    ): Observable<Response<ResponseBody>>
}