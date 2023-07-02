package com.diskominfos.subakbali.ui.tambah.sumberdana

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.databinding.ActivityDetailSumberDanaBinding
import com.diskominfos.subakbali.databinding.ActivityDetailTradisiBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.data.DataViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class DetailSumberDana : AppCompatActivity() {
    private lateinit var binding: ActivityDetailSumberDanaBinding
    private lateinit var dataViewModel: DataViewModel
    private var token: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSumberDanaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val id = bundle.getString("id")
            detailSumberDana("$id")

            binding.btnHapus.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Hapus Tradisi Subak")
                    .setMessage("Apakah anda yakin ingin menghapus sumber dana ini?")
                    .setCancelable(true)
                    .setPositiveButton("Iya") { _, _ ->
                        deleteSumberDana("$id")
                    }.setNegativeButton("Batal") { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }.show()
            }
            binding.btnEdit.setOnClickListener {
                val intent = Intent(this, EditSumberDana::class.java)
                bundle.putString("id", "$id")
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }

    fun detailSumberDana(id: String) {
        dataViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[DataViewModel::class.java]
        dataViewModel.getUser().observe(this) { sumberDana ->
            dataViewModel.getSumberDana(sumberDana, id)
            dataViewModel.sumberDanaSubakList.observe(this) {
                binding.apply {
                    val data = it[0]
                    textJenisSumberDana.text = data.jenis_sumber_dana.nama
                    nominal.setText(data.nominal)
                    tahunSumberDana.setText(data.tahun)
                    deskripsi.setText(data.deskripsi)
                }
            }
        }
    }

    fun deleteSumberDana(id: String) {
        dataViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[DataViewModel::class.java]
        dataViewModel.getUser().observe(this) { delSumberDana ->
            dataViewModel.deleteSumberDana(delSumberDana, id)
            Toast.makeText(this, "hapus data berhasil", Toast.LENGTH_SHORT).show()
        }
    }
}