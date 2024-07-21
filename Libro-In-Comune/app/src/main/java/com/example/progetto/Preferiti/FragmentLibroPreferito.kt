package com.example.progetto.Preferiti

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.progetto.R
import com.example.progetto.Retrofit
import com.example.progetto.databinding.FLibroPreferitoBinding
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentLibroPreferito : Fragment() {

    private lateinit var binding: FLibroPreferitoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FLibroPreferitoBinding.inflate(inflater, container, false)

        val args = arguments
        val text1 = args?.getString("key1")//ISBN
        val text2 = args?.getString("key2")//TITOLO
        val text3 = args?.getString("key3")//GENERE

        if (text1 != null) {
            Descrizione(text1)
        }

        binding.titoloLibro.text = text2

        binding.buttonEliminaPreferito.setOnClickListener(){
            val fragment = FragmentConfermaRimozione()

            val bundle = Bundle()

            bundle.putString("ISBN", text1)

            val id_utente = arguments?.getString("id")
            bundle.putString("id",id_utente)

            fragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.FCVLibriInPrestito, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.buttonPrenotaPreferito.setOnClickListener(){
            if (text3 != null && text1 != null && text2 != null ) {
                Prenotazione(text1,text2,text3)
            }
        }

        return binding.root
    }

    fun Descrizione(ISBN: String) {

        val query = "SELECT PRG_Libro.Descrizione " +
                "FROM PRG_Libro " +
                "WHERE PRG_Libro.ISBN= '$ISBN'"

        Retrofit.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        Log.d("response: ", "Sono destro isSuccesful")
                        val result = (response.body()?.get("queryset") as JsonArray)
                        if (result.size() >= 1) {
                            Log.d("response: ", "Size >=1")
                            for (i in result) {
                                val items = i as JsonObject

                                val descrizione = items.get("Descrizione").asString ?: "N/A"
                                Log.d("Descrizione: ", descrizione)
                                binding.textViewDescrizione.text = descrizione

                            }

                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    // Gestisci l'errore di chiamata
                }
            }
        )
    }

    fun Prenotazione(ISBN: String, titolo: String, genere: String) {

        val id_utente = arguments?.getString("id")

        if (id_utente != null) {
            Log.i("ID", id_utente)
        }

        val query = "SELECT PRG_Libro.ISBN " +
                "FROM PRG_Libro " +
                "WHERE PRG_Libro.titolo = '$titolo' AND " +
                "PRG_Libro.ISBN = '$ISBN' " +
                "AND PRG_Libro.Numero_copie > 0"

        Retrofit.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        Log.d("response: ", "Sono destro isSuccesful")
                        val result = (response.body()?.get("queryset") as JsonArray)
                        if (result.size() >= 1) {
                            Log.d("response: ", "Size >=1")
                            for (i in result) {

                                val items = i as JsonObject


                                val queryControllo = "SELECT PRG_In_prestito.FK_ISBN " +
                                        "FROM PRG_In_prestito " +
                                        "WHERE PRG_In_prestito.FK_ISBN = '$ISBN' AND FK_Codice_utente = '$id_utente' "

                                val queryInsert = "INSERT INTO PRG_In_prestito" +
                                        " (FK_ISBN, Numero_copia, FK_Codice_utente, Data_inizio, Data_fine_prevista, Proroga, Codice_restituzione) " +
                                        "VALUES" +
                                        " ('$ISBN', '1', '$id_utente', CURDATE(),DATE_ADD(CURDATE() ,INTERVAL 1 MONTH), 0 ,(SUBSTR(MD5(RAND()), 1, 10)))"

                                Log.i("Query", queryInsert)

                                Retrofit.retrofit.select(queryControllo).enqueue(
                                    object : Callback<JsonObject> {
                                        override fun onResponse(
                                            call: Call<JsonObject>,
                                            response: Response<JsonObject>
                                        ) {
                                            if (response.isSuccessful) {
                                                val result =
                                                    (response.body()?.get("queryset") as JsonArray)
                                                if (result.size() == 0) {
                                                    Retrofit.retrofit.insert(queryInsert).enqueue(
                                                        object : Callback<JsonObject> {
                                                            override fun onResponse(
                                                                call: Call<JsonObject>,
                                                                response: Response<JsonObject>
                                                            ) {
                                                                if (response.isSuccessful) {
                                                                    Log.i(
                                                                        "response insert",
                                                                        "Sono dentro isSuccefull insert"
                                                                    )
                                                                    Toast.makeText(
                                                                        requireContext(),
                                                                        "Libro prenotato ",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                }
                                                            }

                                                            override fun onFailure(
                                                                call: Call<JsonObject>,
                                                                t: Throwable
                                                            ) {
                                                                // Gestisci l'errore di chiamata
                                                            }
                                                        }
                                                    )
                                                } else {

                                                    Toast.makeText(
                                                        requireContext(),
                                                        "Gia prenotato",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<JsonObject>,
                                            t: Throwable
                                        ) {
                                            // Gestisci l'errore di chiamata
                                        }
                                    })


                                Log.i("Response insert", "Ce l'hai fatta insert")
                            }

                        } else {

                            Toast.makeText(
                                requireContext(),
                                "Non disponibile",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    // Gestisci l'errore di chiamata
                }
            }
        )
    }

}