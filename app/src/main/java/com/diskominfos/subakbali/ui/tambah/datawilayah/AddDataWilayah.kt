package com.diskominfos.subakbali.ui.tambah.datawilayah

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.databinding.ActivityAddDataWilayahBinding
import com.diskominfos.subakbali.databinding.ActivityListDataBinding

class AddDataWilayah : AppCompatActivity() {
    private lateinit var binding: ActivityAddDataWilayahBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_data_wilayah)

        binding = ActivityAddDataWilayahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPolygon.setOnClickListener {
            val intent = Intent(this, AddPolygonSubak::class.java)
            startActivity(intent)
        }
    }
}