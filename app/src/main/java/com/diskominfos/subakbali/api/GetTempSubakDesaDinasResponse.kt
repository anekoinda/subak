package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class GetTempSubakDesaDinasResponse(
    @field:SerializedName("data")
    val data: MutableList<GetAllDataTempSubakDesaDinas>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,
)

@Entity(tableName = "temp_subak_desa_dinas")
@Parcelize
data class GetAllDataTempSubakDesaDinas(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @field:SerializedName("temp_subak_id")
    val temp_subak_id: Int,

    @field:SerializedName("desa_dinas_id")
    val desa_dinas_id: String,

    @field:SerializedName("polygon")
    val polygon: String,

    @field:SerializedName("desa_dinas")
    val desa_dinas: GetDataDesaDinas? = null
) : Parcelable

@Parcelize
data class GetDataDesaDinas(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String
) : Parcelable