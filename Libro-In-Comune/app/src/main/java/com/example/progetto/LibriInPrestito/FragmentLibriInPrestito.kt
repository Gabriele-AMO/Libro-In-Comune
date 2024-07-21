package com.example.progetto.LibriInPrestito

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import androidx.lifecycle.ViewModelProvider
import com.example.progetto.R
import com.example.progetto.Retrofit
import com.example.progetto.RicercaLibri.FragmentRicercaLibro
import com.example.progetto.databinding.FLibriInPrestitoBinding
import com.example.progetto.databinding.FTendinaBinding


/* ok perfetta
class FragmentLibriInPrestito : Fragment() {

    private lateinit var viewModel: MyViewModel
    private lateinit var recyclerView: RecyclerView
    private var recyclerViewState: Parcelable? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)

        // Infla il layout del fragment
        val view = inflater.inflate(R.layout.f_libri_in_prestito, container, false)


        // Ottieni una referenza alla RecyclerView dal layout
        recyclerView = view.findViewById(R.id.RecyclerView)

        // Imposta il LayoutManager per la RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Effettua la richiesta e analizza i dati JSON
        val dataList: ArrayList<ItemsVMLibriInPrestito> = parseJSON()
        Log.i("Data", dataList.toString())

        //Invoca l'Adapter per la recyclerView
        val adapter = CustomAdapter(dataList)
        recyclerView.adapter = adapter

        adapter.setOnClickListener(object:
            CustomAdapter.OnClickListener {
            override fun onClick(position: Int, model: ItemsVMLibriInPrestito) {
            //Toast
               // Toast.makeText(context, "Cliccato", Toast.LENGTH_SHORT).show()
                val manager = parentFragmentManager
                val transaction = manager.beginTransaction()
                    .replace(R.id.FCVLibriInPrestito,FragmentLibro())
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit()

            }
        })

        return view
    }

    private fun parseJSON(): ArrayList<ItemsVMLibriInPrestito> {
        Log.i("ciao", "sono dentro parseJSON :) ")
        var data = ArrayList<ItemsVMLibriInPrestito>()

        // Esegui la query per ottenere i dati JSON
        val query ="SELECT PRG_In_prestito.FK_ISBN,PRG_Libro.Titolo,PRG_In_prestito.Numero_copia\n" +
                "FROM PRG_In_prestito, PRG_Utente, PRG_Libro\n" +
                "WHERE PRG_Utente.Codice_utente = PRG_In_prestito.FK_Codice_utente AND PRG_In_prestito.FK_ISBN = PRG_Libro.ISBN"

        Retrofit.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    Log.i("Response", "sono dentro onResponse")

                    if (response.isSuccessful) {
                        Log.i("Response", "sono dentro isSuccessfull")

                        val result=(response.body()?.get("queryset") as JsonArray)

                        Log.i("Response", "resultsize è"+ result.size().toString())
                        if (result.size()>= 1) {

                            Log.i("Response", "resultsize è >=1")

                            for (i in result) {
                                val items = i as JsonObject

                                val id = items.get("FK_ISBN").asString ?: "N/A"
                                Log.d("id: ", id)

                                val titolo = items.get("Titolo").asString ?: "N/A"
                                Log.d("Titolo: ", titolo)

                                val numeroCopia = items.get("Numero_copia").asString ?: "N/A"
                                Log.d("numeroCopia: ", numeroCopia)

                                data.add(ItemsVMLibriInPrestito(id, titolo, numeroCopia))
                                Log.i("Response", "Ce l'hai fatta coglione")
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                   Log.i("response", "sono in onFailure, fallito")
                }
            }
        )
        return data
    }
} */

class FragmentLibriInPrestito : Fragment() {

    private lateinit var viewModel: MyViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: FLibriInPrestitoBinding

    private var dataList: ArrayList<ItemsVMLibriInPrestito> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        // Infla il layout del fragment
        binding = FLibriInPrestitoBinding.inflate(inflater, container, false)
        val view = binding.root

