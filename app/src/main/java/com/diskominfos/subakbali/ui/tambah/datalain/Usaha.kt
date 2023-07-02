package com.diskominfos.subakbali.ui.tambah.datalain

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.diskominfos.subakbali.databinding.ActivityUsahaBinding
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel

class Usaha : AppCompatActivity() {
    private lateinit var binding: ActivityUsahaBinding
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private var getIdSubak: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsahaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIdSubak = intent.getStringExtra("idsubak") // Retrieve the string value from the Intent
        Log.e("id sumber airrr", "$getIdSubak")
        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnAddUsaha.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                Log.e("id subak", "$getIdSubak")
                val intent = Intent(this, AddUsaha::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
            }
        }
    }
}