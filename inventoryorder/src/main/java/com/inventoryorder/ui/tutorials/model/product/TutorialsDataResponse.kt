package com.inventoryorder.ui.tutorials.model.product


import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class TutorialsDataResponse(
        @SerializedName("Data")
        var data: List<Data?>?,
        @SerializedName("Extra")
        var extra: Extra?
) : BaseResponse(), Serializable

data class Data(
        @SerializedName("createdon")
        var createdon: String?,
        @SerializedName("faqs")
        var faqs: ArrayList<Faq?>?,
        @SerializedName("hero_section")
        var heroSection: HeroSection?,
        @SerializedName("isarchived")
        var isarchived: Boolean?,
        @SerializedName("_kid")
        var kid: String?,
        @SerializedName("rootaliasurl")
        var rootaliasurl:Rootaliasurl?,
        @SerializedName("schemaid")
        var schemaid: String?,
        @SerializedName("tips")
        var tips: List<String?>?,
        @SerializedName("updatedon")
        var updatedon: String?,
        @SerializedName("userid")
        var userid: String?,
        @SerializedName("videos")
        var videos: List<Video?>?,
        @SerializedName("websiteid")
        var websiteid: String?
) : Serializable

data class Faq(
        @SerializedName("answer")
        var answer: String?,
        @SerializedName("createdon")
        var createdon: String?,
        @SerializedName("isarchived")
        var isarchived: Boolean?,
        @SerializedName("_kid")
        var kid: String?,
        @SerializedName("_parentClassId")
        var parentClassId: String?,
        @SerializedName("_parentClassName")
        var parentClassName: String?,
        @SerializedName("_propertyName")
        var propertyName: String?,
        @SerializedName("question")
        var question: String?,
        @SerializedName("updatedon")
        var updatedon: String?,
        @SerializedName("websiteid")
        var websiteid: String?
) : Serializable, AppBaseRecyclerViewItem {
        var recyclerViewType = RecyclerViewItemType.ITEM_FAQ.getLayout()
        override fun getViewType(): Int {
                return recyclerViewType

        }
}
        data class HeroSection(
                @SerializedName("createdon")
                var createdon: String?,
                @SerializedName("description")
                var description: String?,
                @SerializedName("isarchived")
                var isarchived: Boolean?,
                @SerializedName("_kid")
                var kid: String?,
                @SerializedName("_parentClassId")
                var parentClassId: String?,
                @SerializedName("_parentClassName")
                var parentClassName: String?,
                @SerializedName("_propertyName")
                var propertyName: String?,
                @SerializedName("title")
                var title: String?,
                @SerializedName("updatedon")
                var updatedon: String?,
                @SerializedName("websiteid")
                var websiteid: String?
        ) : Serializable

        data class Rootaliasurl(
                @SerializedName("createdon")
                var createdon: String?,
                @SerializedName("isarchived")
                var isarchived: Boolean?,
                @SerializedName("_kid")
                var kid: String?,
                @SerializedName("_parentClassId")
                var parentClassId: String?,
                @SerializedName("_parentClassName")
                var parentClassName: String?,
                @SerializedName("_propertyName")
                var propertyName: String?,
                @SerializedName("updatedon")
                var updatedon: String?,
                @SerializedName("url")
                var url: String?,
                @SerializedName("websiteid")
                var websiteid: String?
        ) : Serializable


        data class Link(
                @SerializedName("createdon")
                var createdon: String?,
                @SerializedName("description")
                var description: String?,
                @SerializedName("isarchived")
                var isarchived: Boolean?,
                @SerializedName("_kid")
                var kid: String?,
                @SerializedName("_parentClassId")
                var parentClassId: String?,
                @SerializedName("_parentClassName")
                var parentClassName: String?,
                @SerializedName("_propertyName")
                var propertyName: String?,
                @SerializedName("updatedon")
                var updatedon: String?,
                @SerializedName("url")
                var url: String?,
                @SerializedName("websiteid")
                var websiteid: String?
        ) : Serializable

        data class Thumbnail(
                @SerializedName("createdon")
                var createdon: String?,
                @SerializedName("isarchived")
                var isarchived: Boolean?,
                @SerializedName("_kid")
                var kid: String?,
                @SerializedName("_parentClassId")
                var parentClassId: String?,
                @SerializedName("_parentClassName")
                var parentClassName: String?,
                @SerializedName("_propertyName")
                var propertyName: String?,
                @SerializedName("updatedon")
                var updatedon: String?,
                @SerializedName("url")
                var url: String?,
                @SerializedName("websiteid")
                var websiteid: String?
        ) : Serializable


        data class Extra(
                @SerializedName("CurrentIndex")
                var currentIndex: Int?,
                @SerializedName("PageSize")
                var pageSize: Int?,
                @SerializedName("TotalCount")
                var totalCount: Int?
        ) : Serializable

        data class Video(
                @SerializedName("app")
                var app: App?,
                @SerializedName("createdon")
                var createdon: String?,
                @SerializedName("duration")
                var duration: String?,
                @SerializedName("isarchived")
                var isarchived: Boolean?,
                @SerializedName("_kid")
                var kid: String?,
                @SerializedName("link")
                var link: Link?,
                @SerializedName("_parentClassId")
                var parentClassId: String?,
                @SerializedName("_parentClassName")
                var parentClassName: String?,
                @SerializedName("_propertyName")
                var propertyName: String?,
                @SerializedName("thumbnail")
                var thumbnail: Thumbnail?,
                @SerializedName("title")
                var title: String?,
                @SerializedName("tutorial")
                var tutorial: ArrayList<String>?,
                @SerializedName("updatedon")
                var updatedon: String?,
                @SerializedName("websiteid")
                var websiteid: String?
        ) : Serializable

        data class App(
                @SerializedName("createdon")
                var createdon: String?,
                @SerializedName("isarchived")
                var isarchived: Boolean?,
                @SerializedName("_kid")
                var kid: String?,
                @SerializedName("_parentClassId")
                var parentClassId: String?,
                @SerializedName("_parentClassName")
                var parentClassName: String?,
                @SerializedName("_propertyName")
                var propertyName: String?,
                @SerializedName("updatedon")
                var updatedon: String?,
                @SerializedName("websiteid")
                var websiteid: String?
        ) : Serializable

