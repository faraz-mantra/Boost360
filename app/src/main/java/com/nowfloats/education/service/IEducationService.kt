package com.nowfloats.education.service

import com.nowfloats.education.batches.model.AddUpcomingBatchModel
import com.nowfloats.education.batches.model.UpdateUpcomingBatchModel
import com.nowfloats.education.faculty.model.AddFacultyModel
import com.nowfloats.education.faculty.model.UpdateFacultyModel
import com.nowfloats.education.model.DeleteModel
import com.nowfloats.education.model.OurFacultyResponse
import com.nowfloats.education.model.OurTopperResponse
import com.nowfloats.education.model.UpcomingBatchesResponse
import com.nowfloats.education.toppers.model.AddTopperModel
import com.nowfloats.education.toppers.model.UpdateTopperModel
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

interface IEducationService {

    @GET("upcoming_batches/get-data")
    fun getUpcomingBatches(@Header("Authorization") auth: String?,
                           @Query("query") websiteId: String,
                           @Query("limit") limit: Int,
                           @Query("skip") skip: Int): Observable<UpcomingBatchesResponse>


    @GET("our_faculty/get-data")
    fun getOurFaculty(@Header("Authorization") auth: String?,
                      @Query("query") websiteId: String,
                      @Query("limit") limit: Int,
                      @Query("skip") skip: Int): Observable<OurFacultyResponse>


    @GET("our_toppers/get-data")
    fun getOurToppers(@Header("Authorization") auth: String?,
                      @Query("query") websiteId: String,
                      @Query("limit") limit: Int,
                      @Query("skip") skip: Int): Observable<OurTopperResponse>

    @POST("upcoming_batches/add-data")
    fun addUpcomingBatches(@Header("Authorization") auth: String?,
                           @Body addUpcomingBatchModel: AddUpcomingBatchModel): Observable<String>

    @POST("upcoming_batches/update-data")
    fun updateUpcomingBatch(@Header("Authorization") auth: String?,
                            @Body updateUpcomingBatchModel: UpdateUpcomingBatchModel): Observable<String>

    @POST("upcoming_batches/update-data")
    fun deleteUpcomingBatch(@Header("Authorization") auth: String?,
                            @Body deleteModel: DeleteModel): Observable<String>

    @POST("our_toppers/add-data")
    fun addOurTopper(@Header("Authorization") auth: String?,
                     @Body addTopperModel: AddTopperModel): Observable<String>

    @POST("our_toppers/update-data")
    fun updateOurTopper(@Header("Authorization") auth: String?,
                        @Body updateTopperModel: UpdateTopperModel): Observable<String>

    @POST("our_toppers/update-data")
    fun deleteOurTopper(@Header("Authorization") auth: String?,
                        @Body deleteModel: DeleteModel): Observable<String>

    @POST("our_faculty/add-data")
    fun addOurFaculty(@Header("Authorization") auth: String?,
                      @Body addFacultyModel: AddFacultyModel): Observable<String>

    @POST("our_faculty/update-data")
    fun updateOurFaculty(@Header("Authorization") auth: String?,
                         @Body updateFacultyModel: UpdateFacultyModel): Observable<String>

    @POST("our_faculty/update-data")
    fun deleteOurFaculty(@Header("Authorization") auth: String?,
                         @Body deleteModel: DeleteModel): Observable<String>

    @Multipart
    @POST("events/upload-file")
    fun getImageUrl(@Header("Authorization") auth: String?,
                    @Query("assetFileName") assetFileName: String,
                    @Part file: MultipartBody.Part): Observable<String>
}