        val managerchild = parentFragmentManager

        val abilitato = arguments?.getString("abilitato")

        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)

        if(abilitato == "1") {
            binding.buttonPrenotaLibro.setOnClickListener() {

                val bundle = Bundle()
                val id_utente = arguments?.getString("id")
                bundle.putString("id", id_utente)


                val fragment = FragmentRicercaLibro()
                fragment.arguments = bundle

                managerchild.beginTransaction()
                    .replace(R.id.FCVLibriInPrestito, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        // Ottieni una referenza alla RecyclerView dal layout
        recyclerView = view.findViewById(R.id.RecyclerView)

        // Imposta il LayoutManager per la RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Verifica se i dati sono già stati salvati nel ViewModel
        val dataList: ArrayList<ItemsVMLibriInPrestito> = if (viewModel.getData().isNotEmpty()) {
            viewModel.getData()
        } else {
            // Effettua la richiesta e analizza i dati JSON solo se non sono stati salvati nel ViewModel
            parseJSON()
        }

        //Invoca l'Adapter per la recyclerView
        val adapter = CustomAdapter(dataList)
        recyclerView.adapter = adapter

        adapter.setOnClickListener(object : CustomAdapter.OnClickListener {

            override fun onClick(position: Int, model: ItemsVMLibriInPrestito) {
                val bundle = Bundle()
                val id_utente = arguments?.getString("id")
                bundle.putString("id", id_utente)

                bundle.putString("key1", model.text_1)
                bundle.putString("key2", model.text_2)
                bundle.putString("key3", model.text_3)


                val abilitato = arguments?.getString("abilitato")
                bundle.putString("abilitato", abilitato)

                val fragment = FragmentLibro()
                fragment.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.FCVLibriInPrestito, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        })

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Salva i dati nel ViewModel
        viewModel.setData(dataList)
    }

    private fun parseJSON(): ArrayList<ItemsVMLibriInPrestito> {

        val id_utente = arguments?.getString("id")

        // Esegui la query per ottenere i dati JSON
        val query ="SELECT PRG_In_prestito.FK_ISBN, PRG_Libro.Titolo, " +
                "PRG_In_prestito.Data_inizio, " +
                "PRG_In_prestito.Data_fine_prevista, " +
                "PRG_Libro.Genere, PRG_Libro.Autore, " +
                "PRG_Libro.Descrizione, " +
                "PRG_In_prestito.Numero_copia " +
                "FROM PRG_In_prestito, PRG_Libro " +
                "WHERE PRG_In_prestito.FK_Codice_utente ='$id_utente' " +
                "AND PRG_In_prestito.FK_ISBN = PRG_Libro.ISBN"

        Log.d("response: ", query)

        val data = ArrayList<ItemsVMLibriInPrestito>()

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

                                val id = items.get("FK_ISBN").asString ?: "N/A"
                                Log.d("id: ", id)

                                val titolo = items.get("Titolo").asString ?: "N/A"
                                Log.d("Titolo: ", titolo)

                                val data_inizio = items.get("Data_inizio").asString ?: "N/A"
                                Log.d("Data_inizio ", data_inizio)

                                val data_fine_prevista = items.get("Data_fine_prevista").asString ?: "N/A"
                                Log.d("Data_fine_prevista ", data_fine_prevista)

                                val descrizione = items.get("Descrizione").asString ?: "N/A"
                                Log.d("Descrizione ", descrizione)

                                val numeroCopia = items.get("Numero_copia").asString ?: "N/A"
                                Log.d("numeroCopia: ", numeroCopia)




                                data.add(ItemsVMLibriInPrestito(id, titolo, numeroCopia))
                                Log.i("Response", "Ce l'hai fatta ")
                            }
                            // Salva i dati nel ViewModel
                            viewModel.setData(data)
                            // Aggiorna la RecyclerView
                            recyclerView.adapter?.notifyDataSetChanged()
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
