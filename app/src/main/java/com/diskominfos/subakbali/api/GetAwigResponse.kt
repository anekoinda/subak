package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class GetAwigResponse(
    @field:SerializedName("data")
    val data: MutableList<GetAllAwig>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,
)

@Entity(tableName = "awig")
@Parcelize
data class GetAllAwig(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @field:SerializedName("subak_id")
    val subak_id: Int,

    @field:SerializedName("judul")
    val judul: String,

    @field:SerializedName("jenis")
    val jenis: String,

    @field:SerializedName("deskripsi")
    val deskripsi: String,

    @field:SerializedName("tanggal_pengesahan")
    val tanggal_pengesahan: String,

    @field:SerializedName("tanggal_berhenti_berlaku")
    val tanggal_berhenti_berlaku: String,

    @field:SerializedName("tanggal_berlaku")
    val tanggal_berlaku: String,
) : Parcelable