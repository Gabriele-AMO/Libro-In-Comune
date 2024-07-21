package com.example.progetto.Eventi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.progetto.R
import com.example.progetto.databinding.FEventoBinding
import com.example.progetto.databinding.FLibroBinding

class FragmentEvento : Fragment() {
    private lateinit var binding: FEventoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding = FEventoBinding.inflate(inflater, container, false)

        val args = arguments
        val text1 = args?.getString("key1")
        val text2 = args?.getString("key2")
        val text3 = args?.getString("key3")

        binding.titoloLibro.text = text1
        binding.textDataInizioEvento.text = text2
        // Utilizza i dati come desiderato


        return binding.root
        onBackPressed()
    }

    fun onBackPressed(){
        val manager = parentFragmentManager
        manager.popBackStack()
    }
}