package com.diskominfos.subakbali.ui.tambah.parahyangan

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.databinding.ActivityDetailTempSubakBinding
import com.diskominfos.subakbali.databinding.ActivityDetailTradisiBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.data.DataViewModel
import com.diskominfos.subakbali.ui.tambah.dataumum.EditTempSubak
import java.sql.Types.NULL

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DetailTradisi : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTradisiBinding
    private lateinit var dataViewModel: DataViewModel
    private var token: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailTradisiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val id = bundle.getString("id")
            detailTradisiSubak("$id")

            binding.btnHapus.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Hapus Tradisi Subak")
                    .setMessage("Apakah anda yakin ingin menghapus tradisi subak ini?")
                    .setCancelable(true)
                    .setPositiveButton("Iya") { _, _ ->
                        deleteTradisiSubak("$id")
                    }.setNegativeButton("Batal") { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }.show()
            }
            binding.btnEdit.setOnClickListener {
                val intent = Intent(this, EditTradisi::class.java)
                bundle.putString("id", "$id")
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }

    fun detailTradisiSubak(id: String) {
        dataViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[DataViewModel::class.java]
        dataViewModel.getUser().observe(this) { tradisiSubak ->
            dataViewModel.getTradisi(tradisiSubak, id)
            dataViewModel.tradisiSubakList.observe(this) {
                binding.apply {
                    val data = it[0]
                    if (data.odalan_jenis == "sasih") {
                        txtSaptawara.visibility = GONE
                        odalanSaptawara.visibility = GONE
                        txtPancawara.visibility = GONE
                        odalanPancawara.visibility = GONE
                        txtWuku.visibility = GONE
                        odalanWuku.visibility = GONE
                    }
                    else if (data.odalan_jenis == "pawukon") {
                        txtBulan.visibility = GONE
                        odalanBulan.visibility = GONE
                        txtSasih.visibility = GONE
                        odalanSasih.visibility = GONE
                    }
                    namaTradisi.setText(data.nama)
                    deskripsiPura.setText(data.deskripsi)
                    odalanJenis.setText(data.odalan_jenis)
                    odalanSaptawara.setText(data.odalan_saptawara)
                    odalanPancawara.setText(data.odalan_pancawara)
                    odalanBulan.setText(data.odalan_bulan)
                    odalanWuku.setText(data.odalan_wuku)
                    odalanSasih.setText(data.odalan_sasih)
                }
            }
        }
    }

    fun deleteTradisiSubak(id: String) {
        dataViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[DataViewModel::class.java]
        dataViewModel.getUser().observe(this) { delTradisiSubak ->
            dataViewModel.deleteDataTradisi(delTradisiSubak, id)
            Toast.makeText(this, "hapus data berhasil", Toast.LENGTH_SHORT).show()
        }
    }
}