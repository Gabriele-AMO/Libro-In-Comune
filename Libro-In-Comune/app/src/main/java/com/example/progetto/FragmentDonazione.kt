package com.example.progetto

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.example.progetto.databinding.FDonazioneBinding
import android.widget.Toast
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentDonazione : Fragment() {

    private lateinit var binding: FDonazioneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FDonazioneBinding.inflate(inflater, container, false)
        val view = binding.root
        var message: String = "ERRORE: Devi compilare tutti i campi --"
        var nErrori: Int = 0


        val radioButton1: RadioButton = binding.radioButtonPessimo
        val radioButton2: RadioButton = binding.radioButtonBuono
        val radioButton3: RadioButton? = binding.radioButtonOttimo


        binding.radioButtonBuono.isChecked = true
        var condizione = "Buono"

        binding.radioGroup2.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.radioButtonPessimo.id -> {
                    // Logica da eseguire quando radioButton1 è selezionato
                    condizione = "Pessimo"
                }

                binding.radioButtonBuono.id -> {
                    // Logica da eseguire quando radioButton2 è selezionato
                    condizione = "Buono"
                }

                binding.radioButtonOttimo?.id -> {
                    // Logica da eseguire quando radioButton3 è selezionato
                    condizione = "Ottimo"
                }
            }
        }

        binding.buttonDona.setOnClickListener() {

            val titolo: String? = binding.editTextTitolo?.text.toString()
            val autore: String? = binding.editTextAutore?.text.toString()
            val casaEditrice: String? = binding.editTextCasaEditrice?.text.toString()

            if (!titolo.isNullOrEmpty() && !autore.isNullOrEmpty() && !casaEditrice.isNullOrEmpty()) {

                parseJSON(titolo, autore, condizione, casaEditrice)
                Toast.makeText(requireContext(), "Ti ringraziamo per aver donato! Verrai ricontattato da un nostro operatore.", Toast.LENGTH_SHORT).show()

            } else {
                message = "ERRORE: Devi compilare tutti i campi --"

                if (titolo.isNullOrEmpty()) {
                    message += "Titolo assente--"
                    nErrori++
                }

                if (autore.isNullOrEmpty()) {
                    message += "Autore assente--"
                    nErrori++
                }

                if (casaEditrice.isNullOrEmpty()) {
                    message += "Casa Editrice assente--"
                    nErrori++
                }

                val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
                toast.show()

            }
        }

        // Inflate the layout for this fragment
        return view
    }

    private fun parseJSON(
        titolo: String,
        autore: String,
        condizione: String,
        casaEditrice: String
    ) {
        // Esegui la query per ottenere fare l'update JSON

        Log.i("parse", "Sono dentro JSONPARSE")

        val query =
            "INSERT INTO PRG_Donazione ( titolo_libro, autore, condizione, casa_editrice ) VALUES ('$titolo','$autore', '$condizione', '$casaEditrice')"

        Retrofit.retrofit.insert(query).enqueue(
            object : Callback<JsonObject> {

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        Log.i("response", "Sono dentro isSuccefull")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    // Gestisci l'errore di chiamata
                }
            }
        )
    }
}