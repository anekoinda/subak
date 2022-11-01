package com.diskominfos.subakbali

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.diskominfos.subakbali.databinding.ActivityAddDataUmumBinding
import com.diskominfos.subakbali.databinding.ActivityListDataBinding

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