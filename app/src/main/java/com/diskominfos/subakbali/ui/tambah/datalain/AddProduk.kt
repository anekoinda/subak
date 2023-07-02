package com.diskominfos.subakbali.ui.tambah.datalain

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
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.databinding.ActivityAddProdukBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import com.diskominfos.subakbali.ui.tambah.sumberdana.SumberDana
import com.leo.searchablespinner.SearchableSpinner
import com.leo.searchablespinner.interfaces.OnItemSelectListener
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AddProduk : AppCompatActivity() {
    private lateinit var binding: ActivityAddProdukBinding
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    var idJenisProdukSelected: String? = ""
    private var getIdSubak: String? = ""
    var isTextInputLayoutClicked: Boolean = false
    private var token : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProdukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Tambah Produk"

        setJenisProduk()

        getIdSubak = intent.getStringExtra("idsubak")
        Log.e("id add produk", "$getIdSubak")
        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnLanjutkan.setOnClickListener {
                addProduk("$getIdSubak")
                val intent = Intent(this, Produk::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
            }
        }
    }

    fun setJenisProduk() {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]
        val idJenisProduk: MutableList<String> = ArrayList()

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            if (it != "") {
                addDataUmumViewModel.getJenisProduk(it)
                addDataUmumViewModel.jenisProdukList.observe(this) { jenisProduk ->
                    getListJenisProduk(jenisProduk)
                    if (it != null) {
                        val list: MutableList<String> = ArrayList()

                        jenisProduk.forEach {
                            idJenisProduk.add(it.id)
                            list.add(it.nama)
                        }

                        val searchableSpinner = SearchableSpinner(this)
                        searchableSpinner.windowTitle = "Pilih Jenis Produk"
                        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
                            override fun setOnItemSelectListener(
                                position: Int,
                                selectedString: String
                            ) {
                                Toast.makeText(
                                    this@AddProduk,
                                    "${searchableSpinner.selectedItem}  ${searchableSpinner.selectedItemPosition}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                idJenisProdukSelected = idJenisProduk[position]
                                Log.e("produk selected", "$idJenisProdukSelected")
                                if (isTextInputLayoutClicked)
                                    binding.textJenisProduk.text = selectedString
                                else
                                    binding.editTextSpinner.setText(selectedString)
                            }
                        }

                        searchableSpinner.setSpinnerListItems(list as ArrayList<String>)
                        binding.textJenisProduk.keyListener = null
                        binding.textJenisProduk.setOnClickListener {
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

    private fun getListJenisProduk(jenisProduk: MutableList<DataJenisProduk>) {
    }

    private fun addProduk(id: String) {
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
            val getNama = binding.namaProduk.text.toString()
            val nama = getNama.toRequestBody("text/plain".toMediaType())
            val getJenisProdukId = idJenisProdukSelected.toString()
            val jenis_id = getJenisProdukId.toRequestBody("text/plain".toMediaType())
            val service = ApiConfig.getApiService().addDataProduk(
                "Bearer $it",
                getIdSubak!!.toInt(),
                1,
                jenis_id,
                nama
            )

            service.enqueue(object : Callback<ProdukResponse> {
                override fun onResponse(
                    call: Call<ProdukResponse>,
                    response: Response<ProdukResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddProduk,
                            "Berhasil input data subak",
                            Toast.LENGTH_SHORT

                        ).show()
//                        val subak = response.body()
//                        idsubak = subak?.data?.id.toString()
//                        Log.e("idsubak", subak?.data?.id.toString())
                    } else {
                        Toast.makeText(
                            this@AddProduk,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ProdukResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddProduk,
                        "Cannot instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }
}