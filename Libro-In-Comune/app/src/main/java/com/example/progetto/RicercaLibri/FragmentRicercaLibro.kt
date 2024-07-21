package com.example.progetto.RicercaLibri

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.progetto.R
import com.example.progetto.Retrofit
import com.example.progetto.databinding.FRicercaLibroBinding
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentRicercaLibro : Fragment() {

    private lateinit var binding: FRicercaLibroBinding

    //private lateinit var viewModel: MyViewModelRicercaLibro
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomAdapterRicercaLibro // Dichiarazione dell'adapter come variabile di istanza

    private var dataList: ArrayList<ItemsVMRicercaLibro> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        // Infla il layout del fragment
        binding = FRicercaLibroBinding.inflate(inflater, container, false)
        val view = binding.root

        // viewModel = ViewModelProvider(this).get(MyViewModelRicercaLibro::class.java)

        // Ottieni una referenza alla RecyclerView dal layout
        recyclerView = view.findViewById(R.id.RecyclerViewRicercaLibro)

        // Imposta il LayoutManager per la RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //Invoca l'Adapter per la recyclerView
        adapter = CustomAdapterRicercaLibro(dataList)
        recyclerView.adapter = adapter

        val id = arguments?.getString("id")

        if (id != null) {
            Log.i("ID", id)
        }

        binding.buttonRicerca.setOnClickListener() {
            dataList = Ricerca()

            //Invoca l'Adapter per la recyclerView
            adapter = CustomAdapterRicercaLibro(dataList)
            recyclerView.adapter = adapter

            adapter.setOnClickListener(object : CustomAdapterRicercaLibro.OnClickListener {

                override fun onClick(position: Int, model: ItemsVMRicercaLibro) {
                    val fragment = FragmentLibroNuovo()
                    val bundle = Bundle()

                    bundle.putString("key1", model.text_1)// ISBN
                    bundle.putString("key2", model.text_2)// titolo
                    bundle.putString("key3", model.text_3)// genere

                    bundle.putString("id", id)
                    fragment.arguments = bundle

                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.FCVLibriInPrestito, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            })

        }




        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Salva i dati nel ViewModel
        // viewModel.setData(dataList)
    }

    private fun Ricerca(): ArrayList<ItemsVMRicercaLibro> {

        val titolo: String? = binding.titoloed.text.toString()
        val autore: String? = binding.autoreed.text.toString()
        val casaEditrice: String? = binding.casaeditriceed.text.toString()
        val genere: String? = binding.genereed.text.toString()


        // Esegui la query solo se almeno uno dei campi viene inserito
        if (!titolo.isNullOrEmpty() || !autore.isNullOrEmpty() || !casaEditrice.isNullOrEmpty() || !genere.isNullOrEmpty()) {
            // Utilizza questi valori per eseguire la query e ottenere i dati JSON
            var query =
                "SELECT PRG_Libro.Titolo, PRG_Libro.Casa_Editrice, PRG_Libro.Genere, PRG_Libro.ISBN, PRG_Libro.Descrizione " +
                        "FROM PRG_Libro " +
                        "WHERE"
            var numeroFiltri = 0
            Log.i("numeroFiltri", ":" + numeroFiltri)
            if (!titolo.isNullOrEmpty() && numeroFiltri == 0) {
                query += " PRG_Libro.Titolo LIKE '%$titolo%' "
                numeroFiltri++
            } else if (!titolo.isNullOrEmpty() && numeroFiltri != 0) {
                query += " AND PRG_Libro.Titolo LIKE '%$titolo%' "
            }

            if (!autore.isNullOrEmpty() && numeroFiltri == 0) {
                query += " PRG_Libro.Autore LIKE '%$autore%' "
                numeroFiltri++
            } else if (!autore.isNullOrEmpty() && numeroFiltri != 0) {
                query += " AND PRG_Libro.Autore LIKE '%$autore%' "
            }
            if (!casaEditrice.isNullOrEmpty() && numeroFiltri == 0) {
                query += " PRG_Libro.Casa_Editrice LIKE '%$casaEditrice%' "
                numeroFiltri++
            } else if (!casaEditrice.isNullOrEmpty() && numeroFiltri != 0) {
                query += " AND PRG_Libro.Casa_Editrice LIKE '%$casaEditrice%' "
            }
            if (!genere.isNullOrEmpty() && numeroFiltri == 0) {
                query += " PRG_Libro.Genere LIKE '%$genere%' "
                numeroFiltri++
            } else if (!genere.isNullOrEmpty() && numeroFiltri != 0) {
                query += " AND PRG_Libro.Genere LIKE '%$genere%' "
            }
            Log.i("numeroFiltriAllaFine", ":" + numeroFiltri)

            // Esegui la chiamata alla funzione parseJSON() con la nuova query
            dataList = parseJSON(query)
        } else {
            var query =
                "SELECT PRG_Libro.Titolo, PRG_Libro.Casa_Editrice, PRG_Libro.Genere, PRG_Libro.ISBN, PRG_Libro.Descrizione " +
                        "FROM PRG_Libro " +
                        "WHERE 1=1"
            // Esegui la chiamata alla funzione parseJSON() con la nuova query
            dataList = parseJSON(query)

        }
        return dataList
    }

    private fun parseJSON(query: String): ArrayList<ItemsVMRicercaLibro> {
        // Esegui la query per ottenere i dati JSON

        Log.i("parse", "Sono dentro JSONPARSE")

        val data = ArrayList<ItemsVMRicercaLibro>()

        Retrofit.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        Log.i("response", "Sono dentro isSuccefull")

                        val result = (response.body()?.get("queryset") as JsonArray)
                        if (result.size() >= 1) {

                            Log.i("response", "resultsize >=1")
                            for (i in result) {
                                val items = i as JsonObject

                                val titolo = items.get("Titolo").asString ?: "N/A"
                                Log.i("Titolo: ", titolo)

                                val casaEditrice = items.get("Casa_Editrice").asString ?: "N/A"
                                Log.i("Casa Editrice: ", casaEditrice)

                                val genere = items.get("Genere").asString ?: "N/A"
                                Log.i("Genere", genere)

                                val ISBN = items.get("ISBN").asString ?: "N/A"
                                Log.i("ISBN", ISBN)


                                data.add(ItemsVMRicercaLibro(ISBN, titolo, genere))
                                Log.i("Response", "Ce l'hai fatta ")
                            }

                            dataList = data // Aggiornamento della lista principale

                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    // Gestisci l'errore di chiamata
                }
            }
        )
        return data
    }
}