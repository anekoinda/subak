package com.diskominfos.subakbali.ui.tambah

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.databinding.ActivityListDataBinding
import com.diskominfos.subakbali.ui.tambah.datalain.AddDataLain
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmum
import com.diskominfos.subakbali.ui.tambah.datawilayah.AddDataWilayah
import com.diskominfos.subakbali.ui.tambah.dokumen.AddDokumen
import com.diskominfos.subakbali.ui.tambah.krama.AddKrama
import com.diskominfos.subakbali.ui.tambah.parahyangan.AddDataParahyangan
import com.diskominfos.subakbali.ui.tambah.sumberair.AddSumberAir
import com.diskominfos.subakbali.ui.tambah.sumberdana.AddSumberDana

class ListData : AppCompatActivity() {
    private lateinit var binding: ActivityListDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_data)

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

        binding.dataKrama.setOnClickListener {
            val intent = Intent(this, AddKrama::class.java)
            startActivity(intent)
        }

        binding.dataDokumen.setOnClickListener {
            val intent = Intent(this, AddDokumen::class.java)
            startActivity(intent)
        }

        binding.dataDana.setOnClickListener {
            val intent = Intent(this, AddSumberDana::class.java)
            startActivity(intent)
        }

        binding.dataAir.setOnClickListener {
            val intent = Intent(this, AddSumberAir::class.java)
            startActivity(intent)
        }

        binding.dataLainnya.setOnClickListener {
            val intent = Intent(this, AddDataLain::class.java)
            startActivity(intent)
        }
    }
}