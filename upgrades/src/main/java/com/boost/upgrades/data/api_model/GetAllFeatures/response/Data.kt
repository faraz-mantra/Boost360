package com.boost.upgrades.data.api_model.GetAllFeatures.response

import com.google.gson.annotations.SerializedName

data class Data(
        @SerializedName("_kid")
        val _kid: String,
        @SerializedName("createdon")
        val createdon: String,
        @SerializedName("feature_deals")
        val feature_deals: List<FeatureDeals>,
        @SerializedName("features")
        val features: List<Feature>,
        @SerializedName("bundles")
        val bundles: List<Bundles>,
        @SerializedName("discount_coupons")
        val discount_coupons: List<DiscountCoupons>,
        @SerializedName("promo_banners")
        val promo_banners: List<PromoBanners>,
        @SerializedName("partner_zone")
        val partner_zone: List<PartnerZone>,
        @SerializedName("isarchived")
        val isarchived: Boolean,
        @SerializedName("rootaliasurl")
        val rootaliasurl: Rootaliasurl,
        @SerializedName("schemaid")
        val schemaid: String,
        @SerializedName("updatedon")
        val updatedon: String,
        @SerializedName("userid")
        val userid: String,
        @SerializedName("websiteid")
        val websiteid: String,
        @SerializedName("expert_connect")
        val expert_connect: ExpertConnect,
        @SerializedName("video_gallery")
        val video_gallery: List<VideoGallery>,
        @SerializedName("feedback_link")
        val feedback_link: String,
        @SerializedName("_propertyName")
        val propertyName: String
)