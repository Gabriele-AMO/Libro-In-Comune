package com.example.progetto.Preferiti

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.progetto.R
import com.example.progetto.Retrofit
import com.example.progetto.databinding.FConfermaRimozioneBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentConfermaRimozione : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FConfermaRimozioneBinding.inflate(inflater)
        // Inflate the layout for this fragment
        val manager = parentFragmentManager

        val args = arguments
        val text1 = args?.getString("ISBN")//ISBN

        binding.buttonCancellaPreferito.setOnClickListener() {
            if (text1 != null) {
                parseJSON(text1)
            }
            val fragment = FragmentListaPreferiti()

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.FCVLibriInPrestito, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.buttonTornaIndietro.setOnClickListener() {
            manager.popBackStack()
        }

        return binding.root
    }

    private fun parseJSON(ISBN: String) {

        val id_utente = arguments?.getString("id")

        Log.i("VALORE", id_utente.toString())
        Log.i("VALORE", ISBN)


        // Esegui la query per ottenere i dati JSON
        val query = "DELETE FROM PRG_Preferiti " +
                "WHERE FK_Codice_utente = '$id_utente' " +
                "AND FK_ISBN = '$ISBN'"

        Log.i("Query", query)

        Retrofit.retrofit.remove(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        Log.d("response: ", "Sono destro isSuccesful")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    // Gestisci l'errore di chiamata
                }
            }
        )
    }


}