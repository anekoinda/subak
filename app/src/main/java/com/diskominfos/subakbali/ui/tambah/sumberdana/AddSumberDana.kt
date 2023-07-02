package com.diskominfos.subakbali.ui.tambah.sumberdana

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.databinding.ActivityAddDataUmumBinding
import com.diskominfos.subakbali.databinding.ActivityAddSumberDanaBinding
import com.diskominfos.subakbali.databinding.ActivitySumberDanaBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import com.diskominfos.subakbali.ui.tambah.datawilayah.AddDataWilayah
import com.leo.searchablespinner.SearchableSpinner
import com.leo.searchablespinner.interfaces.OnItemSelectListener
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddSumberDana : AppCompatActivity() {
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private lateinit var binding: ActivityAddSumberDanaBinding
    var idJenisSubakSelected: String? = ""
    private var idsubak: String? = ""
    private var getIdSubak: String? = ""
    var isTextInputLayoutClicked: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSumberDanaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Tambah Data Sumber Dana"

        setJenisSumberDana()

        Log.e("id add sumber dana", "$getIdSubak")
        getIdSubak = intent.getStringExtra("idsubak")
        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnLanjutkan.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                addSumberDana()
                val intent = Intent(this, SumberDana::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
            }
        }
    }

    private fun addSumberDana() {
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
            val getNominal = binding.nominal.text.toString()
            val nominal = getNominal.toRequestBody("text/plain".toMediaType())
            val getTahun = binding.tahunSumberDana.text.toString()
            val tahun = getTahun.toRequestBody("text/plain".toMediaType())
            val getDeskripsi = binding.deskripsi.text.toString()
            val deskripsi = getDeskripsi.toRequestBody("text/plain".toMediaType())
            val getJenisSumberDanaId = idJenisSubakSelected.toString()
            val jenis_id = getJenisSumberDanaId.toRequestBody("text/plain".toMediaType())
            val service = ApiConfig.getApiService().addDataSumberDana(
                "Bearer $it",
                getIdSubak!!.toInt(),
                jenis_id,
                nominal,
                tahun,
                deskripsi
            )

            service.enqueue(object : Callback<SumberDanaResponse> {
                override fun onResponse(
                    call: Call<SumberDanaResponse>,
                    response: Response<SumberDanaResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddSumberDana,
                            "Berhasil input data subak",
                            Toast.LENGTH_SHORT

                        ).show()
//                        val subak = response.body()
//                        idsubak = subak?.data?.id.toString()
//                        Log.e("idsubak", subak?.data?.id.toString())
                    } else {
                        Toast.makeText(
                            this@AddSumberDana,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<SumberDanaResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddSumberDana,
                        "Cannot instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    fun setJenisSumberDana() {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]
        val idJenisSumberDana: MutableList<String> = ArrayList()

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            if (it != "") {
                addDataUmumViewModel.getJenisSumberDana(it)
                addDataUmumViewModel.jenisSumberDanaList.observe(this) { jenisSumberDana ->
                    getListJenisSumberDana(jenisSumberDana)
                    if (it != null) {
                        val list: MutableList<String> = ArrayList()

                        jenisSumberDana.forEach {
                            idJenisSumberDana.add(it.id)
                            list.add(it.nama)
                        }

                        val searchableSpinner = SearchableSpinner(this)
                        searchableSpinner.windowTitle = "Pilih Jenis Sumber Dana"
                        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
                            override fun setOnItemSelectListener(
                                position: Int,
                                selectedString: String
                            ) {
                                Toast.makeText(
                                    this@AddSumberDana,
                                    "${searchableSpinner.selectedItem}  ${searchableSpinner.selectedItemPosition}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                idJenisSubakSelected = idJenisSumberDana[position]
                                if (isTextInputLayoutClicked)
                                    binding.textJenisSumberDana.text = selectedString
                                else
                                    binding.editTextSpinner.setText(selectedString)
                            }
                        }

                        searchableSpinner.setSpinnerListItems(list as ArrayList<String>)
                        binding.textJenisSumberDana.keyListener = null
                        binding.textJenisSumberDana.setOnClickListener {
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
                }
            }
//            } else {
//                startActivity(Intent(this, LoginActivity::class.java))
//                finish()
//            }
        }
    }

    private fun getListJenisSumberDana(jenisSumberDana: MutableList<DataJenisSumberDana>) {
    }
}