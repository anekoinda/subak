package com.diskominfos.subakbali.ui.tambah.dokumen

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
import com.diskominfos.subakbali.api.PeraremResponse
import com.diskominfos.subakbali.api.SuratKeputusanResponse
import com.diskominfos.subakbali.databinding.ActivityAddPeraremBinding
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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AddPerarem : AppCompatActivity() {
    private lateinit var binding: ActivityAddPeraremBinding
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private var getIdSubak: String? = ""
    private val jenis = arrayListOf("Pararem Penyacah", "Pararem Pangele", "Pararem Penepas Wicara")
    private val tipe = arrayListOf("Pararem Penyacah", "Pararem Pangele", "Pararem Penepas Wicara")
    private var token : String = ""
    var jenisSelected: String? = ""
    var tipeSelected: String? = ""
    var isTextInputLayoutClicked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPeraremBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Tambah Perarem"
        setJenis()
        setTipe()

        getIdSubak = intent.getStringExtra("idsubak")
        Log.e("id add perarem", "$getIdSubak")
        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnLanjutkan.setOnClickListener {
                addPerarem("$getIdSubak")
                val intent = Intent(this, Perarem::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
            }
        }
    }

    private fun setJenis() {
        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Jenis Perarem"
        searchableSpinner.searchViewVisibility = SearchableSpinner.SpinnerView.GONE
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {
                if (isTextInputLayoutClicked)
                    jenisSelected = selectedString
                binding.textJenisPerarem.text = jenisSelected
                binding.textJenisPerarem.setTextColor(Color.BLACK)
            }
        }

        searchableSpinner.setSpinnerListItems(jenis)
        binding.textJenisPerarem.keyListener = null
        binding.textJenisPerarem.setOnClickListener {
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

    private fun setTipe() {
        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Tipe Perarem"
        searchableSpinner.searchViewVisibility = SearchableSpinner.SpinnerView.GONE
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {
                if (isTextInputLayoutClicked)
                    tipeSelected = selectedString
                binding.textTipePerarem.text = tipeSelected
                binding.textTipePerarem.setTextColor(Color.BLACK)
            }
        }

        searchableSpinner.setSpinnerListItems(jenis)
        binding.textTipePerarem.keyListener = null
        binding.textTipePerarem.setOnClickListener {
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

    private fun addPerarem(id: String) {
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
            val getJudul = binding.textJudulPerarem.text.toString()
            val judul = getJudul.toRequestBody("text/plain".toMediaType())
            val getJenis = jenisSelected.toString()
            val jenis = getJenis.toRequestBody("text/plain".toMediaType())
            val getTipe = tipeSelected.toString()
            val tipe = getTipe.toRequestBody("text/plain".toMediaType())
            val getDeskripsi = binding.textDeskripsiPerarem.text.toString()
            val deskripsi = getDeskripsi.toRequestBody("text/plain".toMediaType())
            val service = ApiConfig.getApiService().addPerarem(
                "Bearer $it",
                getIdSubak!!.toInt(),
                judul,
                tipe,
                jenis,
                deskripsi
            )

            service.enqueue(object : Callback<PeraremResponse> {
                override fun onResponse(
                    call: Call<PeraremResponse>,
                    response: Response<PeraremResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddPerarem,
                            "Berhasil input data subak",
                            Toast.LENGTH_SHORT

                        ).show()
//                        val subak = response.body()
//                        idsubak = subak?.data?.id.toString()
//                        Log.e("idsubak", subak?.data?.id.toString())
                    } else {
                        Toast.makeText(
                            this@AddPerarem,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PeraremResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddPerarem,
                        "Cannot instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }
}