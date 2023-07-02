package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class JenisUsahaResponse(
    @field:SerializedName("data")
    val data: MutableList<DataJenisUsaha>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

@Entity(tableName = "jenis_usaha")
@Parcelize
data class DataJenisUsaha(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("nama")
    val nama: String,
) : Parcelable