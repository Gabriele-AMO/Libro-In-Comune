package com.example.progetto.Preferiti

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.progetto.Preferiti.MyViewModelPreferiti
import com.example.progetto.R
import com.example.progetto.Retrofit
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentListaPreferiti : Fragment() {


    private lateinit var viewModel: MyViewModelPreferiti
    private lateinit var recyclerView: RecyclerView
    private var dataList: ArrayList<ItemsVMPreferiti> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(MyViewModelPreferiti::class.java)

        // Infla il layout del fragment
        val view = inflater.inflate(R.layout.f_preferiti, container, false)

        // Ottieni una referenza alla RecyclerView dal layout
        recyclerView = view.findViewById(R.id.RecyclerViewPreferiti)

        // Imposta il LayoutManager per la RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Verifica se i dati sono già stati salvati nel ViewModel
        val dataList: ArrayList<ItemsVMPreferiti> = if (viewModel.getData().isNotEmpty()) {
            viewModel.getData()
        } else {
            // Effettua la richiesta e analizza i dati JSON solo se non sono stati salvati nel ViewModel
            parseJSON()
        }
        //Invoca l'Adapter per la recyclerView
        val adapter = CustomAdapterPreferiti(dataList)
        recyclerView.adapter = adapter

        adapter.setOnClickListener(object: CustomAdapterPreferiti.OnClickListener {

            override fun onClick(position: Int, model: ItemsVMPreferiti) {
                val bundle = Bundle()
                bundle.putString("key1", model.text_1)// ISBN
                bundle.putString("key2", model.text_2)// TITOLO
                bundle.putString("key3", model.text_3)// GENERE

                val id_utente = arguments?.getString("id")
                bundle.putString("id",id_utente)

                val fragment = FragmentLibroPreferito()
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

    private fun parseJSON(): ArrayList<ItemsVMPreferiti> {

        val id = arguments?.getString("id")

        // Esegui la query per ottenere i dati JSON
        val query = "SELECT PRG_Preferiti.FK_ISBN, PRG_Preferiti.Titolo, PRG_Preferiti.Genere " +
                "FROM PRG_Preferiti "+
                "WHERE PRG_Preferiti.FK_Codice_utente ='$id'"

        Log.d("response: ",query)

        val data = ArrayList<ItemsVMPreferiti>()

        Retrofit.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        Log.d("response: ","Sono destro isSuccesful")
                        val result = (response.body()?.get("queryset") as JsonArray)
                        if (result.size() >= 1) {
                            Log.d("response: ","Size >=1")
                            for (i in result) {
                                val items = i as JsonObject

                                val id = items.get("FK_ISBN").asString ?: "N/A"
                                Log.d("id: ", id)

                                val titolo = items.get("Titolo").asString ?: "N/A"
                                Log.d("Titolo: ", titolo)

                                val genere = items.get("Genere").asString ?: "N/A"
                                Log.d("Genere: ", genere)

                                data.add(ItemsVMPreferiti(id, titolo,genere))
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