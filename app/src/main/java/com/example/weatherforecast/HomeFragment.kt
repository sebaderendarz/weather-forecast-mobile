package com.example.weatherforecast

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weatherforecast.data.remote.WeatherApiService
import com.example.weatherforecast.databinding.FragmentHomeBinding
import kotlinx.coroutines.MainScope

// TODO
// 1. save input text when orientation changes.
// 2. save search result when orientation changes?
// 3. Add request sending to fetch forecast and update detailsFragment. How to update fragment?
//      Add method like "updateDetails()" and call it each time when details change, even when
//      fragment was just created?

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null;
    private val binding get() = _binding!!
    private val weatherDetailsFragment: WeatherDetailsFragment = WeatherDetailsFragment()
    private val apiService = WeatherApiService.create()
    private val mainScope = MainScope()
    private val inputTextKey = "textInputText"
    //private val isInputFocusedKey = "isInputFocused"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.textInputLayout.setEndIconOnClickListener {
            val imm: InputMethodManager = binding.textInputText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm != null && imm.isActive){
                imm.hideSoftInputFromWindow(binding.textInputText.windowToken, 0)
            }
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
            binding.textInputLayout.clearFocus()
        }

        childFragmentManager.beginTransaction().apply {
            replace(R.id.weatherDetailsLayout, weatherDetailsFragment)
            commit()
        }

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //outState.putBoolean(isInputFocusedKey, binding.textInputText.isFocused)
        outState.putString(inputTextKey, binding.textInputText.text.toString())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val savedInput = savedInstanceState?.getString(inputTextKey)
        if (savedInput != null){
            //binding.textInputText.requestFocus()
            binding.textInputText.setText(savedInput)
        }
//        val isFocused = savedInstanceState?.getBoolean(isInputFocusedKey)
//        if (isFocused != null && isFocused == false){
//            binding.textInputText.clearFocus()
//        }
    }
}
