package com.nowfloats.education.faculty.model

import com.nowfloats.education.model.Profileimage

data class Data(
        var ActionId: String = "",
        var CreatedOn: String = "",
        var IsArchived: Boolean = false,
        var UpdatedOn: String = "",
        var UserId: String = "",
        var WebsiteId: String = "",
        var _id: String = "",
        var description: String = "",
        var name: String = "",
        var profileimage: Profileimage = Profileimage(),
        var title: String = ""
)