package com.diskominfos.subakbali.ui.profil

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.viewModels
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.databinding.FragmentProfilBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ProfilFragment : Fragment() {
    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupLogout()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupLogout() {
        binding.buttonLogout.setOnClickListener {
            val profilViewModel: ProfilViewModel by viewModels {
                val pref = requireContext().dataStore
                ViewModelFactory(
                    UserPreference.getInstance(pref), requireContext()
                )
            }
            profilViewModel.logout()
            val intent = Intent(activity, LoginActivity::class.java)
            activity?.startActivity(intent)
        }
    }
}
