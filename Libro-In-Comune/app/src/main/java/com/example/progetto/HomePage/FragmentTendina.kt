package com.example.progetto.HomePage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.progetto.FragmentDonazione
import com.example.progetto.Preferiti.FragmentListaPreferiti
import com.example.progetto.FragmentProfiloUtente
import com.example.progetto.Login
import com.example.progetto.R
import com.example.progetto.RicercaLibri.FragmentRicercaLibro
import com.example.progetto.databinding.FTendinaBinding


class FragmentTendina : Fragment() {

    private lateinit var binding: FTendinaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FTendinaBinding.inflate(inflater, container, false)
        val view = binding.root

        val id = arguments?.getString("id")
        val nome = arguments?.getString("nome")
        val telefono = arguments?.getString("telefono")
        val email = arguments?.getString("email")
        val cognome = arguments?.getString("cognome")
        val abilitato = arguments?.getString("abilitato")

        if (id != null) {
            Log.i("ID",id)
        }

        val managerchild = parentFragmentManager

        if(abilitato == "1") {

            binding.buttonNuovaPrenotazione.setOnClickListener() {
                val fragment = FragmentRicercaLibro()

                val args = Bundle()
                args.putString("id", id)
                fragment.arguments=args

                managerchild.beginTransaction()
                    .replace(R.id.FCVLibriInPrestito, fragment)
                    .addToBackStack(null)
                    .commit()
            }


            binding.buttonDonazione.setOnClickListener() {
                val fragment = FragmentDonazione()
                managerchild.beginTransaction()
                    .replace(R.id.FCVLibriInPrestito, fragment)
                    .addToBackStack(null)
                    .commit()
            }


            binding.buttonListaPreferiti.setOnClickListener() {
                val fragment = FragmentListaPreferiti()

                val args = Bundle()
                args.putString("id", id)
                fragment.arguments = args

                managerchild.beginTransaction()
                    .replace(R.id.FCVLibriInPrestito, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        binding.buttonLogout.setOnClickListener() {
            val i = Intent(this.activity, Login::class.java)
            startActivity(i)
        }

        binding.buttonUtente.setOnClickListener() {
            val fragment = FragmentProfiloUtente()

            val args = Bundle()
            args.putString("nome", nome)
            args.putString("telefono", telefono)
            args.putString("email", email)
            args.putString("id", id)
            args.putString("cognome", cognome)
            fragment.arguments = args

            managerchild.beginTransaction()
                .replace(R.id.FCVLibriInPrestito, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}