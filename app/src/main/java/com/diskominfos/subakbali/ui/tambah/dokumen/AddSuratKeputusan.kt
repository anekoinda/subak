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
import com.diskominfos.subakbali.api.ApiConfig
import com.diskominfos.subakbali.api.ProdukResponse
import com.diskominfos.subakbali.api.SuratKeputusanResponse
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
class AddSuratKeputusan : AppCompatActivity() {
    private lateinit var binding: ActivityAddSuratKeputusanBinding
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private var getIdSubak: String? = ""
    private val jabatan = arrayListOf("Kepala Divisi Pengelola Subak", "Kepala Divisi Pertanian")
    private val jenisJabatan = arrayListOf("Sekretariat Daerah", "Sekretariat Provinsi")
    private var token : String = ""
    var jenisJabatanSelected: String? = ""
    var jabatanSelected: String? = ""
    var isTextInputLayoutClicked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSuratKeputusanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Tambah Surat Keputusan"
        setJenisJabatan()
        setJabatan()

        getIdSubak = intent.getStringExtra("idsubak")
        Log.e("id add produk", "$getIdSubak")
        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnLanjutkan.setOnClickListener {
                addSuratKeputusan("$getIdSubak")
                val intent = Intent(this, SuratKeputusan::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
            }
        }
    }

    private fun setJenisJabatan() {
        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Jenis Jabatan"
        searchableSpinner.searchViewVisibility = SearchableSpinner.SpinnerView.GONE
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {
                if (isTextInputLayoutClicked)
                    jenisJabatanSelected = selectedString
                binding.textJenisJabatan.text = jenisJabatanSelected
                binding.textJenisJabatan.setTextColor(Color.BLACK)
            }
        }

        searchableSpinner.setSpinnerListItems(jenisJabatan)
        binding.textJenisJabatan.keyListener = null
        binding.textJenisJabatan.setOnClickListener {
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

    private fun setJabatan() {
        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Jabatan"
        searchableSpinner.searchViewVisibility = SearchableSpinner.SpinnerView.GONE
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {
                if (isTextInputLayoutClicked)
                    jabatanSelected = selectedString
                binding.textJabatan.text = jabatanSelected
                binding.textJabatan.setTextColor(Color.BLACK)
            }
        }

        searchableSpinner.setSpinnerListItems(jabatan)
        binding.textJabatan.keyListener = null
        binding.textJabatan.setOnClickListener {
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
                binding.btnTanggalSurat.text = sdf.format(cal.time)
            }
        binding.btnTanggalSurat.setOnClickListener {
            DatePickerDialog(
                this@AddSuratKeputusan, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
            binding.btnTanggalSurat.text = SimpleDateFormat("dd-MMM-yyyy").format(System.currentTimeMillis())
        }
    }

    private fun addSuratKeputusan(id: String) {
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
            val getNomor = binding.textNomor.text.toString()
            val nomor = getNomor.toRequestBody("text/plain".toMediaType())
            val getNama = binding.textNama.text.toString()
            val nama = getNama.toRequestBody("text/plain".toMediaType())
            val getDeskripsi = binding.textDeskripsi.text.toString()
            val deskripsi = getDeskripsi.toRequestBody("text/plain".toMediaType())
            val getJabatan = jabatanSelected.toString()
            val jabatan = getJabatan.toRequestBody("text/plain".toMediaType())
            val getJenisJabatan = jenisJabatanSelected.toString()
            val jenis_jabatan = getJenisJabatan.toRequestBody("text/plain".toMediaType())
            val getTanggal = binding.btnTanggalSurat.text.toString()
            val tanggal = getTanggal.toRequestBody("text/plain".toMediaType())
            val service = ApiConfig.getApiService().addSuratKeputusan(
                "Bearer $it",
                getIdSubak!!.toInt(),
                nomor,
                nama,
                deskripsi,
                tanggal,
                jabatan,
                jenis_jabatan
            )

            service.enqueue(object : Callback<SuratKeputusanResponse> {
                override fun onResponse(
                    call: Call<SuratKeputusanResponse>,
                    response: Response<SuratKeputusanResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddSuratKeputusan,
                            "Berhasil input data subak",
                            Toast.LENGTH_SHORT

                        ).show()
//                        val subak = response.body()
//                        idsubak = subak?.data?.id.toString()
//                        Log.e("idsubak", subak?.data?.id.toString())
                    } else {
                        Toast.makeText(
                            this@AddSuratKeputusan,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<SuratKeputusanResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddSuratKeputusan,
                        "Cannot instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }
}