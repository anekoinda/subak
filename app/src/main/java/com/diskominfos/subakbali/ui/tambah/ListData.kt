package com.diskominfos.subakbali.ui.tambah

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.diskominfos.subakbali.ui.tambah.datalain.Produk
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.databinding.ActivityListDataBinding
import com.diskominfos.subakbali.ui.tambah.datalain.AddAlihFungsi
import com.diskominfos.subakbali.ui.tambah.datalain.AddUsaha
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmum
import com.diskominfos.subakbali.ui.tambah.datawilayah.AddDataWilayah
import com.diskominfos.subakbali.ui.tambah.dokumen.AddDokumen
import com.diskominfos.subakbali.ui.tambah.dokumen.Awig
import com.diskominfos.subakbali.ui.tambah.dokumen.Perarem
import com.diskominfos.subakbali.ui.tambah.dokumen.SuratKeterangan
import com.diskominfos.subakbali.ui.tambah.krama.AddKrama
import com.diskominfos.subakbali.ui.tambah.parahyangan.AddDataParahyangan
import com.diskominfos.subakbali.ui.tambah.parahyangan.Tradisi
import com.diskominfos.subakbali.ui.tambah.sumberair.AddSumberAirSubak
import com.diskominfos.subakbali.ui.tambah.sumberdana.SumberDana

class ListData : AppCompatActivity() {
    private lateinit var binding: ActivityListDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_data)

        supportActionBar?.hide()

        binding = ActivityListDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dataUmum.setOnClickListener {
            val intent = Intent(this, AddDataUmum::class.java)
            startActivity(intent)
        }

        binding.dataWilayah.setOnClickListener {
            val intent = Intent(this, AddDataWilayah::class.java)
            startActivity(intent)
        }

        binding.dataParahyangan.setOnClickListener {
            val intent = Intent(this, AddDataParahyangan::class.java)
            startActivity(intent)
        }

        binding.dataTradisi.setOnClickListener {
            val intent = Intent(this, Tradisi::class.java)
            startActivity(intent)
        }

        binding.dataKrama.setOnClickListener {
            val intent = Intent(this, AddKrama::class.java)
            startActivity(intent)
        }

        binding.dataSuratKeterangan.setOnClickListener {
            val intent = Intent(this, SuratKeterangan::class.java)
            startActivity(intent)
        }

        binding.dataAwig.setOnClickListener {
            val intent = Intent(this, Awig::class.java)
            startActivity(intent)
        }

        binding.dataPerarem.setOnClickListener {
            val intent = Intent(this, Perarem::class.java)
            startActivity(intent)
        }

        binding.dataDana.setOnClickListener {
            val intent = Intent(this, SumberDana::class.java)
            startActivity(intent)
        }

        binding.dataAir.setOnClickListener {
            val intent = Intent(this, AddSumberAirSubak::class.java)
            startActivity(intent)
        }

        binding.dataProduk.setOnClickListener {
            val intent = Intent(this, Produk::class.java)
            startActivity(intent)
        }

        binding.dataUsaha.setOnClickListener {
            val intent = Intent(this, AddUsaha::class.java)
            startActivity(intent)
        }

        binding.dataAlihFungsi.setOnClickListener {
            val intent = Intent(this, AddAlihFungsi::class.java)
            startActivity(intent)
        }
    }
}