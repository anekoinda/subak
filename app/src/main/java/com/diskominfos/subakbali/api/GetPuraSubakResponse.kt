package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class GetPuraSubakResponse(
    @field:SerializedName("data")
    val data: MutableList<GetAllDataPuraSubak>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,
)

@Entity(tableName = "subak_pura")
@Parcelize
data class GetAllDataPuraSubak(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @field:SerializedName("pura_id")
    val pura_id: Int,

    @field:SerializedName("subak_id")
    val subak_id: Int,

    @field:SerializedName("pura")
    val pura: GetDataPura? = null
) : Parcelable

@Parcelize
data class GetDataPura(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("nama")
    val nama: String
) : Parcelable