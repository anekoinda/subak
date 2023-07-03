package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class GetPeraremResponse(
    @field:SerializedName("data")
    val data: MutableList<GetAllPerarem>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,
)

@Entity(tableName = "perarem")
@Parcelize
data class GetAllPerarem(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @field:SerializedName("subak_id")
    val subak_id: Int,

    @field:SerializedName("judul")
    val judul: String,

    @field:SerializedName("jenis")
    val jenis: String,

    @field:SerializedName("tipe")
    val tipe: String,

    @field:SerializedName("deskripsi")
    val deskripsi: String,
) : Parcelable