package com.diskominfos.subakbali.api

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("login")
    fun login(
        @Body userRequest: UserRequest
    ): Call<UserResponse>

    @GET("subak/get-all")
    fun getAllSubak(
        @Header("Authorization") token: String
    ): Call<SubakResponse>

    @GET("kabupaten/get-all")
    fun getAllKabupaten(
        @Header("Authorization") token: String
    ): Call<KabupatenResponse>

    @GET("kecamatan/get-by-kabupaten/{district_id}")
    fun getKecamatanByKabupaten(
        @Header("Authorization") token: String,
        @Path("district_id") district_id: String
    ): Call<KecamatanResponse>

    @GET("desa-dinas/get-by-kecamatan/{id}")
    fun getDesaByKecamatan(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<DesaDinasResponse>

    @GET("desa-adat/get-by-kecamatan/{id}")
    fun getDesaAdatByKecamatan(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<DesaAdatResponse>

    @Multipart
    @POST("temp_subak/create")
    fun addDataSubak(
        @Header("Authorization") token: String,
        @Part("kabupaten_id") kabupaten_id: RequestBody,
        @Part("kecamatan_id") kecamatan_id: RequestBody,
        @Part("desa_pengampu_id") desa_pengampu_id: RequestBody,
        @Part("nama") nama: RequestBody,
        @Part("jenis_subak") jenis_subak: RequestBody,
        @Part("luas") luas: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lng") lng: RequestBody,
        @Part("is_active") is_active: Int,
        @Part("batas_utara") batas_utara: RequestBody,
        @Part("batas_selatan") batas_selatan: RequestBody,
        @Part("batas_timur") batas_timur: RequestBody,
        @Part("batas_barat") batas_barat: RequestBody,
        @Part("action_status") action_status: RequestBody,
        @Part("created_by") created_by: RequestBody
    ): Call<SingleTempSubakResponse>

    @Multipart
    @POST("temp_subak_desa_adat/create")
    fun addDataTempSubakDesaAdat(
        @Header("Authorization") token: String,
        @Part("temp_subak_id") temp_subak_id: Int,
//        @Part("polygon") polygon: RequestBody,
        @Part("desa_adat_id") desa_adat_id: RequestBody,
    ): Call<TempSubakDesaAdatResponse>

    @Multipart
    @POST("temp_subak_desa_dinas/create")
    fun addDataTempSubakDesaDinas(
        @Header("Authorization") token: String,
        @Part("temp_subak_id") temp_subak_id: Int,
//        @Part("polygon") polygon: RequestBody,
        @Part("desa_dinas_id") desa_dinas_id: RequestBody,
    ): Call<TempSubakDesaDinasResponse>

    @GET("temp_subak/get-data")
    fun getDataTempSubak(
        @Header("Authorization") token: String,
        @Query("id") id: String
    ): Call<TempSubakResponse>

    @GET("temp_subak/get-data")
    fun getListTempSubak(
        @Header("Authorization") token: String
    ): Call<TempSubakResponse>

    @GET("temp_subak_desa_adat/get-data/{temp_subak_id}")
    fun getListTempSubakDesaAdat(
        @Header("Authorization") token: String,
        @Path("temp_subak_id") temp_subak_id: String,
    ): Call<GetTempSubakDesaAdatResponse>

    @GET("temp_subak_desa_dinas/get-data/{temp_subak_id}")
    fun getListTempSubakDesaDinas(
        @Header("Authorization") token: String,
        @Path("temp_subak_id") temp_subak_id: String,
    ): Call<GetTempSubakDesaDinasResponse>

    @DELETE("temp_subak/{id}/delete")
    fun deleteDataTempSubak(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<DeleteTempSubakResponse>

    @DELETE("temp_subak_desa_adat/{id}/delete")
    fun deleteDataTempSubakDesaAdat(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<DeleteTempSubakDesaAdatResponse>

    @DELETE("temp_subak_desa_dinas/{id}/delete")
    fun deleteDataTempSubakDesaDinas(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<DeleteTempSubakDesaDinasResponse>

    @Multipart
    @POST("temp_subak/{id}/update")
    fun updateDataSubak(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Part("kabupaten_id") kabupaten_id: RequestBody,
        @Part("kecamatan_id") kecamatan_id: RequestBody,
        @Part("desa_pengampu_id") desa_pengampu_id: RequestBody,
        @Part("nama") nama: RequestBody,
        @Part("jenis_subak") jenis_subak: RequestBody,
        @Part("luas") luas: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lng") lng: RequestBody,
        @Part("is_active") is_active: Int,
        @Part("batas_utara") batas_utara: RequestBody,
        @Part("batas_selatan") batas_selatan: RequestBody,
        @Part("batas_timur") batas_timur: RequestBody,
        @Part("batas_barat") batas_barat: RequestBody,
        @Part("action_status") action_status: RequestBody,
        @Part("updated_by") updated_by: RequestBody
    ): Call<DataSubakResponse>

    @Multipart
    @POST("temp_subak/{id}/update")
    fun updateDataSubakPolygon(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Part("polygon") polygon: RequestBody,
    ): Call<DataSubakResponse>

    @GET("desa-adat/get-all")
    fun getDesaAdat(
        @Header("Authorization") token: String
    ): Call<DesaAdatResponse>

    @GET("desa-dinas/get-all")
    fun getDesaDinas(
        @Header("Authorization") token: String
    ): Call<DesaDinasResponse>

    @GET("tempekan/get-all")
    fun getTempekan(
        @Header("Authorization") token: String
    ): Call<TempekanResponse>

    @Multipart
    @POST("tempekan/create")
    fun addTempekan(
        @Header("Authorization") token: String,
//        @Part("subak_id") subak_id: RequestBody,
//        @Part("prejuru_id") prejuru_id: RequestBody,
        @Part("nama") nama: RequestBody,
        @Part("is_active") is_active: Int
    ): Call<TempekanResponse>

    @GET("pura/get-all")
    fun getPura(
        @Header("Authorization") token: String
    ): Call<PuraResponse>

    @GET("subak_pura/get-data/{subak_id}")
    fun getListPuraSubak(
        @Header("Authorization") token: String,
        @Path("subak_id") subak_id: String,
    ): Call<GetPuraSubakResponse>

    @Multipart
    @POST("pura/create")
    fun addDataPura(
        @Header("Authorization") token: String,
        @Part("id") id: Int,
        @Part("kabupaten_id") kabupaten_id: RequestBody,
        @Part("kecamatan_id") kecamatan_id: RequestBody,
        @Part("desa_dinas_id") desa_dinas_id: RequestBody,
        @Part("desa_adat_id") desa_adat_id: RequestBody,
        @Part("nama") nama: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("tahun_berdiri") tahun_berdiri: RequestBody,
        @Part("nama_pemangku") nama_pemangku: RequestBody,
        @Part("telp_pemangku") telp_pemangku: RequestBody,
        @Part("lng") lng: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("odalan_jenis") odalan_jenis: RequestBody,
        @Part("odalan_saptawara") odalan_saptawara: RequestBody,
        @Part("odalan_pancawara") odalan_pancawara: RequestBody,
        @Part("odalan_bulan") odalan_bulan: RequestBody,
        @Part("odalan_wuku") odalan_wuku: RequestBody,
        @Part("odalan_sasih") odalan_sasih: RequestBody,
        @Part("odalan_keterangan") odalan_keterangan: RequestBody,
        @Part("verified_status") verified_status: RequestBody,
        @Part("is_active") is_active: RequestBody,
    ): Call<PuraResponse>

    @Multipart
    @POST("subak_pura/create")
    fun addDataSubakPura(
        @Header("Authorization") token: String,
        @Part("id") id: Int,
        @Part("subak_id") subak_id: Int,
        @Part("pura_id") pura_id: RequestBody,
    ): Call<SubakPuraResponse>

    @DELETE("subak_pura/{id}/delete")
    fun deleteDataSubakPura(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<DeleteSubakPuraResponse>

    @Multipart
    @POST("pura/{id}/update")
    fun updatePura(
        @Header("Authorization") token: String,
        @Path("id") id: String,
//        @Part("kabupaten_id") kabupaten_id: RequestBody,
//        @Part("kecamatan_id") kecamatan_id: RequestBody,
//        @Part("desa_dinas_id") desa_dinas_id: RequestBody,
//        @Part("desa_adat_id") desa_adat_id: RequestBody,
//        @Part("nama") nama: RequestBody,
//        @Part("deskripsi") deskripsi: RequestBody,
//        @Part("tahun_berdiri") tahun_berdiri: RequestBody,
//        @Part("nama_pemangku") nama_pemangku: RequestBody,
//        @Part("telp_pemangku") telp_pemangku: RequestBody,
        @Part("odalan_jenis") odalan_jenis: RequestBody,
        @Part("odalan_saptwara") odalan_saptwara: RequestBody,
        @Part("odalan_bulan") odalan_bulan: RequestBody,
        @Part("odalan_wuku") odalan_wuku: RequestBody,
        @Part("odalan_sasih") odalan_sasih: RequestBody,
//        @Part("polygon") polygon: RequestBody,
    ): Call<PuraResponse>

    @Multipart
    @POST("tradisi_lain/create")
    fun addDataTradisi(
        @Header("Authorization") token: String,
        @Part("subak_id") subak_id: Int,
        @Part("nama") nama: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("odalan_jenis") odalan_jenis: RequestBody,
        @Part("odalan_saptawara") odalan_saptawara: RequestBody,
        @Part("odalan_pancawara") odalan_pancawara: RequestBody,
        @Part("odalan_bulan") odalan_bulan: RequestBody,
        @Part("odalan_wuku") odalan_wuku: RequestBody,
        @Part("odalan_sasih") odalan_sasih: RequestBody,
        @Part("is_active") is_active: Int,
    ): Call<TradisiResponse>

    @GET("tradisi_lain/get-data/{subak_id}")
    fun getListTradisi(
        @Header("Authorization") token: String,
        @Path("subak_id") subak_id: String,
    ): Call<GetTradisiLainResponse>

    @GET("tradisi_lain/get-detail")
    fun getDataTradisi(
        @Header("Authorization") token: String,
        @Query("id") id: String
    ): Call<GetTradisiLainResponse>

    @Multipart
    @POST("tradisi_lain/{id}/update")
    fun updateDataTradisi(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Part("subak_id") subak_id: RequestBody,
        @Part("nama") nama: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("odalan_jenis") odalan_jenis: RequestBody,
        @Part("odalan_saptawara") odalan_saptawara: RequestBody,
        @Part("odalan_pancawara") odalan_pancawara: RequestBody,
        @Part("odalan_bulan") odalan_bulan: RequestBody,
        @Part("odalan_wuku") odalan_wuku: RequestBody,
        @Part("odalan_sasih") odalan_sasih: Int,
        @Part("is_active") is_active: RequestBody,
        @Part("created_by") created_by: RequestBody
    ): Call<TradisiResponse>

    @DELETE("tradisi_lain/{id}/delete")
    fun deleteDataTradisiSubak(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<TradisiResponse>

    @Multipart
    @POST("sumber_dana/create")
    fun addDataSumberDana(
        @Header("Authorization") token: String,
        @Part("subak_id") subak_id: Int,
        @Part("jenis_sumber_dana_id") jenis_sumber_dana_id: RequestBody,
        @Part("nominal") nominal: RequestBody,
        @Part("tahun") tahun: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody
    ): Call<SumberDanaResponse>

    @GET("sumber_dana/get-data/{subak_id}")
    fun getListSumberDana(
        @Header("Authorization") token: String,
        @Path("subak_id") subak_id: String,
    ): Call<GetSumberDanaResponse>

    @GET("jenis-sumber-dana/get-all")
    fun getAllJenisSumberDana(
        @Header("Authorization") token: String
    ): Call<JenisSumberDanaResponse>

    @GET("sumber_dana/get-detail")
    fun getDataSumberDana(
        @Header("Authorization") token: String,
        @Query("id") id: String
    ): Call<GetSumberDanaResponse>

    @DELETE("sumber_dana/{id}/delete")
    fun deleteDataSumberDana(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<GetSumberDanaResponse>

    @Multipart
    @POST("sumber_dana/{id}/update")
    fun updateSumberDana(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Part("subak_id") subak_id: RequestBody,
        @Part("jenis_sumber_dana_id") jenis_sumber_dana_id: RequestBody,
        @Part("nominal") nominal: RequestBody,
        @Part("tahun") tahun: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody
    ): Call<GetSumberDanaResponse>

    @Multipart
    @POST("sumber_air/create")
    fun addDataSumberAir(
        @Header("Authorization") token: String,
        @Part("id") id: Int,
        @Part("kabupaten_id") kabupaten_id: RequestBody,
        @Part("kecamatan_id") kecamatan_id: RequestBody,
        @Part("desa_dinas_id") desa_dinas_id: RequestBody,
        @Part("nama") nama: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("verified_status") verified_status: RequestBody,
        @Part("is_active") is_active: RequestBody,
    ): Call<SumberAirResponse>

    @GET("sumber_air/get-all")
    fun getSumberAir(
        @Header("Authorization") token: String
    ): Call<SumberAirResponse>

    @GET("sumber_air/get-data/{subak_id}")
    fun getListSumberAir(
        @Header("Authorization") token: String,
        @Path("subak_id") subak_id: String,
    ): Call<SumberAirResponse>

    @Multipart
    @POST("subak_sumber_air/create")
    fun addDataSubakSumberAir(
        @Header("Authorization") token: String,
        @Part("subak_id") subak_id: Int,
        @Part("sumber_air_id") pura_id: RequestBody,
        @Part("panjang_saluran") panjang_saluran: RequestBody,
        @Part("satuan_panjang") satuan_panjang: RequestBody,
        @Part("debit_air") debit_air: RequestBody,
        @Part("satuan_debit") satuan_debit: RequestBody,
        @Part("is_active") is_active: Int,
    ): Call<SubakSumberAirResponse>

    @GET("jenis-produk/get-all")
    fun getAllJenisProduk(
        @Header("Authorization") token: String
    ): Call<JenisProdukResponse>

    @Multipart
    @POST("produk/create")
    fun addDataProduk(
        @Header("Authorization") token: String,
        @Part("subak_id") subak_id: Int,
        @Part("is_active") is_active: Int,
        @Part("jenis_produk_id") jenis_produk_id: RequestBody,
        @Part("nama") nama: RequestBody,
    ): Call<ProdukResponse>

    @GET("produk/get-data/{subak_id}")
    fun getListProduk(
        @Header("Authorization") token: String,
        @Path("subak_id") subak_id: String,
    ): Call<GetProdukResponse>

    @GET("jenis-usaha/get-all")
    fun getAllJenisUsaha(
        @Header("Authorization") token: String
    ): Call<JenisUsahaResponse>

    @GET("jenis-pengelola-usaha/get-all")
    fun getAllJenisPengelolaUsaha(
        @Header("Authorization") token: String
    ): Call<JenisPengelolaUsahaResponse>

    @Multipart
    @POST("usaha/create")
    fun addDataUsaha(
        @Header("Authorization") token: String,
        @Part("subak_id") subak_id: Int,
        @Part("nama") nama: RequestBody,
        @Part("jenis_usaha_id") jenis_usaha_id: RequestBody,
        @Part("jenis_pengelola_id") jenis_pengelola_id: RequestBody,
        @Part("keterangan") keterangan: RequestBody,
    ): Call<UsahaResponse>

    @GET("usaha/get-data/{subak_id}")
    fun getListUsaha(
        @Header("Authorization") token: String,
        @Path("subak_id") subak_id: String,
    ): Call<GetUsahaResponse>

    @Multipart
    @POST("alih_fungsi/create")
    fun addAlihFungsi(
        @Header("Authorization") token: String,
        @Part("subak_id") subak_id: Int,
        @Part("luas") luas: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part("nama_alih_lahan") nama_alih_lahan: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
    ): Call<AlihFungsiResponse>

    @GET("alih_fungsi/get-data/{subak_id}")
    fun getListAlihFungsi(
        @Header("Authorization") token: String,
        @Path("subak_id") subak_id: String,
    ): Call<GetAlihFungsiResponse>

    @Multipart
    @POST("surat_keputusan/create")
    fun addSuratKeputusan(
        @Header("Authorization") token: String,
        @Part("subak_id") subak_id: Int,
        @Part("no_sk") no_sk: RequestBody,
        @Part("nama") nama: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part("jabatan_penandatangan") jabatan_penandatangan: RequestBody,
        @Part("jenis_jabatan_penandatangan") jenis_jabatan_penandatangan: RequestBody,
    ): Call<SuratKeputusanResponse>

    @GET("surat_keputusan/get-data/{subak_id}")
    fun getListSuratKeputusan(
        @Header("Authorization") token: String,
        @Path("subak_id") subak_id: String,
    ): Call<GetSuratKeputusanResponse>

    @Multipart
    @POST("awig/create")
    fun addAwig(
        @Header("Authorization") token: String,
        @Part("subak_id") subak_id: Int,
        @Part("judul") judul: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("jenis") jenis: RequestBody,
        @Part("tanggal_pengesahan") tanggal_pengesahan: RequestBody,
        @Part("tanggal_berlaku") tanggal_berlaku: RequestBody,
        @Part("tanggal_berhenti_berlaku") tanggal_berhenti_berlaku: RequestBody,
    ): Call<AwigResponse>

    @GET("awig/get-data/{subak_id}")
    fun getListAwig(
        @Header("Authorization") token: String,
        @Path("subak_id") subak_id: String,
    ): Call<GetAwigResponse>

    @Multipart
    @POST("perarem/create")
    fun addPerarem(
        @Header("Authorization") token: String,
        @Part("subak_id") subak_id: Int,
        @Part("judul") judul: RequestBody,
        @Part("tipe") tipe: RequestBody,
        @Part("jenis") jenis: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
    ): Call<PeraremResponse>

    @GET("perarem/get-data/{subak_id}")
    fun getListPerarem(
        @Header("Authorization") token: String,
        @Path("subak_id") subak_id: String,
    ): Call<GetPeraremResponse>
}
