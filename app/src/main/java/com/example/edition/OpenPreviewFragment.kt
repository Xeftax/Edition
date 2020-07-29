package com.example.edition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.edition.databinding.OpenPreviewFragmentBinding

class OpenPreviewFragment : Fragment() {

    private lateinit var binding: OpenPreviewFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = OpenPreviewFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.setOpenPreviewClickListener {
            findNavController().navigate(R.id.action_openPreviewFragment_to_picturePreviewFragment)
        }

    }
}