package com.example.progetto.Eventi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.progetto.R
import com.example.progetto.Retrofit
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentEventi : Fragment() {


    private lateinit var viewModel: ViewModelEventi
    private lateinit var recyclerView: RecyclerView

    private var dataList: ArrayList<ItemsVMEventi> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)


        viewModel = ViewModelProvider(this).get(ViewModelEventi::class.java)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.f_lista_eventi,container, false)

        // Ottieni una referenza alla RecyclerView dal layout
        recyclerView = view.findViewById(R.id.RecyclerViewEventi)

        // Imposta il LayoutManager per la RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Verifica se i dati sono già stati salvati nel ViewModel
        val dataList: ArrayList<ItemsVMEventi> = if (viewModel.getData().isNotEmpty()) {
            viewModel.getData()
        } else {
            // Effettua la richiesta e analizza i dati JSON solo se non sono stati salvati nel ViewModel
            parseJSON()
        }
        val adapter = CustomAdapterEventi(dataList)
        recyclerView.adapter = adapter

        adapter.setOnClickListener(object: CustomAdapterEventi.OnClickListener {

            override fun onClick(position: Int, model: ItemsVMEventi) {
                val bundle = Bundle()
                bundle.putString("key1", model.text_1)
                bundle.putString("key2", model.text_2)
                bundle.putString("key3", model.text_3)

                val fragment = FragmentEvento()
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
    private fun parseJSON(): ArrayList<ItemsVMEventi> {
        // Esegui la query per ottenere i dati JSON
        val query ="SELECT PRG_Eventi.Nome_Evento, PRG_Eventi.Data_Evento, PRG_Eventi.Tipologia FROM PRG_Eventi;"
        val data = ArrayList<ItemsVMEventi>()

        Retrofit.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val result = (response.body()?.get("queryset") as JsonArray)
                        if (result.size() >= 1) {
                            for (i in result) {
                                val items = i as JsonObject

                                val nome = items.get("Nome_Evento").asString ?: "N/A"
                                Log.d("Nome_Evento: ", nome)

                                val date = items.get("Data_Evento").asString ?: "N/A"
                                Log.d("Data_Evento: ", date)

                                val tipo = items.get("Tipologia").asString ?: "N/A"
                                Log.d("Tipologia: ", tipo)
                                data.add(ItemsVMEventi(nome,date,tipo))

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

