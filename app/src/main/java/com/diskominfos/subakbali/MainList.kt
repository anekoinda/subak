package com.diskominfos.subakbali

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.diskominfos.subakbali.databinding.ActivityAddDataOdalanBinding
import com.diskominfos.subakbali.databinding.ActivityMainListBinding
import com.diskominfos.subakbali.ui.tambah.ListData

class MainList : AppCompatActivity() {
    private lateinit var binding: ActivityMainListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.subak.setOnClickListener {
            Intent(this@MainList, DraftSubak::class.java).also {
                startActivity(it)
            }
        }
    }
}