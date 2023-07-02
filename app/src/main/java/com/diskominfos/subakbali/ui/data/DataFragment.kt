package com.diskominfos.subakbali.ui.data

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony.Sms.Draft
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.diskominfos.subakbali.DraftSubak
import com.diskominfos.subakbali.adapter.SubakAdapter
import com.diskominfos.subakbali.adapter.TempSubakAdapter
import com.diskominfos.subakbali.api.DataTempSubak
import com.diskominfos.subakbali.api.TempSubakResponse
import com.diskominfos.subakbali.databinding.FragmentDataBinding
import com.diskominfos.subakbali.model.SubakPreference
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.profil.ProfilViewModel
import com.diskominfos.subakbali.ui.tambah.ListData
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import com.diskominfos.subakbali.ui.tambah.dataumum.DetailTempSubak

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataFragment : Fragment() {
    private var _binding: FragmentDataBinding? = null
    private val binding get() = _binding!!
    private var created_by: String = ""
    private lateinit var dataViewModel: DataViewModel
    private lateinit var setAdapter: TempSubakAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDataBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.subak.setOnClickListener {
            val intent = Intent(activity, DraftSubak::class.java)
            activity?.startActivity(intent)
        }

        setupViewModel(created_by)
        setupRecyclerView()
        return root
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
//        binding.rvSubak.layoutManager = layoutManager
//        binding.rvSubak.setHasFixedSize(true)
    }

    private fun setupViewModel(created_by: String) {
        dataViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(requireContext().dataStore))
        )[DataViewModel::class.java]
        dataViewModel = ViewModelProvider(this)[DataViewModel::class.java]
        dataViewModel.getUser().observe(viewLifecycleOwner) { it ->
            if (it != "") {
                dataViewModel.getListTempSubak(it)
                Log.e("token", it)
                Log.e("created_by", created_by)
                dataViewModel.tempSubakList.observe(viewLifecycleOwner) { tempSubak ->
                    getList(tempSubak)
                    Log.d("error", "setupViewModel: $tempSubak")
                }
            } else {
                val intent = Intent(activity, LoginActivity::class.java)
                activity?.startActivity(intent)
            }
        }
    }

    private fun getList(tempSubak: MutableList<DataTempSubak>) {
        val tempSubakAdapter = TempSubakAdapter(tempSubak,
            object : TempSubakAdapter.OnAdapterAllTempSubakListener {
                override fun onClick(data: DataTempSubak) {
                    val bundle = Bundle()
                    val intent = Intent(context, DetailTempSubak::class.java)
                    bundle.putString("id", data.id)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            })
//        binding.rvSubak.adapter = tempSubakAdapter
//        tempSubakAdapter.setOnClickCallBack(object : TempSubakAdapter.OnItemClickCallback {
//            override fun onClick(data: DataTempSubak) {
//                Intent(requireContext(), DetailTempSubak::class.java).also {
//                    it.putExtra("id", data.id)
//                    Log.e("id frag", "$id")
//                    startActivity(it)
//                }
////                val bundle = Bundle()
////                val intent = Intent(requireContext(), DetailTempSubak::class.java)
////                bundle.putString("id", data.id)
////                Log.e("id okama", data.id)
////                intent.putExtras(bundle)
////                startActivity(intent)
//            }
//        })

//        setAdapter = tempSubak.let {
//            TempSubakAdapter(it,
//                object : TempSubakAdapter.OnAdapterAllTempSubakListener{
//                    override fun onClick(data: DataTempSubak) {
//                        val bundle = Bundle()
//                        val intent = Intent(context, DetailTempSubak::class.java)
//                        bundle.putString("id", data.id)
//                        intent.putExtras(bundle)
//                        startActivity(intent)
//                    }
//                })
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}