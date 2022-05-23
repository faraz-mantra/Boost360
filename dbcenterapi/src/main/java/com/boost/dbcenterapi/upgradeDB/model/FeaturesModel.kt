package com.boost.dbcenterapi.upgradeDB.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.RecyclerViewItemType
import java.io.Serializable

@Entity(tableName = "Features")
data class FeaturesModel(

    @PrimaryKey
    @ColumnInfo(name = "feature_id")
    var feature_id: String,

    @ColumnInfo(name = "boost_widget_key")
    var boost_widget_key: String,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "feature_code")
    var feature_code: String? = null,

    @ColumnInfo(name = "description")
    var description: String? = null,

    @ColumnInfo(name = "description_title")
    var description_title: String? = null,

    @ColumnInfo(name = "createdon")
    var createdon: String? = null,

    @ColumnInfo(name = "updatedon")
    var updatedon: String? = null,

    @ColumnInfo(name = "websiteid")
    var websiteid: String? = null,

    @ColumnInfo(name = "isarchived")
    var isarchived: Boolean = false,

    @ColumnInfo(name = "is_premium")
    var is_premium: Boolean = false,

    @ColumnInfo(name = "target_business_usecase")
    var target_business_usecase: String? = null,

    @ColumnInfo(name = "feature_importance")
    var feature_importance: Int = 0,

    @ColumnInfo(name = "discount_percent")
    var discount_percent: Int = 0,

    @ColumnInfo(name = "price")
    var price: Double = 0.0,

    @ColumnInfo(name = "time_to_activation")
    var time_to_activation: Int? = null,

    @ColumnInfo(name = "primary_image")
    var primary_image: String? = null,

    @ColumnInfo(name = "feature_banner")
    var feature_banner: String? = null,

    @ColumnInfo(name = "secondary_images")
    var secondary_images: String? = null,

    @ColumnInfo(name = "learn_more_link")
    var learn_more_link: String? = null,

    @ColumnInfo(name = "total_installs")
    var total_installs: String? = "--",

    @ColumnInfo(name = "extended_properties")
    var extended_properties: String? = null,

    @ColumnInfo(name = "exclusive_to_categories")
    var exclusive_to_categories: String? = null,

    @ColumnInfo(name = "widget_type")
    var widget_type: String? = null,

    @ColumnInfo(name = "benefits")
    var benefits: String? = null,

    @ColumnInfo(name = "all_testimonials")
    var all_testimonials: String? = null,

    @ColumnInfo(name = "all_frequently_asked_questions")
    var all_frequently_asked_questions: String? = null,

    @ColumnInfo(name = "how_to_use_steps")
    var how_to_use_steps: String? = null,

    @ColumnInfo(name = "how_to_use_title")
    var how_to_use_title: String? = null

) : Serializable, AppBaseRecyclerViewItem {

    override fun getViewType(): Int {
        return RecyclerViewItemType.FEATURES_MODEL.ordinal

    }

    var expiryDate: String? = null
    var activatedDate: String? = null
    var status: Int? = null


}
