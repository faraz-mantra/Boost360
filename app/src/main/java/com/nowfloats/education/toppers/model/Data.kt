package com.nowfloats.education.toppers.model

import com.nowfloats.education.model.Profileimage
import com.nowfloats.education.model.Testimonialimage

data class Data(
        var ActionId: String = "",
        var CreatedOn: String = "",
        var IsArchived: Boolean = false,
        var UpdatedOn: String = "",
        var UserId: String = "",
        var WebsiteId: String = "",
        var _id: String = "",
        var coursecategory: String = "",
        var name: String = "",
        var profileimage: Profileimage = Profileimage(),
        var programavailed: String = "",
        var rank: String = "",
        var testimonialimage: Testimonialimage = Testimonialimage(),
        var testimonialtext: String = ""
)