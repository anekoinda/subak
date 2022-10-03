package com.diskominfos.subakbali.ui.profil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.databinding.FragmentDataBinding
import com.diskominfos.subakbali.databinding.FragmentProfilBinding
import com.diskominfos.subakbali.ui.data.DataViewModel

class ProfilFragment : Fragment() {
    private var _binding: FragmentProfilBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dataViewModel =
            ViewModelProvider(this)[ProfilViewModel::class.java]

        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}