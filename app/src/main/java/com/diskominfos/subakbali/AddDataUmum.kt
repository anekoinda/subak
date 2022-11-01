package com.diskominfos.subakbali

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.api.ApiConfig.Companion.getApiService
import com.diskominfos.subakbali.api.DataDesaDinas
import com.diskominfos.subakbali.api.DataKabupaten
import com.diskominfos.subakbali.api.DataKecamatan
import com.diskominfos.subakbali.api.KabupatenResponse
import com.diskominfos.subakbali.databinding.ActivityAddDataUmumBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.data.DataViewModel
import com.diskominfos.subakbali.ui.profil.ProfilViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddDataUmum : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private lateinit var profilViewModel: ProfilViewModel
    private lateinit var binding: ActivityAddDataUmumBinding
    val jenisSubak = arrayOf("subak", "abian")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_data_umum)

        binding = ActivityAddDataUmumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupJenis()
        setupViewModelKabupaten()
        setupViewModelKecamatan()
        setupViewModelDesaDinas()
    }

    private fun setupJenis() {
        binding.spnJenis.onItemSelectedListener = this
        val adapterJenis = ArrayAdapter(this, android.R.layout.simple_spinner_item, jenisSubak)
        adapterJenis.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnJenis.adapter = adapterJenis
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    private fun setupViewModelKabupaten() {
        profilViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[ProfilViewModel::class.java]

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        profilViewModel.getUser().observe(this) {
            if (it != "") {
                addDataUmumViewModel.getKabupaten(it)
                addDataUmumViewModel.kabupatenList.observe(this) { kabupaten ->
                    getListKabupaten(kabupaten)
                    if (it != null) {
                        val list: MutableList<String> = ArrayList()
                        kabupaten.forEach {
                            list.add(0, it.name.toString())
                            Log.d("data kabupaten", it.name)
                        }
                        binding.spnKabupaten.adapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_item,
                            list
                        )
                    }
                    Log.d("errorrrrrrrr", "setupViewModel: $kabupaten")
                }
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun getListKabupaten(kabupaten: MutableList<DataKabupaten>) {
    }

    private fun setupViewModelKecamatan() {
        profilViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[ProfilViewModel::class.java]

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        profilViewModel.getUser().observe(this) {
            if (it != "") {
                addDataUmumViewModel.getKecamatan(it)
                addDataUmumViewModel.kecamataList.observe(this) { kecamatan ->
                    getListKecamatan(kecamatan)
                    if (it != null) {
                        val list: MutableList<String> = ArrayList()
                        kecamatan.forEach {
                            list.add(0, it.name.toString())
                        }
                        binding.spnKecamatan.adapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_item,
                            list
                        )
                    }
                    Log.d("errorrrrrrrr", "setupViewModel: $kecamatan")
                }
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun getListKecamatan(kecamatan: MutableList<DataKecamatan>) {
    }

    private fun setupViewModelDesaDinas() {
        profilViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[ProfilViewModel::class.java]

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        profilViewModel.getUser().observe(this) {
            if (it != "") {
                addDataUmumViewModel.getDesDinas(it)
                addDataUmumViewModel.desaList.observe(this) { desa ->
                    getListDesa(desa)
                    if (it != null) {
                        val list: MutableList<String> = ArrayList()
                        desa.forEach {
                            list.add(0, it.name.toString())
                            Log.d("data desa", it.name)
                        }
                        binding.spnDesa.adapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_item,
                            list
                        )
                    }
                    Log.d("errorrrrrrrr", "setupViewModel: $desa")
                }
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun getListDesa(kabupaten: MutableList<DataDesaDinas>) {
    }
}

