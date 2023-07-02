package com.diskominfos.subakbali

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.diskominfos.subakbali.adapter.SubakAdapter
import com.diskominfos.subakbali.adapter.TempSubakAdapter
import com.diskominfos.subakbali.api.DataSubak
import com.diskominfos.subakbali.api.DataTempSubak
import com.diskominfos.subakbali.databinding.ActivityDraftSubakBinding
import com.diskominfos.subakbali.databinding.ActivityListDataBinding
import com.diskominfos.subakbali.model.SubakPreference
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.data.DataViewModel
import com.diskominfos.subakbali.ui.profil.ProfilViewModel
import com.diskominfos.subakbali.ui.tambah.ListData
import com.diskominfos.subakbali.ui.tambah.dataumum.DetailTempSubak

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DraftSubak : AppCompatActivity() {
    private lateinit var binding: ActivityDraftSubakBinding
    private lateinit var dataViewModel: DataViewModel
    private lateinit var setAdapter: TempSubakAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDraftSubakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.fabSubak.setOnClickListener{
            val intent = Intent(this@DraftSubak, ListData::class.java)
            startActivity(intent)
        }

        setupViewModel()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvSubak.layoutManager = layoutManager
        binding.rvSubak.setHasFixedSize(true)
    }

    private fun setupViewModel() {
        val profilViewModel: ProfilViewModel by viewModels {
            val pref = this.dataStore
            val subak_pref = this.dataStore
            ViewModelFactory(
                UserPreference.getInstance(pref)
            )
        }

        val dataViewModel: DataViewModel by viewModels {
            val pref = this.dataStore
            val subak_pref = this.dataStore
            ViewModelFactory(
                UserPreference.getInstance(pref)
            )
        }

//        dataViewModel = ViewModelProvider(this)[DataViewModel::class.java]
        profilViewModel.getUser().observe(this) {

            if (it != "") {
                dataViewModel.getListTempSubak(it)
                var getName: String = ""
                profilViewModel.getName().observe(this) { it ->
                    getName = it
//                    binding.namaUser.text = getName
                }
                dataViewModel.tempSubakList.observe(this) { tempSubak ->
                    getList(tempSubak)
                    if (it != null) {
//                        showLoading(false)
                    }
                    Log.d("error", "setupViewModel: $tempSubak")
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                this.startActivity(intent)
            }
        }
    }

    private fun getList(tempSubak: MutableList<DataTempSubak>) {
        val tempSubakAdapter = TempSubakAdapter(tempSubak,
            object : TempSubakAdapter.OnAdapterAllTempSubakListener {
                override fun onClick(data: DataTempSubak) {
                    val bundle = Bundle()
                    val intent = Intent(this@DraftSubak, DetailTempSubak::class.java)
                    bundle.putString("id", data.id)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            })
        binding.rvSubak.adapter = tempSubakAdapter
//        tempSubakAdapter.setOnClickCallBack(object : TempSubakAdapter.OnItemClickCallback {
//            override fun onClick(data: DataTempSubak) {
//                Intent(this@DraftSubak, DetailTempSubak::class.java).also {
//                    it.putExtra("id", data.id)
//                    Log.e("id frag", "$id")
//                    startActivity(it)
//                }
//                val bundle = Bundle()
//                val intent = Intent(this@DraftSubak, DetailTempSubak::class.java)
//                bundle.putString("id", data.id)
//                Log.e("id okama", data.id)
//                intent.putExtras(bundle)
//                startActivity(intent)
//            }
//        })

        setAdapter = tempSubak.let {
            TempSubakAdapter(it,
                object : TempSubakAdapter.OnAdapterAllTempSubakListener {
                    override fun onClick(data: DataTempSubak) {
                        val bundle = Bundle()
                        val intent = Intent(this@DraftSubak, DetailTempSubak::class.java)
                        bundle.putString("id", data.id)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                })
        }
    }
}