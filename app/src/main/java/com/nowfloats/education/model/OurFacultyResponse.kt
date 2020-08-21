package com.nowfloats.education.model

import com.nowfloats.education.faculty.model.Data

data class OurFacultyResponse(
        val Data: List<Data>,
        val Extra: Extra
)