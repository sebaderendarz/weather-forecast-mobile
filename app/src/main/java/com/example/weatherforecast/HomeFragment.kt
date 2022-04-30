package com.example.weatherforecast

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.google.android.material.textfield.TextInputEditText


class HomeFragment : Fragment() {

    private lateinit var androidViewModel: WeatherForecastAndroidViewModel
    private lateinit var weatherDetailsFragment: WeatherDetailsFragment
    private var _binding: FragmentHomeBinding? = null;
    private val binding get() = _binding!!
    private val inputTextKey = "textInputText"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (savedInstanceState == null) {
            weatherDetailsFragment  = WeatherDetailsFragment()
            childFragmentManager.beginTransaction().apply {
                replace(R.id.weatherDetailsLayout, weatherDetailsFragment)
                commit()
            }
        } else {
            weatherDetailsFragment = childFragmentManager.findFragmentById(R.id.weatherDetailsLayout) as WeatherDetailsFragment
        }

        configureAndroidViewModel()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        configureBindingListeners()
        return binding.root
    }

    private fun configureAndroidViewModel(){
        androidViewModel = ViewModelProvider(requireActivity()).get(WeatherForecastAndroidViewModel::class.java)
        androidViewModel.currentSearchLocation.observe(viewLifecycleOwner) {
            if (it != null) {
                weatherDetailsFragment.updateFragmentContent(it)
            }
        }
    }

    private fun configureBindingListeners(){
        binding.textInputLayout.setEndIconOnClickListener {
            val imm: InputMethodManager = binding.textInputText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isActive){
                imm.hideSoftInputFromWindow(binding.textInputText.windowToken, 0)
            }
            binding.textInputLayout.clearFocus()
            val locationName = binding.textInputText.text.toString()
            if (locationName != "") androidViewModel.searchLocationForecast(locationName, RequestType.SEARCH)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Using view instead of binding.textInputText solved issue with NullPointerException when
        // 1. go to favourites 2. go back to home 3. change to horizontal 4. search for location
        // 5. change back to vertical 6. get exception
        // But I don't like this solution. It is a workaround.
        val view = activity?.findViewById<TextInputEditText>(R.id.textInputText)
        outState.putString(inputTextKey, view?.text.toString())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val savedInput = savedInstanceState?.getString(inputTextKey)
        if (savedInput != null){
            binding.textInputText.setText(savedInput)
        }
        binding.textInputText.setSelection(binding.textInputText.length())
    }
}
