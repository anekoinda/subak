package com.diskominfos.subakbali.ui.tambah.datalain

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.ui.tambah.dokumen.SectionsPagerAdapter
import com.diskominfos.subakbali.databinding.ActivityAddDokumenBinding
import com.diskominfos.subakbali.ui.tambah.dokumen.AddAwig
import com.diskominfos.subakbali.ui.tambah.dokumen.AddPerarem
import com.diskominfos.subakbali.ui.tambah.dokumen.AddSuratKeputusan

class AddDataLain : AppCompatActivity() {

    private lateinit var binding: ActivityAddDokumenBinding
    private lateinit var pager: ViewPager // creating object of ViewPager
    private lateinit var tab: TabLayout  // creating object of TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddDokumenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pager = findViewById(R.id.viewPager)
        tab = findViewById(R.id.tabs)

        supportActionBar?.hide()

        // Initializing the ViewPagerAdapter
        val adapter = SectionsPagerAdapter(supportFragmentManager)

        // add fragment to the list
        adapter.addFragment(AddProduk(), "Produk")
        adapter.addFragment(AddUsaha(), "Usaha")
        adapter.addFragment(AddAlihFungsi(), "Alih Fungsi")

        // Adding the Adapter to the ViewPager
        pager.adapter = adapter

        // bind the viewPager with the TabLayout.
        tab.setupWithViewPager(pager)
    }
}