package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class SingleTempSubakResponse(
    @field:SerializedName("data")
    val data: TempSubak,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

//    @SerializedName("datasubak")
//    @Expose
//    var datasubak: Subak? = null,
)
//
//class Subak(
//    @SerializedName("id")
//    @Expose
//    var id : String? = null
//)

@Entity(tableName = "temp_subak")
@Parcelize
data class TempSubak(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @field:SerializedName("kabupaten_id")
    val kabupaten_id: String,

    @field:SerializedName("kabupaten")
    val kabupaten: DataKabupatenTempSubak? = null,

    @field:SerializedName("kecamatan_id")
    val kecamatan_id: String,

    @field:SerializedName("kecamatan")
    val kecamatan: DataKecamatanTempSubak? = null,

    @field:SerializedName("desa_pengampu_id")
    val desa_pengampu_id: String,

    @field:SerializedName("desa_pengampu")
    val desa_pengampu: DataDesaPengampuTempSubak? = null,

    @field:SerializedName("nama")
    val nama: String,

    @field:SerializedName("jenis_subak")
    val jenis_subak: String,

    @field:SerializedName("luas")
    val luas: String,

    @field:SerializedName("lat")
    val lat: String,

    @field:SerializedName("lng")
    val lng: String,

    @field:SerializedName("batas_utara")
    val batas_utara: String,

    @field:SerializedName("batas_selatan")
    val batas_selatan: String,

    @field:SerializedName("batas_timur")
    val batas_timur: String,

    @field:SerializedName("batas_barat")
    val batas_barat: String,
) : Parcelable