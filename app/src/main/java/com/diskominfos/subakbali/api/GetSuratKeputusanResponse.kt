package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class GetSuratKeputusanResponse(
    @field:SerializedName("data")
    val data: MutableList<GetAllSuratKeputusan>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,
)

@Entity(tableName = "sk_subak")
@Parcelize
data class GetAllSuratKeputusan(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @field:SerializedName("subak_id")
    val subak_id: Int,

    @field:SerializedName("no_sk")
    val no_sk: String,

    @field:SerializedName("nama")
    val nama: String,

    @field:SerializedName("deskripsi")
    val deskripsi: String,

    @field:SerializedName("tanggal_sk")
    val tanggal_sk: String,

    @field:SerializedName("jabatan_penandatangan")
    val jabatan_penandatangan: String,

    @field:SerializedName("jenis_jabatan_penandatangan")
    val jenis_jabatan_penandatangan: String,
) : Parcelable