package com.example.progetto.LibriInPrestito

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.progetto.Retrofit
import com.example.progetto.databinding.FLibroBinding
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast
import com.example.progetto.R

class FragmentLibro : Fragment() {
    private lateinit var binding: FLibroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FLibroBinding.inflate(inflater, container, false)

        val args = arguments
        val text1 = args?.getString("key1")//ISBN
        val text2 = args?.getString("key2")//TITOLO
        val text3 = args?.getString("key3")//NUMERO COPIA
        val abilitato = arguments?.getString("abilitato")

        if (text1 != null) {
            parseJSON(text1)
        }


        binding.titoloLibro.text = text2

        if (abilitato == "1") {
            binding.buttonPreferiti.setOnClickListener() {
                if (text1 != null) {
                    Preferiti(text1)
                }
            }

            binding.buttonProroga.setOnClickListener() {
                if (text1 != null) {
                    proroga(text1)
                }
            }
        }

        binding.buttonRestituzione?.setOnClickListener() {
            val fragment = FragmentRestituzione()

            val args = Bundle()
            args.putString("ISBN", text1)
            fragment.arguments = args

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.FCVLibriInPrestito, fragment)
                .addToBackStack(null)
                .commit()
        }




        return binding.root
    }

    private fun Preferiti(ISBN: String) {

        val id_utente = arguments?.getString("id")

        val queryControllo = "SELECT PRG_Preferiti.FK_ISBN " +
                "FROM PRG_Preferiti " +
                "WHERE PRG_Preferiti.FK_ISBN = '$ISBN' AND FK_Codice_utente = '$id_utente' "


        // Esegui la query per ottenere i dati JSON
        val query = "SELECT PRG_Libro.Titolo, PRG_Libro.Genere, PRG_Libro.Autore " +
                "FROM PRG_Libro " +
                "WHERE PRG_Libro.ISBN ='$ISBN'"

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
                                                        Log.i("queryy", queryInsert)

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

    fun proroga(ISBN: String) {


        val id_utente = arguments?.getString("id")

        Log.i("ISBN e Cod_Utente", ISBN + id_utente)

        val querySelect = "SELECT PRG_In_prestito.Proroga " +
                "FROM PRG_In_prestito " +
                "WHERE PRG_In_prestito.Proroga = '0' " +
                "AND PRG_In_prestito.FK_Codice_utente = '$id_utente' " +
                "AND PRG_In_prestito.FK_ISBN = '$ISBN' "

        val queryUpdate = "UPDATE PRG_In_prestito " +
                "SET Data_fine_prevista = " +
                "DATE_ADD(Data_fine_prevista ,INTERVAL 10 DAY), Proroga = '1' " +
                "WHERE PRG_In_prestito.FK_ISBN = '$ISBN' " +
                "AND PRG_In_prestito.FK_Codice_utente = '$id_utente' " +
                "AND PRG_In_prestito.Proroga = 0"

        Retrofit.retrofit.select(querySelect).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val result = (response.body()?.get("queryset") as JsonArray)
                        if (result.size() >= 1) {
                            Retrofit.retrofit.update(queryUpdate).enqueue(
                                object : Callback<JsonObject> {
                                    override fun onResponse(
                                        call: Call<JsonObject>,
                                        response: Response<JsonObject>
                                    ) {
                                        if (response.isSuccessful) {

                                            Log.i("Response", "sono dentro isSuccesful")
                                            Toast.makeText(
                                                requireContext(),
                                                "Proroga di 10 giorni effettuata ",
                                                Toast.LENGTH_SHORT
                                            ).show()
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
                                "HAI GIA' EFFETTUATO LA PROROGA ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    // Gestisci l'errore di chiamata
                }
            })
    }

    private fun parseJSON(ISBN: String) {

        val id_utente = arguments?.getString("id")

        // Esegui la query per ottenere i dati JSON
        val query ="SELECT PRG_In_prestito.Data_inizio, " +
                "PRG_In_prestito.Data_fine_prevista, " +
                "PRG_Libro.Descrizione " +
                "FROM PRG_In_prestito, PRG_Libro " +
                "WHERE PRG_In_prestito.FK_Codice_utente ='$id_utente' " +
                "AND PRG_In_prestito.FK_ISBN = PRG_Libro.ISBN " +
                "AND PRG_In_prestito.FK_ISBN = '$ISBN'"

        Log.d("response: ", query)

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

                                val data_inizio = items.get("Data_inizio").asString ?: "N/A"
                                Log.d("Data_inizio ", data_inizio)

                                val data_fine_prevista = items.get("Data_fine_prevista").asString ?: "N/A"
                                Log.d("Data_fine_prevista ", data_fine_prevista)

                                val descrizione = items.get("Descrizione").asString ?: "N/A"
                                Log.d("Descrizione ", descrizione)

                                binding.textDataInizio.text = data_inizio
                                binding.textDataFine.text = data_fine_prevista
                                binding.textViewDescrizione.text = descrizione
                            }


                            // Salva i dati nel ViewModel
                            // viewModel.setData(data)
                            // Aggiorna la RecyclerView
                            //recyclerView.adapter?.notifyDataSetChanged()
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