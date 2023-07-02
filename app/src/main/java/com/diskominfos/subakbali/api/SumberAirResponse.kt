package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class SumberAirResponse(
    @field:SerializedName("data")
    val data: MutableList<DataSumberAir>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

@Entity(tableName = "sumber_air")
@Parcelize
data class DataSumberAir(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("kabupaten_id")
    val kabupaten_id: String,

    @field:SerializedName("kabupaten")
    val kabupaten: DataKabupatenSumberAir? = null,

    @field:SerializedName("kecamatan_id")
    val kecamatan_id: String,

    @field:SerializedName("kecamatan")
    val kecamatan: DataKecamatanSumberAir? = null,

    @field:SerializedName("desa_adat_id")
    val desa_adat_id: String,

    @field:SerializedName("desa_dinas_id")
    val desa_dinas_id: String,

    @field:SerializedName("desa_dinas")
    val desa_dinas: DataDesaDinasSumberAir? = null,

    @field:SerializedName("nama")
    val nama: String,

    @field:SerializedName("deskripsi")
    val deskripsi: String,

) : Parcelable

@Parcelize
data class DataKabupatenSumberAir(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String,
): Parcelable

@Parcelize
data class DataKecamatanSumberAir(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String,
): Parcelable

@Parcelize
data class DataDesaDinasSumberAir(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String,
): Parcelable