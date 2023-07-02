package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class TradisiResponse(
    @field:SerializedName("data")
    val data: MutableList<DataTradisi>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

@Entity(tableName = "tradisi_lain")
@Parcelize
data class DataTradisi(
    @field:SerializedName("subak_id")
    val subak_id: Int,

    @field:SerializedName("nama")
    val nama: String,

    @field:SerializedName("deskripsi")
    val deskripsi: String
) : Parcelable
