package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class TempSubakDesaAdatResponse(
    @field:SerializedName("data")
    val data: TempSubakDesaAdat,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,
)

@Entity(tableName = "temp_subak_desa_adat")
@Parcelize
data class TempSubakDesaAdat(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @field:SerializedName("temp_subak_id")
    val temp_subak_id: Int,

    @field:SerializedName("desa_adat_id")
    val desa_adat_id: String,

    @field:SerializedName("polygon")
    val polygon: String,
) : Parcelable