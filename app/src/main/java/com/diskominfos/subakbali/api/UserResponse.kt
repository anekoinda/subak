package com.diskominfos.subakbali.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserResponse {
    @SerializedName("data")
    @Expose
    var data: User? = null

    class User {
        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("username")
        @Expose
        var username: String? = null

        @SerializedName("token")
        @Expose
        var token : String? = null
    }
}