package com.diskominfos.subakbali.ui.tambah.dokumen

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.api.ApiConfig
import com.diskominfos.subakbali.api.AwigResponse
import com.diskominfos.subakbali.api.SuratKeputusanResponse
import com.diskominfos.subakbali.databinding.ActivityAddAwigBinding
import com.diskominfos.subakbali.databinding.ActivityAddSuratKeputusanBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import com.leo.searchablespinner.SearchableSpinner
import com.leo.searchablespinner.interfaces.OnItemSelectListener
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AddAwig : AppCompatActivity() {
    private lateinit var binding: ActivityAddAwigBinding
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private var getIdSubak: String? = ""
    private val jenis = arrayListOf("Dedosaan", "Kerampang")
    private var token: String = ""
    var jenisSelected: String? = ""
    var isTextInputLayoutClicked: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAwigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Tambah Awig-Awig"
        setJenis()

        getIdSubak = intent.getStringExtra("idsubak")
        Log.e("id add awig", "$getIdSubak")
        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnLanjutkan.setOnClickListener {
                addAwig("$getIdSubak")
                val intent = Intent(this, Awig::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
            }
        }
    }

    private fun setJenis() {
        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Jenis Awig"
        searchableSpinner.searchViewVisibility = SearchableSpinner.SpinnerView.GONE
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {
                if (isTextInputLayoutClicked)
                    jenisSelected = selectedString
                binding.textJenisAwig.text = jenisSelected
                binding.textJenisAwig.setTextColor(Color.BLACK)
            }
        }

        searchableSpinner.setSpinnerListItems(jenis)
        binding.textJenisAwig.keyListener = null
        binding.textJenisAwig.setOnClickListener {
            isTextInputLayoutClicked = true
            searchableSpinner.show()
        }

        binding.editTextSpinner.keyListener = null
        binding.editTextSpinner.setOnClickListener {
            searchableSpinner.highlightSelectedItem = false
            isTextInputLayoutClicked = false
            searchableSpinner.show()
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
                binding.btnTanggalPengesahan.text = sdf.format(cal.time)
                binding.btnTanggalBerlaku.text = sdf.format(cal.time)
                binding.btnTanggalBerhentiBerlaku.text = sdf.format(cal.time)
            }

        binding.btnTanggalPengesahan.setOnClickListener {
            DatePickerDialog(
                this@AddAwig, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
            binding.btnTanggalPengesahan.text =
                SimpleDateFormat("dd-MMM-yyyy").format(System.currentTimeMillis())
        }

        binding.btnTanggalBerlaku.setOnClickListener {
            DatePickerDialog(
                this@AddAwig, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
            binding.btnTanggalBerlaku.text =
                SimpleDateFormat("dd-MMM-yyyy").format(System.currentTimeMillis())
        }

        binding.btnTanggalBerhentiBerlaku.setOnClickListener {
            DatePickerDialog(
                this@AddAwig, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
            binding.btnTanggalBerhentiBerlaku.text =
                SimpleDateFormat("dd-MMM-yyyy").format(System.currentTimeMillis())
        }
    }

    private fun addAwig(id: String) {
        addDataUmumViewModel.addSubak()
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        var getUsername: String = ""
        addDataUmumViewModel.getUsername().observe(this) { it ->
            getUsername = it
        }
        addDataUmumViewModel.getUser().observe(this) { it ->
            Log.e("token", it)
            val getJudul = binding.textJudulAwig.text.toString()
            val judul = getJudul.toRequestBody("text/plain".toMediaType())
            val getDeskripsi = binding.textDeskripsiAwig.text.toString()
            val deskripsi = getDeskripsi.toRequestBody("text/plain".toMediaType())
            val getJenis = jenisSelected.toString()
            val jenis = getJudul.toRequestBody("text/plain".toMediaType())
            val getTanggalPengesahan = binding.btnTanggalPengesahan.text.toString()
            val tanggal_pengesahan = getTanggalPengesahan.toRequestBody("text/plain".toMediaType())
            val getTanggalBerlaku = binding.btnTanggalBerlaku.text.toString()
            val tanggal_berlaku = getTanggalBerlaku.toRequestBody("text/plain".toMediaType())
            val getTanggalBerhentiBerlaku = binding.btnTanggalBerhentiBerlaku.text.toString()
            val tanggal_berhenti_berlaku = getTanggalBerhentiBerlaku.toRequestBody("text/plain".toMediaType())
            val service = ApiConfig.getApiService().addAwig(
                "Bearer $it",
                getIdSubak!!.toInt(),
                judul,
                deskripsi,
                jenis,
                tanggal_pengesahan,
                tanggal_berlaku,
                tanggal_berhenti_berlaku
            )

            service.enqueue(object : Callback<AwigResponse> {
                override fun onResponse(
                    call: Call<AwigResponse>,
                    response: Response<AwigResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddAwig,
                            "Berhasil input data subak",
                            Toast.LENGTH_SHORT

                        ).show()
//                        val subak = response.body()
//                        idsubak = subak?.data?.id.toString()
//                        Log.e("idsubak", subak?.data?.id.toString())
                    } else {
                        Toast.makeText(
                            this@AddAwig,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AwigResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddAwig,
                        "Cannot instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }
}