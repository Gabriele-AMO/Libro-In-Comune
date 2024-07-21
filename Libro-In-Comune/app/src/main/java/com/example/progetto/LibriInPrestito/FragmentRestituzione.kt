package com.example.progetto.LibriInPrestito

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.progetto.FragmentSuccesso
import com.example.progetto.R
import com.example.progetto.Retrofit
import com.example.progetto.databinding.FRestituzioneLibroBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentRestituzione : Fragment() {

    private lateinit var binding: FRestituzioneLibroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FRestituzioneLibroBinding.inflate(inflater, container, false)

        val args = arguments
        val ISBN = args?.getString("ISBN")//ISBN

        binding.buttonRestituisciLibro.setOnClickListener() {
            if (ISBN != null) {
                Restituisci(ISBN)
            }
        }


        // Inflate the layout for this fragment
        return binding.root
    }


    fun Restituisci(ISBN: String) {

        var codice = binding.editTextCodiceRestituzioneLibro.text.toString()

        if (!codice.isNullOrEmpty()) {

            Log.i("VALORI", ISBN + " " + codice)


            // Esegui la query per ottenere i dati JSON
            val queryUpdate = "UPDATE PRG_Libro " +
                    "SET Numero_Copie = Numero_Copie + 1 " +
                    "WHERE ISBN = '$ISBN' "

            val queryDelete = "DELETE FROM PRG_In_prestito " +
                    "WHERE Codice_restituzione = '$codice'"

            Retrofit.retrofit.remove(queryDelete).enqueue(
                object : Callback<JsonObject> {
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {
                        if (response.isSuccessful) {
                            Log.i("ResponseDel", "Libro Cancellato")
                        }

                        Retrofit.retrofit.insert(queryUpdate).enqueue(
                            object : Callback<JsonObject> {

                                override fun onResponse(
                                    call: Call<JsonObject>,
                                    response: Response<JsonObject>
                                ) {
                                    if (response.isSuccessful) {
                                        Log.i("responseUpdate", "LibroRestituito")
                                        Toast.makeText(
                                            requireContext(),
                                            "LIBRO RESTITUITO CORRETTAMENTE",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        val fragment = FragmentSuccesso()
                                        requireActivity().supportFragmentManager.beginTransaction()
                                            .replace(R.id.FCVLibriInPrestito, fragment)
                                            .addToBackStack(null)
                                            .commit()


                                    }
                                }

                                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                    // Gestisci l'errore di chiamata
                                }
                            }
                        )

                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        // Gestisci l'errore di chiamata
                    }

                }
            )
        }else{
            Toast.makeText(
                requireContext(),
                "INSERISCI IL CODICE",
                Toast.LENGTH_SHORT
            ).show()

        }
    }


}