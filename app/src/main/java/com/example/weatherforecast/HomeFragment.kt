package com.example.weatherforecast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.weatherforecast.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null;
    private val binding get() = _binding!!
    private val weatherDetailsFragment: WeatherDetailsFragment = WeatherDetailsFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.textInputLayout.setEndIconOnClickListener {
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.textInputText.doOnTextChanged { text, _, _, _ ->
            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
        }

        childFragmentManager.beginTransaction().apply {
            replace(R.id.weatherDetailsLayout, weatherDetailsFragment)
            commit()
        }

        return binding.root
    }
}
