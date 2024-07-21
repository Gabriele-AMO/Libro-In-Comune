package com.example.progetto.RicercaLibri

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.progetto.LibriInPrestito.ItemsVMLibriInPrestito
import com.example.progetto.Retrofit
import com.example.progetto.databinding.FLibroNuovoBinding
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentLibroNuovo : Fragment() {

    private lateinit var binding: FLibroNuovoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FLibroNuovoBinding.inflate(inflater, container, false)

        val args = arguments
        val text1 = args?.getString("key1")//ISBN
        val text2 = args?.getString("key2")//titolo
        val text3 = args?.getString("key3")//genere

        val id_utente = arguments?.getString("id")

        if (text1 != null) {
            Descrizione(text1)
        }

        binding.titoloLibro.text = text2

        binding.buttonPrenotazio?.setOnClickListener() {
            if (text1 != null && text2 != null && text3 != null && id_utente != null) {
                Prenotazione(text1, text2, text3, id_utente)
            }
        }

        binding.buttonPreferitiLibroNuovo.setOnClickListener() {
            if (text1 != null) {
                Preferiti(text1)
            }
        }

        return binding.root

    }

    fun Prenotazione(ISBN: String, titolo: String, genere: String, id_utente: String) {


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

                            val queryControllo = "SELECT PRG_In_prestito.FK_ISBN " +
                                    "FROM PRG_In_prestito " +
                                    "WHERE PRG_In_prestito.FK_ISBN = '$ISBN' AND FK_Codice_utente = '$id_utente' "

                            val queryInsert = "INSERT INTO PRG_In_prestito" +
                                    " (FK_ISBN, Numero_copia, FK_Codice_utente, Data_inizio, Data_fine_prevista, Proroga, Codice_restituzione) " +
                                    "VALUES" +
                                    " ('$ISBN', '1', '$id_utente', CURDATE(),DATE_ADD(CURDATE() ,INTERVAL 1 MONTH), '0' ,SUBSTR(MD5(RAND()), 1, 10))"

                            Log.i("Query", queryInsert)

                            Retrofit.retrofit.select(queryControllo).enqueue(
                                object : Callback<JsonObject> {
                                    override fun onResponse(
                                        call: Call<JsonObject>,
                                        response: Response<JsonObject>
                                    ) {
                                        if (response.isSuccessful) {
                                            Log.i("RESPONSE", "SONO DENTRO ISSUCCESSFUL CONTROLLO")
                                            val result = (response.body()?.get("queryset") as JsonArray)
                                            if (result.size() == 0) {
                                                Log.i("RESPONSE", "SONO DENTRO SIZE == 0")
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

    private fun Preferiti(ISBN: String) {

        val id_utente = arguments?.getString("id")

        val queryControllo = "SELECT PRG_Preferiti.FK_ISBN " +
                "FROM PRG_Preferiti " +
                "WHERE PRG_Preferiti.FK_ISBN = '$ISBN' AND FK_Codice_utente = '$id_utente' "


        // Esegui la query per ottenere i dati JSON
        val query = "SELECT PRG_Libro.Titolo, PRG_Libro.Genere, PRG_Libro.Autore " +
                "FROM PRG_Libro " +
                "WHERE PRG_Libro.ISBN = '$ISBN' "

        Retrofit.retrofit.select(queryControllo).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        Log.d("response: ", "Sono destro isSuccesful")
                        val result = (response.body()?.get("queryset") as JsonArray)
                        if (result.size() == 0) {

                            Retrofit.retrofit.select(query).enqueue(
                                object : Callback<JsonObject> {
                                    override fun onResponse(
                                        call: Call<JsonObject>,
                                        response: Response<JsonObject>
                                    ) {
                                        if (response.isSuccessful) {
                                            Log.d("response: ", "Sono destro isSuccesful")
                                            val result =
                                                (response.body()?.get("queryset") as JsonArray)
                                            if (result.size() == 1) {
                                                Log.d("response: ", "Size >=1")
                                                for (i in result) {
                                                    val items = i as JsonObject

                                                    val titolo =
                                                        items.get("Titolo").asString ?: "N/A"
                                                    Log.d("Titolo: ", titolo)

                                                    val genere =
                                                        items.get("Genere").asString ?: "N/A"
                                                    Log.d("Genere: ", genere)

                                                    val autore =
                                                        items.get("Autore").asString ?: "N/A"
                                                    Log.d("Autore: ", autore)

                                                    val queryInsert =
                                                        "INSERT INTO PRG_Preferiti (Titolo, FK_Codice_utente, Genere, Autore, FK_ISBN) " +
                                                                "VALUES ('$titolo', '$id_utente', '$genere', '$autore', '$ISBN')"

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
                                                                        "Aggiunto ai preferiti ",
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

                                                }

                                            }
                                        }
                                    }

                                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                        // Gestisci l'errore di chiamata
                                    }
                                }
                            )
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Gia presente nei tuoi preferiti ",
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

            }

        )
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
                                binding.textViewDescrizione?.text = descrizione

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


}