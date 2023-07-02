package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class TempSubakDesaDinasResponse(
    @field:SerializedName("data")
    val data: TempSubakDesaDinas,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,
)

@Entity(tableName = "temp_subak_dinas_dinas")
@Parcelize
data class TempSubakDesaDinas(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @field:SerializedName("temp_subak_id")
    val temp_subak_id: Int,

    @field:SerializedName("desa_dinas_id")
    val desa_dinas_id: String,

    @field:SerializedName("polygon")
    val polygon: String,
) : Parcelable