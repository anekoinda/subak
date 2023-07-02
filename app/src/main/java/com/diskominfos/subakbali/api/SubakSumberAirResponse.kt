package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class SubakSumberAirResponse(
    @field:SerializedName("data")
    val data: MutableList<DataSubakSumberAir>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

@Entity(tableName = "sumber_air_subak")
@Parcelize
data class DataSubakSumberAir(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("subak_id")
    val subak_id: String,

    @field:SerializedName("sumber_air_id")
    val sumber_air_id: String,

    @field:SerializedName("panjang_saluran")
    val panjang_saluran: String,

    @field:SerializedName("satuan_panjang")
    val satuan_panjang: String,

    @field:SerializedName("debit_air")
    val debit_air: String,

    @field:SerializedName("satuan_debit")
    val satuan_debit: String,

    @field:SerializedName("is_active")
    val is_active: String
) : Parcelable