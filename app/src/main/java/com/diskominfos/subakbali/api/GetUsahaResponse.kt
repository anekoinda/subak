package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class GetUsahaResponse(
    @field:SerializedName("data")
    val data: MutableList<GetAllUsaha>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,
)

@Entity(tableName = "usaha")
@Parcelize
data class GetAllUsaha(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @field:SerializedName("subak_id")
    val subak_id: Int,

    @field:SerializedName("jenis_usaha_id")
    val jenis_usaha_id: String,

    @field:SerializedName("jenis_usaha")
    val jenis_usaha: GetJenisUsaha,

    @field:SerializedName("jenis_pengelola_usaha_id")
    val jenis_pengelola_usaha_id: String,

    @field:SerializedName("jenis_pengelola_usaha")
    val jenis_pengelola_usaha: GetJenisPengelolaUsaha,

    @field:SerializedName("nama")
    val nama: String,

    @field:SerializedName("keterangan")
    val keterangan: String,
) : Parcelable

@Parcelize
data class GetJenisUsaha(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("nama")
    val nama: String
) : Parcelable

@Parcelize
data class GetJenisPengelolaUsaha(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("nama")
    val nama: String
) : Parcelable