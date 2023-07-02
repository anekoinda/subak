package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class DesaAdatResponse(
    @field:SerializedName("data")
    val data: MutableList<DataDesaAdat>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

@Entity(tableName = "desaadat")
@Parcelize
data class DataDesaAdat(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("nama")
    val nama: String,

    @field:SerializedName("kecamatan_id")
    val kecamatan_id: String,

    @field:SerializedName("sub_district")
    val sub_district: DataKecamatanDesaAdat? = null,

    @field:SerializedName("kabupaten_id")
    val kabupaten_id: String,

    @field:SerializedName("district")
    val district: DataKabupatenDesaAdat? = null
) : Parcelable

@Parcelize
data class DataKabupatenDesaAdat(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String
) : Parcelable

@Parcelize
data class DataKecamatanDesaAdat(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String
) : Parcelable