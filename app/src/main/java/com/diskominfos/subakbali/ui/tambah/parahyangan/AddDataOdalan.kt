package com.diskominfos.subakbali.ui.tambah.parahyangan

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.api.ApiConfig
import com.diskominfos.subakbali.api.DataSubakResponse
import com.diskominfos.subakbali.api.PuraResponse
import com.diskominfos.subakbali.databinding.ActivityAddDataOdalanBinding
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

class AddDataOdalan : AppCompatActivity() {
    private lateinit var dataViewModel: DataViewModel
    private lateinit var puraViewModel: PuraViewModel
    private val jenisOdalan = arrayListOf("Sasih", "Pawukon")
    private val bulanOdalan = arrayListOf("Purnama", "Tilem")
    private val sapwatara =
        arrayListOf("Redite", "Soma", "Anggara", "Buddha", "Wrespati", "Sukra", "Saniscara")
    private val jenisSasih = arrayListOf(
        "Kasa",
        "Karo",
        "Katiga",
        "Kapat",
        "Kalima",
        "Kaenem",
        "Kepitu",
        "Kewolu",
        "Kesanga",
        "Kedasa",
        "Jiyesta",
        "Shada"
    )
//    private val wuku = arrayListOf(
//        "Sinta",
//        "Landep",
//        "Ukir",
//        "Kulantir",
//        "Tolu",
//        "Gumbreg",
//        "Wariga",
//        "Warigadian",
//        "Julungwangi",
//        "Sungsang",
//        "Dulungan",
//        "Kuningan",
//        "Langkir",
//        "Medangsia",
//        "Pujut",
//        "Pahang",
//        "Krulut",
//        "Merakih",
//        "Tambir",
//        "Medangkungan",
//        "Matal",
//        "Uye",
//        "Menail",
//        "Parangbakat",
//        "Bala",
//        "Ugu",
//        "Wayang",
//        "Kelawu",
//        "Dukut",
//        "Watugunung"
//    )
    var jenisOdalanSelected: String? = ""
    var bulanOdalanSelected: String? = ""
    var jenisSasihSelected: String? = ""
    var wukuSelected: String? = ""
    var saptawaraSelected: String? = ""

