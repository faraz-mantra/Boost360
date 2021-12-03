package com.onboarding.nowfloats.model.supportVideo

import com.framework.base.BaseResponse
import com.framework.utils.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val SUPPORT_VIDEO_DATA = "SUPPORT_VIDEO_DATA"

data class FeatureSupportVideoResponse(
	@field:SerializedName("Extra")
	val extra: Extra? = null,

	@field:SerializedName("Data")
	val data: List<DataItem?>? = null
):Serializable, BaseResponse(){

	companion object {
		fun saveSupportVideoData(user: List<FeaturevideoItem>?) {
			PreferencesUtils.instance.saveData(SUPPORT_VIDEO_DATA, convertListObjToString(user?.toList() ?: ArrayList()) ?: "")
		}

		fun getSupportVideoData(): List<FeaturevideoItem>? {
			return convertStringToList(PreferencesUtils.instance.getData(SUPPORT_VIDEO_DATA, "") ?: "")
		}
	}
}

data class RootAliasUrl(

	@field:SerializedName("_parentClassId")
	val parentClassId: String? = null,

	@field:SerializedName("websiteid")
	val websiteid: String? = null,

	@field:SerializedName("_parentClassName")
	val parentClassName: String? = null,

	@field:SerializedName("_propertyName")
	val propertyName: String? = null,

	@field:SerializedName("_kid")
	val kid: String? = null,

	@field:SerializedName("updatedon")
	val updatedon: String? = null,

	@field:SerializedName("isarchived")
	val isarchived: Boolean? = null,

	@field:SerializedName("createdon")
	val createdon: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)

data class Extra(

	@field:SerializedName("TotalCount")
	val totalCount: Int? = null,

	@field:SerializedName("PageSize")
	val pageSize: Int? = null,

	@field:SerializedName("CurrentIndex")
	val currentIndex: Int? = null
)

data class DataItem(

	@field:SerializedName("websiteid")
	val websiteid: String? = null,

	@field:SerializedName("schemaid")
	val schemaid: String? = null,

	@field:SerializedName("featurevideo")
	val featurevideo: List<FeaturevideoItem>? = null,

	@field:SerializedName("_kid")
	val kid: String? = null,

	@field:SerializedName("updatedon")
	val updatedon: String? = null,

	@field:SerializedName("isarchived")
	val isarchived: Boolean? = null,

	@field:SerializedName("userid")
	val userid: String? = null,

	@field:SerializedName("createdon")
	val createdon: String? = null,

	@field:SerializedName("rootaliasurl")
	val rootAliasUrl: RootAliasUrl? = null
)

data class FeaturevideoItem(

	@field:SerializedName("_parentClassId")
	val parentClassId: String? = null,

	@field:SerializedName("videodurationseconds")
	val videodurationseconds: Double? = null,

	@field:SerializedName("_parentClassName")
	val parentClassName: String? = null,

	@field:SerializedName("importance")
	val importance: Double? = null,

	@field:SerializedName("relatedfeatures")
	val relatedfeatures: List<String>? = null,

	@field:SerializedName("videotitle")
	val videotitle: String? = null,

	@field:SerializedName("videodescription")
	val videodescription: String? = null,

	@field:SerializedName("_propertyName")
	val propertyName: String? = null,

	@field:SerializedName("updatedon")
	val updatedon: String? = null,

	@field:SerializedName("_kid")
	val kid: String? = null,

	@field:SerializedName("platformid")
	val platformid: String? = null,

	@field:SerializedName("createdon")
	val createdon: String? = null,

	@field:SerializedName("websiteid")
	val websiteid: String? = null,

	@field:SerializedName("videotype")
	val videotype: String? = null,

	@field:SerializedName("helpsectionidentifier")
	val helpsectionidentifier: String? = null,

	@field:SerializedName("videourl")
	val videourl: Videourl? = null,

	@field:SerializedName("categories")
	val categories: List<Any>? = null,

	@field:SerializedName("videolanguage")
	val videolanguage: String? = null,

	@field:SerializedName("isarchived")
	val isarchived: Boolean? = null,

	@field:SerializedName("videoorientation")
	val videoorientation: String? = null,

	@field:SerializedName("platformminversion")
	val platformminversion: String? = null,

	@field:SerializedName("isenabled")
	val isenabled: Boolean? = null
)

data class Videourl(

	@field:SerializedName("_parentClassId")
	val parentClassId: String? = null,

	@field:SerializedName("websiteid")
	val websiteid: String? = null,

	@field:SerializedName("_parentClassName")
	val parentClassName: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("_propertyName")
	val propertyName: String? = null,

	@field:SerializedName("updatedon")
	val updatedon: String? = null,

	@field:SerializedName("_kid")
	val kid: String? = null,

	@field:SerializedName("isarchived")
	val isarchived: Boolean? = null,

	@field:SerializedName("createdon")
	val createdon: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)
