package com.diskominfos.subakbali.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.diskominfos.subakbali.adapter.SubakAdapter
import com.diskominfos.subakbali.api.DataSubak
import com.diskominfos.subakbali.databinding.FragmentHomeBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.data.DataViewModel
import com.diskominfos.subakbali.ui.profil.ProfilViewModel

private lateinit var context: Context
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class HomeFragment : Fragment() {
    private lateinit var profilViewModel: ProfilViewModel
    private lateinit var dataViewModel: DataViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupViewModel()
        setupRecyclerView()
        return root
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        binding.rvSubak.layoutManager = layoutManager
        binding.rvSubak.setHasFixedSize(true)
    }

    private fun setupViewModel() {
        val profilViewModel: ProfilViewModel by viewModels {
            val pref = requireContext().dataStore
            ViewModelFactory(
                UserPreference.getInstance(pref),
                requireContext()
            )
        }

        dataViewModel = ViewModelProvider(this)[DataViewModel::class.java]
        profilViewModel.getUser().observe(viewLifecycleOwner) {

            if (it != "") {
                dataViewModel.getSubak(it)
                dataViewModel.subakList.observe(viewLifecycleOwner) { subak ->
                    getList(subak)
                    if (it != null) {
//                        showLoading(false)
                    }
                    Log.d("error", "setupViewModel: $subak")
                }
            } else {
                val intent = Intent(activity, LoginActivity::class.java)
                activity?.startActivity(intent)
            }
        }
    }

    private fun getList(subak: MutableList<DataSubak>) {
        binding.rvSubak.layoutManager = GridLayoutManager(context, 2)
        val subakAdapter = SubakAdapter(subak)
        binding.rvSubak.adapter = subakAdapter
//        subakAdapter.setOnClickCallBack(object : SubakAdapter.OnItemClickCallback {
//            override fun onItemClicked(data: DataSubak) {
//                Intent(this@HomeFragment, DetailStoryActivity::class.java).also {
//                    it.putExtra(DetailStoryActivity.NAME, data.name)
//                    it.putExtra(DetailStoryActivity.ID, data.id)
//                    it.putExtra(DetailStoryActivity.DESCRIPTION, data.description)
//                    it.putExtra(DetailStoryActivity.PHOTO, data.photoUrl)
//                    startActivity(it)
//                }
//            }
//        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}