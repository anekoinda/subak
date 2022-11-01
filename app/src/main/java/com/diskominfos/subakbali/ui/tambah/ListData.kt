package com.diskominfos.subakbali.ui.tambah

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.databinding.ActivityListDataBinding
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmum

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
    }
}