package com.diskominfos.subakbali.ui.tambah.krama

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.api.DataKabupaten
import com.diskominfos.subakbali.api.DataTempekan
import com.diskominfos.subakbali.databinding.ActivityAddDataUmumBinding
import com.diskominfos.subakbali.databinding.ActivityAddKramaBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import com.leo.searchablespinner.SearchableSpinner
import com.leo.searchablespinner.interfaces.OnItemSelectListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddKrama : AppCompatActivity() {
    private lateinit var tempekanViewModel: TempekanViewModel
    private lateinit var binding: ActivityAddKramaBinding
    var isTextInputLayoutClicked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddKramaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textTambahTempekan.setOnClickListener {
            val intent = Intent(this, AddTempekan::class.java)
            startActivity(intent)
        }

        setTempekan()
        setDatePicker()

        binding.btnFile.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            Log.e("file", "$selectedFile")
        }
    }

    private fun setDatePicker() {
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.btnTanggalMulai.text = sdf.format(cal.time)
            }
        binding.btnTanggalMulai.setOnClickListener {
            DatePickerDialog(
                this@AddKrama, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
            binding.btnTanggalMulai.text = SimpleDateFormat("dd MMM yyyy").format(System.currentTimeMillis())
        }
    }

    fun setTempekan() {
        tempekanViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[TempekanViewModel::class.java]
        val idKabupaten: MutableList<String> = ArrayList()

        tempekanViewModel = ViewModelProvider(this)[TempekanViewModel::class.java]
        tempekanViewModel.getUser().observe(this) { it ->
            if (it != "") {
                tempekanViewModel.getTempekan(it)
                tempekanViewModel.tempekanList.observe(this) { tempekan ->
                    getTempekanList(tempekan)
                    if (it != null) {
                        val list: MutableList<String> = ArrayList()

                        tempekan.forEach {
                            idKabupaten.add(it.id)
                            list.add(it.nama)
                        }

                        val searchableSpinner = SearchableSpinner(this)
                        searchableSpinner.windowTitle = "Pilih Tempekan"
                        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
                            override fun setOnItemSelectListener(
                                position: Int,
                                selectedString: String
                            ) {
                                Toast.makeText(
                                    this@AddKrama,
                                    "${searchableSpinner.selectedItem}  ${searchableSpinner.selectedItemPosition}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                if (idKabupaten.count() >= 0 && position >= 0) {
//                                    setKecamatan(idKabupaten[position])
                                }
//                                idKabupatenSelected = idKabupaten[position]
                                if (isTextInputLayoutClicked)
                                    binding.textTempekan.text = selectedString
                                else
                                    binding.editTextSpinner.setText(selectedString)
                            }
                        }

                        searchableSpinner.setSpinnerListItems(list as ArrayList<String>)
                        binding.textTempekan.keyListener = null
                        binding.textTempekan.setOnClickListener {
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

    private fun getTempekanList(tempekan: MutableList<DataTempekan>) {
    }
}