package com.example.progetto

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.example.progetto.databinding.FBibliotecaBinding
import com.example.progetto.databinding.FTendinaBinding
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentBiblioteca : Fragment() {

    private lateinit var binding: FBibliotecaBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FBibliotecaBinding.inflate(inflater, container, false)
        val view = binding.root

        val id = arguments?.getString("id")

        var  managerchild = parentFragmentManager

        Posti()

        // Esempio: Imposta il valore massimo e il valore attuale per le ProgressBar
        binding.progressBar1.max = 10

        binding.progressBar2.max = 10

        binding.progressBar3.max = 10


        Log.i("Progress",binding.progressBar1.progress.toString())
        Log.i("Progress",binding.progressBar1.max.toString())
        if (binding.progressBar1.progress < binding.progressBar1.max) {

            binding.buttonPrenota1.setOnClickListener() {

                if (id != null) {
                    prenota(id, 1)
                }
                val fragment = FragmentSuccesso()
                managerchild.beginTransaction()
                    .replace(R.id.FCVLibriInPrestito, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }else {
            Toast.makeText(
                requireContext(),
                "POSTI ESAURITI",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (binding.progressBar2.progress < binding.progressBar2.max) {
            binding.buttonPrenota2.setOnClickListener() {

                if (id != null) {
                    prenota(id, 2)
                }
                val fragment = FragmentSuccesso()
                managerchild.beginTransaction()
                    .replace(R.id.FCVLibriInPrestito, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }else {
            Toast.makeText(
                requireContext(),
                "POSTI ESAURITI",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (binding.progressBar3.progress < binding.progressBar3.max) {
            binding.buttonPrenota3.setOnClickListener() {

                if (id != null) {
                    prenota(id, 3)
                }
                val fragment = FragmentSuccesso()
                managerchild.beginTransaction()
                    .replace(R.id.FCVLibriInPrestito, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }else {
            Toast.makeText(
                requireContext(),
                "POSTI ESAURITI",
                Toast.LENGTH_SHORT
            ).show()
        }

        return view

    }

    fun Posti() {
        var query = "SELECT PRG_Biblioteca.Posti_occupati " +
                "FROM PRG_Biblioteca"

        var progress = IntArray(3)

        Retrofit.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                    Log.i("Response", "sono dentro onResponse")

                    if (response.isSuccessful) {
                        Log.i("Response", "sono dentro isSuccessfull")

                        val result = (response.body()?.get("queryset") as JsonArray)

                        Log.i("Response",response.body().toString())
                        if (result.size() > 0) {
                            Log.i("RESPONSE", "SIZE > 0")

                            var j = 0

                            for (i in result) {

                                var items = i as JsonObject

                                 progress.set(j, items.get("Posti_occupati").asInt)
                                        j++




                            }
                            binding.progressBar1.progress = progress[0]
                            Log.i("Progress",binding.progressBar1.progress.toString())
                            binding.progressBar2.progress = progress[1]
                            Log.i("Progress",binding.progressBar2.progress.toString())
                            binding.progressBar3.progress = progress[2]
                            Log.i("Progress",binding.progressBar3.progress.toString())

                        }
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.i("response", "sono in onFailure, fallito")
                }

            })
    }

    fun prenota(id_utente: String, fascia: Int) {

        var queryControllo = "SELECT * " +
                "FROM PRG_Posti_prenotati " +
                "WHERE FK_Codice_utente = '$id_utente'"
        Log.i("query controllo", queryControllo)

        var queryInsert = "INSERT INTO PRG_Posti_prenotati (FK_Codice_Utente, FK_Fascia_oraria) " +
                "VALUES ('$id_utente','$fascia')"
        Log.i("query controllo", queryInsert)

        var queryUpdate =
            "UPDATE PRG_Biblioteca SET PRG_Biblioteca.Posti_occupati = PRG_Biblioteca.Posti_occupati + 1 " +
                    "WHERE PRG_Biblioteca.Fascia_oraria = '$fascia'"
        Log.i("query controllo", queryUpdate)

        Retrofit.retrofit.select(queryControllo).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                    Log.i("Response", "sono dentro onResponse")

                    if (response.isSuccessful) {
                        Log.i("Response", "sono dentro isSuccessfull Controllo")

                        val result = (response.body()?.get("queryset") as JsonArray)

                        if (result.size() == 0) {
                            Retrofit.retrofit.insert(queryInsert).enqueue(
                                object : Callback<JsonObject> {
                                    override fun onResponse(
                                        call: Call<JsonObject>,
                                        response: Response<JsonObject>
                                    ) {

                                        Log.i("Response", "sono dentro onResponse Insert")

                                        if (response.isSuccessful) {
                                            Log.i("Response", "sono dentro isSuccessfull Insert")
                                            Retrofit.retrofit.update(queryUpdate).enqueue(
                                                object : Callback<JsonObject> {
                                                    override fun onResponse(
                                                        call: Call<JsonObject>,
                                                        response: Response<JsonObject>
                                                    ) {

                                                        Log.i(
                                                            "Response",
                                                            "sono dentro onResponse Update"
                                                        )

                                                        if (response.isSuccessful) {
                                                            Log.i(
                                                                "Response",
                                                                "sono dentro isSuccessfull Update"
                                                            )
                                                            Toast.makeText(
                                                                requireContext(),
                                                                "Prenotato con successo",
                                                                Toast.LENGTH_SHORT
                                                            ).show()




                                                        }

                                                    }

                                                    override fun onFailure(
                                                        call: Call<JsonObject>,
                                                        t: Throwable
                                                    ) {
                                                        Log.i(
                                                            "response",
                                                            "sono in onFailure, fallito"
                                                        )
                                                    }

                                                })

                                        }

                                    }

                                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                        Log.i("response", "sono in onFailure, fallito")
                                    }

                                })


                        }else{
                            Toast.makeText(
                                requireContext(),
                                "Hai giÃ  effettuato una prenotazione",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.i("response", "sono in onFailure, fallito")
                }

            })


    }


}
//se c'Ã¨ query di controllo se gia prenotato