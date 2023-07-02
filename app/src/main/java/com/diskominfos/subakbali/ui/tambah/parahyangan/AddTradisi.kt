package com.diskominfos.subakbali.ui.tambah.parahyangan

import android.content.Context
import android.content.Intent
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
import com.diskominfos.subakbali.api.ApiConfig
import com.diskominfos.subakbali.api.PuraResponse
import com.diskominfos.subakbali.api.TradisiResponse
import com.diskominfos.subakbali.databinding.ActivityAddTradisiBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import com.diskominfos.subakbali.ui.tambah.datawilayah.AddDataWilayah
import com.leo.searchablespinner.SearchableSpinner
import com.leo.searchablespinner.interfaces.OnItemSelectListener
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AddTradisi : AppCompatActivity() {
    private lateinit var binding: ActivityAddTradisiBinding
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private val jenisOdalan = arrayListOf("Sasih", "Pawukon")
    private val bulanOdalan = arrayListOf("Purnama", "Tilem")
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
    var jenisOdalanSelected: String? = ""
    var bulanOdalanSelected: String? = ""
    var jenisSasihSelected: String? = ""
    var wukuSelected: String? = ""
    var saptawaraSelected: String? = ""
    var isTextInputLayoutClicked: Boolean = false
    var idWuku: Int = 0
    var idSaptawara: Int = 0
    var namePancawara: String? = ""
    private var getIdSubak: String? = ""
    private lateinit var listWuku: ArrayList<String>
    private lateinit var listSaptawara: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTradisiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Tambah Tradisi"

        setJenisOdalan()
        setBulanOdalan()
        setJenisSasih()
        wuku_detail()
        saptawara()

        getIdSubak = intent.getStringExtra("idsubak") // Retrieve the string value from the Intent
        Log.e("id subak add tradisi", "$getIdSubak")
        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnSimpan.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                addDataTradisi()
                val intent = Intent(this, Tradisi::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
            }
        }
    }

    private fun addDataTradisi() {
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
            val getName = binding.namaTradisi.text.toString()
            val nama = getName.toRequestBody("text/plain".toMediaType())
            val getDeskripsi = binding.deskripsiPura.text.toString()
            val deskripsi = getDeskripsi.toRequestBody("text/plain".toMediaType())
            val getJenisOdalan= jenisOdalanSelected
            val odalan_jenis = getJenisOdalan?.toRequestBody("text/plain".toMediaType())
            val getOdalanSaptawara= saptawaraSelected
            val odalan_saptawara = getOdalanSaptawara?.toRequestBody("text/plain".toMediaType())
            val getOdalanPancawara = namePancawara
            val odalan_pancawara = getOdalanPancawara?.toRequestBody("text/plain".toMediaType())
            val getOdalanBulan= bulanOdalanSelected
            val odalan_bulan = getOdalanBulan?.toRequestBody("text/plain".toMediaType())
            val getOdalanWuku= wukuSelected
            val odalan_wuku = getOdalanWuku?.toRequestBody("text/plain".toMediaType())
            val getOdalanSasih= jenisSasihSelected
            val odalan_sasih = getOdalanSasih?.toRequestBody("text/plain".toMediaType())
            val service = ApiConfig.getApiService().addDataTradisi(
                "Bearer $it",
                getIdSubak!!.toInt(),
                nama,
                deskripsi,
                odalan_jenis!!,
                odalan_saptawara!!,
                odalan_pancawara!!,
                odalan_bulan!!,
                odalan_wuku!!,
                odalan_sasih!!,
                1
            )

            service.enqueue(object : Callback<TradisiResponse> {
                override fun onResponse(
                    call: Call<TradisiResponse>,
                    response: Response<TradisiResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddTradisi,
                            "Berhasil input data tradisi",
                            Toast.LENGTH_SHORT
                        ).show()
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(
                                this@AddTradisi,
                                responseBody.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@AddTradisi,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<TradisiResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddTradisi,
                        "Cannot instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

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
    fun wuku_detail(): ArrayList<String> {
        val wukuArray = arrayOf(
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
        listWuku = wukuArray.flatMap { subArray ->
            subArray.filter { it.first == "name" }.map { it.second.toString() }
        } as ArrayList<String>

        Log.e("nameee", "$listWuku")

        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Wuku"
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {
                if (isTextInputLayoutClicked)
                    wukuSelected = selectedString
                val nameToFind = wukuSelected
                Log.e("id haha", "$nameToFind")
                val subArray = wukuArray.find { it.any { tuple -> tuple.first == "name" && tuple.second == nameToFind } }
                idWuku = subArray?.find { it.first == "number" }?.second as Int
                Log.e("id haha", "$idWuku")
                binding.textWuku.text = wukuSelected
            }
        }

        searchableSpinner.setSpinnerListItems(listWuku)
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




        val wukuList = ArrayList<String>()
        return wukuList
    }

    fun saptawara(): ArrayList<String> {
        val saptawaraArray = arrayOf(
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
            )
        )

        listSaptawara = saptawaraArray.flatMap { subArray ->
            subArray.filter { it.first == "name" }.map { it.second.toString() }
        } as ArrayList<String>

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
                val nameToFind = saptawaraSelected
                Log.e("id haha", "$nameToFind")
                val subArray = saptawaraArray.find { it.any { tuple -> tuple.first == "name" && tuple.second == nameToFind } }
                idSaptawara = subArray?.find { it.first == "id" }?.second as Int
                Log.e("id haha", "$idSaptawara")
                binding.textSaptawara.text = saptawaraSelected
                val getIdWuku = idWuku
                get_pancawara_by_wuku_saptawara(getIdWuku, idSaptawara)
                Log.e("get wuku 1", "$getIdWuku")
                Log.e("get saptawara 1", "$idSaptawara")
            }
        }

        searchableSpinner.setSpinnerListItems(listSaptawara)
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

        val saptawaraList = ArrayList<String>()
        return saptawaraList
    }

    fun get_pancawara_by_wuku_saptawara(wuku: Int, saptawara: Int): Map<String, Any> {
        val x = (wuku * 7) + saptawara
        val sisa = x % 5
        Log.e("get wuku", "$wuku")
        Log.e("get saptawara", "$saptawara")
        Log.e("get sisa", "$sisa")
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
        namePancawara = pancawara["name"].toString()
        Log.e("pancawara", "$namePancawara")
        if (saptawaraSelected == null){
            binding.textPancawara.setText(" ")
        }else{
            binding.textPancawara.setText(namePancawara)
        }
        return pancawara
    }
}