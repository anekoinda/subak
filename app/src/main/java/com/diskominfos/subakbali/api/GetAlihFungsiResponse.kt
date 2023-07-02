package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class GetAlihFungsiResponse(
    @field:SerializedName("data")
    val data: MutableList<GetAllAlihFungsi>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,
)

@Entity(tableName = "alih_fungsi")
@Parcelize
data class GetAllAlihFungsi(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @field:SerializedName("subak_id")
    val subak_id: Int,

    @field:SerializedName("luas")
    val luas: String,

    @field:SerializedName("tanggal")
    val tanggal: String,

    @field:SerializedName("nama_alih_lahan")
    val nama_alih_lahan: String,

    @field:SerializedName("deskripsi")
    val deskripsi: String,
) : Parcelable
