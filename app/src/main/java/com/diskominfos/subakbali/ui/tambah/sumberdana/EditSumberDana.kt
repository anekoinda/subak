package com.diskominfos.subakbali.ui.tambah.sumberdana

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
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.databinding.ActivityEditSumberDanaBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.data.DataViewModel
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import com.leo.searchablespinner.SearchableSpinner
import com.leo.searchablespinner.interfaces.OnItemSelectListener
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class EditSumberDana : AppCompatActivity() {
    private lateinit var binding: ActivityEditSumberDanaBinding
    private lateinit var dataViewModel: DataViewModel
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    var jenisSumberDanaSelected: String? = ""
    var idJenisSumberDanaSelected: String? = ""
    var jenis: String = ""
    private var subak_id: String = ""
    private var token: String = ""
    var isTextInputLayoutClicked: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditSumberDanaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val id_subak = bundle.getString("id")
            getData("$id_subak")
            subak_id = id_subak.toString()
            binding.btnSimpan.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Edit Draf Subak")
                    .setMessage("Apakah anda yakin ingin mengedit sumber dana ini?")
                    .setCancelable(true)
                    .setPositiveButton("Iya") { _, _ ->
                        simpanData("$id_subak")
                        Log.e("data id", "$id_subak")
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
        setJenisSumberDana()
        dataViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[DataViewModel::class.java]
        dataViewModel.getUser().observe(this) { sumberDana ->
            dataViewModel.getSumberDana(sumberDana, id)
            dataViewModel.sumberDanaSubakList.observe(this) {
                binding.apply {
                    val data = it[0]
                    val id = data.id
                    textJenisSumberDana.text = data.jenis_sumber_dana.nama
                    nominal.setText(data.nominal)
                    tahunSumberDana.setText(data.tahun)
                    deskripsi.setText(data.deskripsi)
                }
            }
        }
    }

    fun simpanData(id: String) {
        getViewModel()
        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        var getUsername: String = ""
        addDataUmumViewModel.getUsername().observe(this) { it ->
            getUsername = it
        }
        addDataUmumViewModel.getUser().observe(this) { it ->
            Log.e("data id", "$id")
            Log.e("token", it)
            val getNominal = binding.nominal.text.toString()
            val nominal = getNominal.toRequestBody("text/plain".toMediaType())
            val getTahun = binding.tahunSumberDana.text.toString()
            val tahun = getTahun.toRequestBody("text/plain".toMediaType())
            val getDeskripsi = binding.deskripsi.text.toString()
            val deskripsi = getDeskripsi.toRequestBody("text/plain".toMediaType())
            val getSubakId = subak_id
            val subakid = getSubakId.toRequestBody("text/plain".toMediaType())
            var jenis_sumber_dana = ""
            if (jenisSumberDanaSelected == "") {
                jenis_sumber_dana = jenis
                Log.e("id jenis sumber danaa", "$jenis_sumber_dana")
            } else {
                val getJenis = jenisSumberDanaSelected.toString()
                jenis_sumber_dana = getJenis
                Log.e("id jenis sumber dana", "$jenis_sumber_dana")
            }

            val service = ApiConfig.getApiService().updateSumberDana(
                "Bearer $it",
                id,
                subakid,
                jenis_sumber_dana.toRequestBody("text/plain".toMediaType()),
                nominal,
                tahun,
                deskripsi
            )

            service.enqueue(object : Callback<GetSumberDanaResponse> {
                override fun onResponse(
                    call: Call<GetSumberDanaResponse>,
                    response: Response<GetSumberDanaResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@EditSumberDana,
                            "Berhasil edit sumber dana",
                            Toast.LENGTH_SHORT
                        ).show()
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(
                                this@EditSumberDana,
                                responseBody.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@EditSumberDana,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GetSumberDanaResponse>, t: Throwable) {
                    Toast.makeText(
                        this@EditSumberDana,
                        "Cannot instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        }
    }
    fun getViewModel() {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
    }

    fun searchSpinner() {
        val searchableSpinner = SearchableSpinner(this)
        binding.editTextSpinner.keyListener = null
        binding.editTextSpinner.setOnClickListener {
            searchableSpinner.highlightSelectedItem = false
            isTextInputLayoutClicked = false
            searchableSpinner.show()
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
                                    this@EditSumberDana,
                                    "${searchableSpinner.selectedItem}  ${searchableSpinner.selectedItemPosition}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                idJenisSumberDanaSelected = idJenisSumberDana[position]
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