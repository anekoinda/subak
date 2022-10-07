package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class SubakResponse(
    @field:SerializedName("data")
    val data: MutableList<DataSubak>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

@Entity(tableName = "subak")
@Parcelize
data class DataSubak(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("lng")
    val lng: Double,

    @field:SerializedName("lat")
    val lat: Double,

    @field:SerializedName("nama")
    val nama: String,

    @field:SerializedName("jenis_subak")
    val jenis_subak: String
) : Parcelable
