package com.diskominfos.subakbali.ui.tambah

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.databinding.FragmentJenisBinding

class JenisFragment : Fragment() {
    private var _binding: FragmentJenisBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val jenisViewModel =
            ViewModelProvider(this)[JenisViewModel::class.java]

        _binding = FragmentJenisBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.jenisSawah.setOnClickListener{
            val intent = Intent (activity, ListData::class.java)
            activity?.startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}