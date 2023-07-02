package com.diskominfos.subakbali.ui.tambah.parahyangan

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.api.ApiConfig
import com.diskominfos.subakbali.api.DataSubakResponse
import com.diskominfos.subakbali.databinding.ActivityEditTempSubakBinding
import com.diskominfos.subakbali.databinding.ActivityEditTradisiBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.data.DataViewModel
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class EditTradisi : AppCompatActivity() {
    private lateinit var binding: ActivityEditTradisiBinding
    private lateinit var dataViewModel: DataViewModel
    var id: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTradisiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val id = bundle.getString("id")
            getData("$id")

            binding.btnSimpan.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Edit Draf Subak")
                    .setMessage("Apakah anda yakin ingin mengedit draf subak ini?")
                    .setCancelable(true)
                    .setPositiveButton("Iya") { _, _ ->
//                        simpanData("$id")
                        Log.e("data id", "$id")
                    }.setNegativeButton("Batal") { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }.show()
            }
            binding.btnBatal.setOnClickListener {
                finish()
            }
        }
    }

    fun getData(id: String) {
        dataViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[DataViewModel::class.java]
        dataViewModel.getUser().observe(this) { tradisiSubak ->
            dataViewModel.getTradisi(tradisiSubak, id)
            dataViewModel.tradisiSubakList.observe(this) {
                binding.apply {
                    val data = it[0]
                    val id = data.id
                    namaTradisi.setText(data.nama)
                    deskripsiPura.setText(data.deskripsi)
                    odalanJenis.setText(data.odalan_jenis)
                    odalanSaptawara.setText(data.odalan_saptawara)
                    odalanPancawara.setText(data.odalan_pancawara)
                    odalanBulan.setText(data.odalan_bulan)
                    odalanWuku.setText(data.odalan_wuku)
                    odalanSasih.setText(data.odalan_sasih)
                }
            }
        }
    }

//    fun simpanData(id: String) {
//        getViewModel()
//        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
//        var getUsername: String = ""
//        addDataUmumViewModel.getUsername().observe(this) { it ->
//            getUsername = it
//        }
//        addDataUmumViewModel.getUser().observe(this) { it ->
//            Log.e("data id", "$id")
//            Log.e("token", it)
//            val getName = binding.namaSubak.text.toString()
//            val name = getName.toRequestBody("text/plain".toMediaType())
//            val getLuas = binding.luasSubak.text.toString()
//            val luas = getLuas.toRequestBody("text/plain".toMediaType())
//            val getBatasTimur = binding.batasTimurSubak.text.toString()
//            val batas_timur = getBatasTimur.toRequestBody("text/plain".toMediaType())
//            val getBatasBarat = binding.batasBaratSubak.text.toString()
//            val batas_barat = getBatasBarat.toRequestBody("text/plain".toMediaType())
//            val getBatasUtara = binding.batasUtaraSubak.text.toString()
//            val batas_utara = getBatasUtara.toRequestBody("text/plain".toMediaType())
//            val getBatasSelatan = binding.batasSelatanSubak.text.toString()
//            val batas_selatan = getBatasSelatan.toRequestBody("text/plain".toMediaType())
//
//            var kabupaten_id = ""
//            if (idKabupatenSelected == "") {
//                kabupaten_id = kab_id
//            } else {
//                val getKabupatenId = idKabupatenSelected.toString()
//                kabupaten_id = getKabupatenId
//            }
//
//            var kecamatan_id = ""
//            if (idKecamatanSelected == "") {
//                kecamatan_id = kec_id
//            } else {
//                val getKecamatanId = idKecamatanSelected.toString()
//                kecamatan_id = getKecamatanId
//            }
//
//            var desa_pengampu_id = ""
//            if (idDesaSelected == "") {
//                desa_pengampu_id = desa_id
//            } else {
//                val getDesaId = idDesaSelected.toString()
//                desa_pengampu_id = getDesaId
//            }
//
//            var jenis_subak = ""
//            if (jenisSubakSelected == "") {
//                jenis_subak = jenis
//            } else {
//                val getJenis = jenisSubakSelected.toString()
//                jenis_subak = getJenis
//            }
//
//            var lng = ""
//            if (longitude == "") {
//                lng = getLng
//            } else {
//                lng = longitude.toString()
//            }
//
//            var lat = ""
//            if (latitude == "") {
//                lat = getLat
//            } else {
//                lat = latitude.toString()
//            }
//
//            val action_status = actionStatus.toRequestBody("text/plain".toMediaType())
//
//            val updated_by = getUsername.toRequestBody("text/plain".toMediaType())
//            Log.e("updated by", "$updated_by")
//
//            val service = ApiConfig.getApiService().updateDataSubak(
//                "Bearer $it",
//                id,
//                kabupaten_id.toRequestBody("text/plain".toMediaType()),
//                kecamatan_id.toRequestBody("text/plain".toMediaType()),
//                desa_pengampu_id.toRequestBody("text/plain".toMediaType()),
//                name,
//                jenis_subak.toRequestBody("text/plain".toMediaType()),
//                luas,
//                lat.toRequestBody("text/plain".toMediaType()),
//                lng.toRequestBody("text/plain".toMediaType()),
//                1,
//                batas_utara,
//                batas_selatan,
//                batas_timur,
//                batas_barat,
//                action_status,
//                updated_by
//            )
//
//            service.enqueue(object : Callback<DataSubakResponse> {
//                override fun onResponse(
//                    call: Call<DataSubakResponse>,
//                    response: Response<DataSubakResponse>
//                ) {
//                    Log.e("token", it)
//                    if (response.isSuccessful) {
//                        Toast.makeText(
//                            this@EditTempSubak,
//                            "Berhasil input data subak",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        val responseBody = response.body()
//                        if (responseBody != null && !responseBody.error) {
//                            Toast.makeText(
//                                this@EditTempSubak,
//                                responseBody.message,
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    } else {
//                        Toast.makeText(
//                            this@EditTempSubak,
//                            response.message(),
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<DataSubakResponse>, t: Throwable) {
//                    Toast.makeText(
//                        this@EditTempSubak,
//                        "Cannot instance Retrofit",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            })
//        }
//    }
}