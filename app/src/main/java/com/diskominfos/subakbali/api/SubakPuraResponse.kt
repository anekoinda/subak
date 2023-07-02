package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class SubakPuraResponse(
    @field:SerializedName("data")
    val data: MutableList<DataSubakPura>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

@Entity(tableName = "subak_pura")
@Parcelize
data class DataSubakPura(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("subak_id")
    val subak_id: String,

    @field:SerializedName("pura_id")
    val pura_id: String
) : Parcelable