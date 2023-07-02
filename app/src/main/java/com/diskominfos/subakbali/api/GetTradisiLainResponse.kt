package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class GetTradisiLainResponse(
    @field:SerializedName("data")
    val data: MutableList<GetAllDataTradisi>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,
)

@Entity(tableName = "tradisi_lain")
@Parcelize
data class GetAllDataTradisi(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @field:SerializedName("subak_id")
    val subak_id: Int,

    @field:SerializedName("nama")
    val nama: String,

    @field:SerializedName("deskripsi")
    val deskripsi: String,

    @field:SerializedName("odalan_jenis")
    val odalan_jenis: String,

    @field:SerializedName("odalan_saptawara")
    val odalan_saptawara: String,

    @field:SerializedName("odalan_pancawara")
    val odalan_pancawara: String,

    @field:SerializedName("odalan_bulan")
    val odalan_bulan: String,

    @field:SerializedName("odalan_wuku")
    val odalan_wuku: String,

    @field:SerializedName("odalan_sasih")
    val odalan_sasih: String,
) : Parcelable