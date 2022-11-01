package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class DesaDinasResponse(
    @field:SerializedName("data")
    val data: MutableList<DataDesaDinas>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

@Entity(tableName = "desadinas")
@Parcelize
data class DataDesaDinas(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("sub_district_id")
    val sub_district_id: String,

    @field:SerializedName("district_id")
    val district_id: String,
) : Parcelable