package com.diskominfos.subakbali.ui.tambah.datalain

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.api.AlihFungsiResponse
import com.diskominfos.subakbali.api.ApiConfig
import com.diskominfos.subakbali.databinding.ActivityAddAlihFungsiBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AddAlihFungsi : AppCompatActivity() {
    private lateinit var binding: ActivityAddAlihFungsiBinding
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private var getIdSubak: String? = ""
    private var token : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAlihFungsiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Tambah Data Alih Fungsi"
        setDatePicker()

        getIdSubak = intent.getStringExtra("idsubak")
        Log.e("id add alih fungsi", "$getIdSubak")
        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnLanjutkan.setOnClickListener {
                addAlihFungsi("$getIdSubak")
                val intent = Intent(this, AlihFungsi::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
            }
        }
    }

    private fun setDatePicker() {
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "yyyy-MM-dd"// mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.btnTanggalAlihFungsi.text = sdf.format(cal.time)
            }
        binding.btnTanggalAlihFungsi.setOnClickListener {
            DatePickerDialog(
                this@AddAlihFungsi, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
            binding.btnTanggalAlihFungsi.text = SimpleDateFormat("dd-MMM-yyyy").format(System.currentTimeMillis())
        }
    }

    private fun addAlihFungsi(id: String) {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.addSubak()
        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        var getUsername: String = ""
        addDataUmumViewModel.getUsername().observe(this) { it ->
            getUsername = it
        }
        addDataUmumViewModel.getUser().observe(this) { it ->
            Log.e("token", it)
            val getNama = binding.namaAlihFungsi.text.toString()
            val nama = getNama.toRequestBody("text/plain".toMediaType())
            val getTanggal = binding.btnTanggalAlihFungsi.text.toString()
            val tanggal = getTanggal.toRequestBody("text/plain".toMediaType())
            val getLuas = binding.luasAlihFungsi.text.toString()
            val luas = getLuas.toRequestBody("text/plain".toMediaType())
            val getDeskripsi = binding.deskripsiAlihFungsi.text.toString()
            val deskripsi = getDeskripsi.toRequestBody("text/plain".toMediaType())
            val service = ApiConfig.getApiService().addAlihFungsi(
                "Bearer $it",
                getIdSubak!!.toInt(),
                luas,
                tanggal,
                nama,
                deskripsi
            )

            service.enqueue(object : Callback<AlihFungsiResponse> {
                override fun onResponse(
                    call: Call<AlihFungsiResponse>,
                    response: Response<AlihFungsiResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddAlihFungsi,
                            "Berhasil input data subak",
                            Toast.LENGTH_SHORT

                        ).show()
//                        val subak = response.body()
//                        idsubak = subak?.data?.id.toString()
//                        Log.e("idsubak", subak?.data?.id.toString())
                    } else {
                        Toast.makeText(
                            this@AddAlihFungsi,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AlihFungsiResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddAlihFungsi,
                        "Cannot instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }
}