    //    var pancawaraSelected: String? = ""
    var isTextInputLayoutClicked: Boolean = false
    var jenis_odalan: String = ""
    private lateinit var binding: ActivityAddDataOdalanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDataOdalanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setJenisOdalan()
        setBulanOdalan()
        setJenisSasih()
        setWuku()
        setSaptawara()

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val id = bundle.getString("id")
            Log.e("id pura", "$id")
            binding.btnSimpan.setOnClickListener {
                simpanData("$id")
            }
        }
    }

    private fun setJenisOdalan() {
        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Jenis Odalan"
        searchableSpinner.searchViewVisibility = SearchableSpinner.SpinnerView.GONE
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {
                if (isTextInputLayoutClicked)
                    jenisOdalanSelected = selectedString

                binding.textJenisOdalan.text = jenisOdalanSelected
                if (jenisOdalanSelected == "Sasih") {
                    binding.jenisOdalanPawukon.visibility = View.INVISIBLE
                    binding.jenisOdalanSasih.visibility = View.VISIBLE
                } else {
                    binding.jenisOdalanSasih.visibility = View.INVISIBLE
                    binding.jenisOdalanPawukon.visibility = View.VISIBLE
                }
            }
        }

        searchableSpinner.setSpinnerListItems(jenisOdalan)
        binding.textJenisOdalan.keyListener = null
        binding.textJenisOdalan.setOnClickListener {
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

    private fun setBulanOdalan() {
        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Bulan Odalan"
        searchableSpinner.searchViewVisibility = SearchableSpinner.SpinnerView.GONE
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {
                if (isTextInputLayoutClicked)
                    bulanOdalanSelected = selectedString
                binding.textJenisBulan.text = bulanOdalanSelected
            }
        }

        searchableSpinner.setSpinnerListItems(bulanOdalan)
        binding.textJenisBulan.keyListener = null
        binding.textJenisBulan.setOnClickListener {
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

    private fun setJenisSasih() {
        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Sasih"
        searchableSpinner.searchViewVisibility = SearchableSpinner.SpinnerView.GONE
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {
                if (isTextInputLayoutClicked)
                    jenisSasihSelected = selectedString
                binding.textJenisSasih.text = jenisSasihSelected
            }
        }

        searchableSpinner.setSpinnerListItems(jenisSasih)
        binding.textJenisSasih.keyListener = null
        binding.textJenisSasih.setOnClickListener {
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

    private fun setWuku() {
        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Wuku"
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {
                if (isTextInputLayoutClicked)
                    wukuSelected = selectedString
                binding.textWuku.text = wukuSelected
                val wukuArray = wuku_detail()
                for (it in wukuArray) {
                    if (it[2] == selectedString) {
                        Log.e("hahaha", "$it[0]")
                    }
                }
            }

        }

        val wukuArray = wuku_detail()
        val names = wukuArray.map { it[2] }
        searchableSpinner.setSpinnerListItems(names as ArrayList<String>)


        binding.textWuku.keyListener = null
        binding.textWuku.setOnClickListener {
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

    private fun setSaptawara() {
        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Wuku"
        searchableSpinner.searchViewVisibility = SearchableSpinner.SpinnerView.GONE
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {
                if (isTextInputLayoutClicked)
                    saptawaraSelected = selectedString
                binding.textSaptawara.text = saptawaraSelected
            }
        }

        val saptawaraArray = saptawara()
        val names = saptawaraArray.map { it[2] }
        searchableSpinner.setSpinnerListItems(names as ArrayList<String>)
        binding.textSaptawara.keyListener = null
        binding.textSaptawara.setOnClickListener {
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

    fun wuku_detail(): Array<Array<Any>> {
        return arrayOf(
            arrayOf("id" to 0, "number" to 1, "name" to "Sinta"),
            arrayOf("id" to 1, "number" to 2, "name" to "Landep"),
            arrayOf("id" to 2, "number" to 3, "name" to "Ukir"),
            arrayOf("id" to 3, "number" to 4, "name" to "Kulantir"),
            arrayOf("id" to 4, "number" to 5, "name" to "Tolu"),
            arrayOf("id" to 5, "number" to 6, "name" to "Gumbreg"),
            arrayOf("id" to 6, "number" to 7, "name" to "Wariga"),
            arrayOf("id" to 7, "number" to 8, "name" to "Warigadian"),
            arrayOf("id" to 8, "number" to 9, "name" to "Julungwangi"),
            arrayOf("id" to 9, "number" to 10, "name" to "Sungsang"),
            arrayOf("id" to 10, "number" to 11, "name" to "Dungulan"),
            arrayOf("id" to 11, "number" to 12, "name" to "Kuningan"),
            arrayOf("id" to 12, "number" to 13, "name" to "Langkir"),
            arrayOf("id" to 13, "number" to 14, "name" to "Medangsia"),
            arrayOf("id" to 14, "number" to 15, "name" to "Pujut"),
            arrayOf("id" to 15, "number" to 16, "name" to "Pahang"),
            arrayOf("id" to 16, "number" to 17, "name" to "Krulut"),
            arrayOf("id" to 17, "number" to 18, "name" to "Merakih"),
            arrayOf("id" to 18, "number" to 19, "name" to "Tambir"),
            arrayOf("id" to 19, "number" to 20, "name" to "Medangkungan"),
            arrayOf("id" to 20, "number" to 21, "name" to "Matal"),
            arrayOf("id" to 21, "number" to 22, "name" to "Uye"),
            arrayOf("id" to 22, "number" to 23, "name" to "Menail"),
            arrayOf("id" to 23, "number" to 24, "name" to "Prangbakat"),
            arrayOf("id" to 24, "number" to 25, "name" to "Bala"),
            arrayOf("id" to 25, "number" to 26, "name" to "Ugu"),
            arrayOf("id" to 26, "number" to 27, "name" to "Wayang"),
            arrayOf("id" to 27, "number" to 28, "name" to "Kelawu"),
            arrayOf("id" to 28, "number" to 29, "name" to "Dukut"),
            arrayOf("id" to 29, "number" to 30, "name" to "Watugunung"),
        )
    }

    fun pancawara(): Array<Array<Any>> {
        return arrayOf(
            arrayOf(
                "id" to 0,
                "number" to 1,
                "name" to "Umanis",
                "subject" to "umanis"
            ),
            arrayOf(
                "id" to 1,
                "number" to 2,
                "name" to "Paing",
                "subject" to "paing"
            ),
            arrayOf(
                "id" to 2,
                "number" to 3,
                "name" to "Pon",
                "subject" to "pon"
            ),
            arrayOf(
                "id" to 3,
                "number" to 4,
                "name" to "Wage",
                "subject" to "wage"
            ),
            arrayOf(
                "id" to 4,
                "number" to 5,
                "name" to "Kliwon",
                "subject" to "kliwon"
            )
        )
    }

    fun saptawara(): Array<Array<Any>> {
        return arrayOf(
            arrayOf(
                "id" to 0,
                "number" to 1,
                "name" to "Redite",
                "subject" to "redite"
            ),
            arrayOf(
                "id" to 1,
                "number" to 2,
                "name" to "Soma",
                "subject" to "soma"
            ),
            arrayOf(
                "id" to 2,
                "number" to 3,
                "name" to "Anggara",
                "subject" to "anggara"
            ),
            arrayOf(
                "id" to 3,
                "number" to 4,
                "name" to "Buddha",
                "subject" to "buddha"
            ),
            arrayOf(
                "id" to 4,
                "number" to 5,
                "name" to "Wrespati",
                "subject" to "wrespati"
            ),
            arrayOf(
                "id" to 5,
                "number" to 6,
                "name" to "Sukra",
                "subject" to "sukra"
            ),
            arrayOf(
                "id" to 6,
                "number" to 7,
                "name" to "Saniscara",
                "subject" to "saniscara"
            ),
        )
    }

    fun get_pancawara_by_wuku_saptawara(wuku: Int, saptawara: Int): Map<String, Any> {
        val x = (wuku * 7) + saptawara
        val sisa = x % 5
        val pancawara: Map<String, Any> = when (sisa) {
            1 -> mapOf(
                "id" to 0,
                "number" to 1,
                "name" to "Umanis",
                "subject" to "umanis"
            )
            2 -> mapOf(
                "id" to 1,
                "number" to 2,
                "name" to "Paing",
                "subject" to "paing"
            )
            3 -> mapOf(
                "id" to 2,
                "number" to 3,
                "name" to "Pon",
                "subject" to "pon"
            )
            4 -> mapOf(
                "id" to 3,
                "number" to 4,
                "name" to "Wage",
                "subject" to "wage"
            )
            0, 5 -> mapOf(
                "id" to 4,
                "number" to 5,
                "name" to "Kliwon",
                "subject" to "kliwon"
            )
            else -> throw IllegalArgumentException("Invalid sisa value")
        }
        return pancawara
    }

    fun simpanData(id: String) {
        puraViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[PuraViewModel::class.java]

        puraViewModel = ViewModelProvider(this)[PuraViewModel::class.java]
        var getUsername: String = ""
        puraViewModel.getUsername().observe(this) { it ->
            getUsername = it
        }
        puraViewModel.getUser().observe(this) { it ->
            Log.e("data id", "$id")
            Log.e("token", it)

//            val updated_by = getUsername.toRequestBody("text/plain".toMediaType())
//            Log.e("updated by", "$updated_by")

            val service = ApiConfig.getApiService().updatePura(
                "Bearer $it",
                id,
                jenisOdalanSelected?.toRequestBody("text/plain".toMediaType())!!,
                saptawaraSelected?.toRequestBody("text/plain".toMediaType())!!,
                bulanOdalanSelected?.toRequestBody("text/plain".toMediaType())!!,
                wukuSelected?.toRequestBody("text/plain".toMediaType())!!,
                jenisSasihSelected?.toRequestBody("text/plain".toMediaType())!!
            )

            service.enqueue(object : Callback<PuraResponse> {
                override fun onResponse(
                    call: Call<PuraResponse>,
                    response: Response<PuraResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddDataOdalan,
                            "Berhasil input data odalan",
                            Toast.LENGTH_SHORT
                        ).show()
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(
                                this@AddDataOdalan,
                                responseBody.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@AddDataOdalan,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PuraResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddDataOdalan,
                        "Cannot instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        }
    }

}