package com.example.progetto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.progetto.databinding.ActivityCreazioneAccountBinding
import com.example.progetto.databinding.ActivityRecuperoPasswordBinding
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityRecuperoPassword : AppCompatActivity() {

    private lateinit var binding: ActivityRecuperoPasswordBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecuperoPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonRecupero.setOnClickListener(){

            Recupero()

        }
    }

    fun Recupero(){

        val codice = binding.editTextRecuperoCodiceUtente.text.toString()
        val telefono = binding.editTextRecuperoNumeroDiTelefono?.text.toString()

        val query = "SELECT PRG_Utente.Psw " +
                "FROM PRG_Utente " +
                "WHERE PRG_Utente.Codice_utente = '$codice' " +
                "AND PRG_Utente.Telefono = '$telefono'"

        Retrofit.retrofit.select(query).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                    Log.i("Response", "sono dentro onResponse")

                    if (response.isSuccessful) {
                        Log.i("Response", "sono dentro isSuccessfull")

                        val result = (response.body()?.get("queryset") as JsonArray)

                        if (result.size() == 1) {

                            Log.i("Response", "Sono dentro al resultsize ==1")

                            for (i in result) {

                                val items = i as JsonObject
                                binding.textViewPsw.text = items.get("Psw").asString ?: "N/A"



                            }
                        }

                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.i("response", "sono in onFailure, fallito")
                }
            }
        )



    }

}