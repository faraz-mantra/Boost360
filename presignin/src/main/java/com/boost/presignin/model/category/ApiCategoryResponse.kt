package com.boost.presignin.model.category

import com.framework.base.BaseResponse

data class ApiCategoryResponse(
    val Data: List<ApiCategoryResponseData>,
): BaseResponse()

data class ApiCategoryResponseData(
    val _kid: String,
    val categories: List<ApiCategoryResponseCategory>,
    val createdon: String,
    val isarchived: Boolean,
    val rootaliasurl: ApiCategoryResponseRootaliasurl,
    val schemaid: String,
    val updatedon: String,
    val userid: String,
    val websiteid: String
)

data class ApiCategoryResponseCategory(
    val _kid: String,
    val _parentClassId: String,
    val _parentClassName: String,
    val _propertyName: String,
    val appexperiencecodedetails: List<Appexperiencecodedetail>,
    val appexperiencecodes: List<String>,
    val createdon: String,
    val isarchived: Boolean,
    val name: String,
    val updatedon: String,
    val websiteid: String
)

data class ApiCategoryResponseRootaliasurl(
    val _kid: String,
    val _parentClassId: String,
    val _parentClassName: String,
    val _propertyName: String,
    val createdon: String,
    val isarchived: Boolean,
    val updatedon: String,
    val url: String,
    val websiteid: String
)

data class ApiCategoryResponsePreview(
    val _kid: String,
    val _parentClassId: String,
    val _parentClassName: String,
    val _propertyName: String,
    val createdon: String,
    val isarchived: Boolean,
    val updatedon: String,
    val url: String,
    val websiteid: String
)

data class Appexperiencecodedetail(
    val _kid: String,
    val _parentClassId: String,
    val _parentClassName: String,
    val _propertyName: String,
    val createdon: String,
    val desktoppreview: ApiCategoryResponsePreview,
    val isarchived: Boolean,
    val mobilepreview: ApiCategoryResponsePreview,
    val name: String,
    val updatedon: String,
    val websiteid: String
)
