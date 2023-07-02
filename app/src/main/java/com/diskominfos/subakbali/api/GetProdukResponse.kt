package com.diskominfos.subakbali.api

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class GetProdukResponse(
    @field:SerializedName("data")
    val data: MutableList<GetAllProduk>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,
)

@Entity(tableName = "produk")
@Parcelize
data class GetAllProduk(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @field:SerializedName("subak_id")
    val subak_id: Int,

    @field:SerializedName("nama")
    val nama: String,

    @field:SerializedName("jenis_produk_id")
    val jenis_produk_id: GetJenisProduk
) : Parcelable

@Parcelize
data class GetJenisProduk(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("nama")
    val nama: String
) : Parcelable