package com.diskominfos.subakbali.api

import android.widget.AdapterView
import androidx.annotation.Nullable
import okhttp3.RequestBody
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

    @GET("kabupaten/get-all")
    fun getAllKabupaten(
        @Header("Authorization") token: String
    ): Call<KabupatenResponse>

    @GET("kecamatan/get-by-kabupaten/{district_id}")
    fun getKecamatanByKabupaten(
        @Header("Authorization") token: String,
        @Path("district_id") district_id: String
    ): Call<KecamatanResponse>

    @GET("desa-dinas/get-by-kecamatan/{id}")
    fun getDesaByKecamatan(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<DesaDinasResponse>

    @Multipart
    @POST("temp_subak/create")
    fun addDataSubak(
        @Header("Authorization") token: String,
        @Part("kabupaten_id") kabupaten_id: RequestBody,
        @Part("kecamatan_id") kecamatan_id: RequestBody,
        @Part("desa_pengampu_id") desa_pengampu_id: RequestBody,
        @Part("nama") nama: RequestBody,
        @Part("jenis_subak") jenis_subak: RequestBody,
        @Part("luas") luas: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lng") lng: RequestBody,
        @Part("is_active") is_active: Int,
        @Part("batas_utara") batas_utara: RequestBody,
        @Part("batas_selatan") batas_selatan: RequestBody,
        @Part("batas_timur") batas_timur: RequestBody,
        @Part("batas_barat") batas_barat: RequestBody,
        @Part("action_status") action_status: RequestBody,
        @Part("created_by") created_by: RequestBody
//        @Part("polygon") polygon: RequestBody,
    ): Call<DataSubakResponse>
}