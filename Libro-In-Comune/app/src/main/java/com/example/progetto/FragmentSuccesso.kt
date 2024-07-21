package com.example.progetto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.progetto.HomePage.FragmentBenvenuto
import com.example.progetto.LibriInPrestito.FragmentLibriInPrestito

import com.example.progetto.databinding.FSuccessoBinding

class FragmentSuccesso : Fragment() {

    private lateinit var binding: FSuccessoBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FSuccessoBinding.inflate(inflater, container, false)
        val view = binding.root

        val manager = parentFragmentManager

        binding.buttonRitornoHomepage.setOnClickListener(){
            val fragment= FragmentBenvenuto()
            manager.beginTransaction()
                .replace(R.id.FCVLibriInPrestito, fragment)
                .addToBackStack(null)
                .commit()
        }
        
        return view


    }

}