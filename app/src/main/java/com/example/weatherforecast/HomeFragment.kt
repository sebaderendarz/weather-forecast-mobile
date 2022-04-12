package com.example.weatherforecast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class HomeFragment : Fragment() {

//    private var _binding = null;
//    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null;
//    }
}