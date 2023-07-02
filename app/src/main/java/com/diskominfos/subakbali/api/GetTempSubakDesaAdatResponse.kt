package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class GetTempSubakDesaAdatResponse(
    @field:SerializedName("data")
    val data: MutableList<GetAllDataTempSubakDesaAdat>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,
)

@Entity(tableName = "temp_subak_desa_adat")
@Parcelize
data class GetAllDataTempSubakDesaAdat(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @field:SerializedName("temp_subak_id")
    val temp_subak_id: Int,

    @field:SerializedName("desa_adat_id")
    val desa_adat_id: String,

    @field:SerializedName("polygon")
    val polygon: String,

    @field:SerializedName("desa_adat")
    val desa_adat: GetDataDesaAdat? = null
) : Parcelable

@Parcelize
data class GetDataDesaAdat(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("nama")
    val nama: String
) : Parcelable