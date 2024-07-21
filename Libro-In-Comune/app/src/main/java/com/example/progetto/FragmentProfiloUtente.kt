package com.example.progetto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.progetto.databinding.FProfiloUtenteBinding
import com.example.progetto.databinding.FRicercaLibroBinding

class FragmentProfiloUtente : Fragment() {

    private lateinit var binding: FProfiloUtenteBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FProfiloUtenteBinding.inflate(inflater, container, false)
        val view = binding.root

        val id = arguments?.getString("id")
        val nome = arguments?.getString("nome")
        val telefono = arguments?.getString("telefono")
        val email = arguments?.getString("email")
        val cognome = arguments?.getString("cognome")


        binding.textViewCognome.text = cognome
        binding.textViewNome.text = nome
        binding.textViewCodiceUtente.text = id
        binding.textViewEmail.text = email
        binding.textViewNumeroTelefono.text = telefono

        return view

    }

}