package com.diskominfos.subakbali.api

import com.google.gson.annotations.SerializedName

class DeleteSubakPuraResponse (
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("message")
    val message: String
)