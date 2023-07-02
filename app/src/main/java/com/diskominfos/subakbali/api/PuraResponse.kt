package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class PuraResponse(
    @field:SerializedName("data")
    val data: MutableList<DataPura>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

@Entity(tableName = "pura")
@Parcelize
data class DataPura(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("kabupaten_id")
    val kabupaten_id: String,

    @field:SerializedName("kabupaten")
    val kabupaten: DataKabupatenPura? = null,

    @field:SerializedName("kecamatan_id")
    val kecamatan_id: String,

    @field:SerializedName("kecamatan")
    val kecamatan: DataKecamatanPura? = null,

    @field:SerializedName("desa_adat_id")
    val desa_adat_id: String,

    @field:SerializedName("desa_adat")
    val desa_adat: DataDesaAdatPura? = null,

    @field:SerializedName("desa_dinas_id")
    val desa_dinas_id: String,

    @field:SerializedName("desa_dinas")
    val desa_dinas: DataDesaDinasPura? = null,

    @field:SerializedName("nama")
    val nama: String,

    @field:SerializedName("deskripsi")
    val deskripsi: String,

    @field:SerializedName("lat")
    val lat: String,

    @field:SerializedName("lng")
    val lng: String,

    @field:SerializedName("tahun_berdiri")
    val tahun_berdiri: String,

    @field:SerializedName("nama_pemangku")
    val nama_pemangku: String,

    @field:SerializedName("telp_pemangku")
    val telp_pemangku: String
) : Parcelable

@Parcelize
data class DataKabupatenPura(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String,
): Parcelable

@Parcelize
data class DataKecamatanPura(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String,
): Parcelable

@Parcelize
data class DataDesaDinasPura(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String,
): Parcelable

@Parcelize
data class DataDesaAdatPura(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String,
): Parcelable