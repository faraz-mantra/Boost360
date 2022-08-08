package com.festive.poster.reset.repo



import com.festive.poster.base.rest.AppBaseLocalService
import com.festive.poster.base.rest.AppBaseRepository
import com.festive.poster.models.response.GetTemplatesBody
import com.festive.poster.models.response.GetTodaysTemplateBody
import com.festive.poster.models.response.TemplateSaveActionBody
import com.festive.poster.reset.TaskCode
import com.festive.poster.reset.apiClients.NowFloatsApiClient
import com.festive.poster.reset.services.NowFloatsRemoteData
import com.framework.base.BaseResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Retrofit


object NowFloatsRepository : AppBaseRepository<NowFloatsRemoteData, AppBaseLocalService>() {

  fun getTemplates(floatingPointId: String?,floatingPointTag: String?,tags:List<String>?): Observable<BaseResponse> {

    val body = JsonObject().apply {
      addProperty("floatingPointId",floatingPointId)
      addProperty("floatingPointTag",floatingPointTag)
      add("tags", Gson().toJsonTree(tags).asJsonArray)
      addProperty("showFavourites",false)
    }
    return makeRemoteRequest(
      remoteDataSource.getTemplates(body),
      TaskCode.GET_TEMPLATES
    )
  }

  fun getFavTemplates(floatingPointId: String?,floatingPointTag: String?,featureKey:String): Observable<BaseResponse> {

    val body = JsonObject().apply {
      addProperty("floatingPointId",floatingPointId)
      addProperty("floatingPointTag",floatingPointTag)
      addProperty("featureKey",featureKey)
    }
    return makeRemoteRequest(
      remoteDataSource.getFavTemp(body),
      TaskCode.GET_FAV_TEMPLATES
    )
  }

  fun makeTempFav(floatingPointId: String?,floatingPointTag: String?,templateId: String?): Observable<BaseResponse> {

    val body = JsonObject().apply {
      addProperty("floatingPointId",floatingPointId)
      addProperty("floatingPointTag",floatingPointTag)
      addProperty("templateId",templateId)
    }
    return makeRemoteRequest(
      remoteDataSource.favPoster(body),
      TaskCode.GET_TEMPLATES
    )
  }

  fun getTemplateConfig(fKey:String,floatingPointId: String?,floatingPointTag: String?): Observable<BaseResponse> {
    val body =JsonObject().apply {
      addProperty("floatingPointId",floatingPointId)
      addProperty("floatingPointTag",floatingPointTag)
      addProperty("featureKey",fKey)
    }
    return makeRemoteRequest(
      remoteDataSource.getTemplateViewConfig(body),
      TaskCode.GET_TEMPLATE_CONFIG
    )
  }

  fun uploadProfileImage(floatingPointId: String?,floatingPointTag: String?,fileName:String,file: RequestBody?): Observable<BaseResponse> {

    return makeRemoteRequest(
      remoteDataSource.uploadImage(floatingPointId,floatingPointTag,fileName,file),
      TaskCode.UPLOAD_IMAGE
    )
  }

  fun saveKeyValue(floatingPointId: String?,fpTag: String?,templateId:String,map:HashMap<String,String?>): Observable<BaseResponse> {
    val body =JsonObject().apply {
      addProperty("floatingPointId",floatingPointId)
      addProperty("floatingPointTag",fpTag)
      addProperty("templateId",templateId)
      add("keyDetails",Gson().toJsonTree(map).asJsonObject)
    }
    return makeRemoteRequest(
      remoteDataSource.saveKeyValue(body),
      TaskCode.SAVE_KEY_VALUES
    )
  }

  fun updatePurchaseStatus(floatingPointId: String?,fpTag: String?,posterTag:String?,templateIds:List<String>): Observable<BaseResponse> {
    val body =JsonObject().apply {
      addProperty("floatingPointId",floatingPointId)
      addProperty("floatingPointTag",fpTag)
      add("templateIds",Gson().toJsonTree(templateIds).asJsonArray)
      addProperty("tag",posterTag)
      addProperty("isPurchased",true)
    }
    return makeRemoteRequest(
      remoteDataSource.updatePurchaseStatus(body),
      TaskCode.SAVE_KEY_VALUES
    )
  }

  fun getCategories(): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getCategories(),
      TaskCode.GET_CATEGORIES
    )
  }

  fun getTodaysTemplates(): Observable<BaseResponse> {
    val body =GetTodaysTemplateBody(session.fPID,
      session.fpTag)
    return makeRemoteRequest(
      remoteDataSource.getTodayTemplates(body),
      TaskCode.GET_TODAY_TEMPLATES
    )
  }

  fun getTemplates(isFav: Boolean?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getTemplates(GetTemplatesBody(session.fPID!!,session.fpTag!!, showFavourites = isFav)),
      TaskCode.GET_TEMPLATES_V2
    )
  }

  fun saveTemplateAction(action:TemplateSaveActionBody.ActionType,
  isFav:Boolean,templateId:String): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.templateSaveAction(
        TemplateSaveActionBody(
        action = action.name, favourite = isFav, floatingPointId = session.fPID!!,
          floatingPointTag = session.fpTag!!,templateId=templateId
      )
      ),
      TaskCode.GET_TEMPLATES_V2
    )
  }


  override fun getRemoteDataSourceClass(): Class<NowFloatsRemoteData> {
    return NowFloatsRemoteData::class.java
  }



  override fun getApiClient(): Retrofit {
    return NowFloatsApiClient.shared.retrofit
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }
}
