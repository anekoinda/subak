package com.diskominfos.subakbali.api

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("login")
    fun login(
        @Body userRequest: UserRequest
    ): Call<UserResponse>

    @GET("subak/get-all")
    fun getAllSubak(
        @Header("Authorization") token: String
    ): Call<SubakResponse>
}