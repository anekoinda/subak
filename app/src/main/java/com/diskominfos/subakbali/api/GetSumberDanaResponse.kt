package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class GetSumberDanaResponse(
    @field:SerializedName("data")
    val data: MutableList<GetAllSumberDana>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,
)

@Entity(tableName = "sumber_dana")
@Parcelize
data class GetAllSumberDana(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @field:SerializedName("subak_id")
    val subak_id: Int,

    @field:SerializedName("jenis_sumber_dana_id")
    val jenis_sumber_dana_id: String,

    @field:SerializedName("jenis_sumber_dana")
    val jenis_sumber_dana: GetJenisSumberDana,

    @field:SerializedName("nominal")
    val nominal: String,

    @field:SerializedName("tahun")
    val tahun: String,

    @field:SerializedName("deskripsi")
    val deskripsi: String,
) : Parcelable

@Parcelize
data class GetJenisSumberDana(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("nama")
    val nama: String
) : Parcelable