package com.example.progetto.HomePage

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.progetto.Eventi.FragmentEventi
import com.example.progetto.FragmentBiblioteca
import com.example.progetto.LibriInPrestito.FragmentLibriInPrestito
import com.example.progetto.R
import com.example.progetto.Retrofit
import com.example.progetto.databinding.HomePageBinding
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePage : AppCompatActivity() {
    private lateinit var binding: HomePageBinding
    val manager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {

        //savedInstanceState Usato per il salvataggio del contesto di una Activity
        //Invocato prima che l'activity venga distrutta
        super.onCreate(savedInstanceState)
        binding = HomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Estrai i dati dall'intent
        val id = intent.getStringExtra("id")
        val nome = intent.getStringExtra("nome")
        val cognome = intent.getStringExtra("cognome")
        val telefono = intent.getStringExtra("telefono")
        val email = intent.getStringExtra("email")
        val psw = intent.getStringExtra("psw")
        val abilitato = intent.getStringExtra("abilitato")

        binding.buttonLibri.setOnClickListener() {
            val fragment = FragmentLibriInPrestito()

            val args = Bundle()
            args.putString("id",id)
            args.putString("abilitato",abilitato)
            fragment.arguments=args

            manager.beginTransaction()
                .replace(R.id.FCVLibriInPrestito, fragment)
                .addToBackStack(null)
                .commit()
        }

        if(abilitato == "1") {


            binding.buttonPosto.setOnClickListener() {
                val fragment = FragmentBiblioteca()
                val args = Bundle()
                args.putString("id",id)
                fragment.arguments=args

                manager.beginTransaction()
                    .replace(R.id.FCVLibriInPrestito, fragment)
                    .addToBackStack(null)
                    .commit()
            }

            binding.buttonEventi.setOnClickListener() {
                val fragment = FragmentEventi()

                manager.beginTransaction()
                    .replace(R.id.FCVLibriInPrestito, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }


        binding.buttonMenu.setOnClickListener(){
            val fragment = FragmentTendina()

            val args = Bundle()
            args.putString("nome",nome)
            args.putString("telefono",telefono)
            args.putString("email",email)
            args.putString("id",id)
            args.putString("cognome",cognome)
            args.putString("abilitato",abilitato)
            fragment.arguments=args

            manager.beginTransaction()
                .replace(R.id.FCVLibriInPrestito,fragment)
                .addToBackStack(null)
                .commit()
        }


    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setContentView(R.layout.activity_login) // Imposta nuovamente il layout dell'activity se necessario
        // Effettua altre operazioni di aggiornamento, se necessario
    }



}