package com.example.progetto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.progetto.databinding.ActivityLoginBinding
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.progetto.HomePage.HomePage
import com.example.progetto.databinding.HomePageBinding
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var data: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.Accedi.setOnClickListener() {
            val email = binding.PersonName.text.toString()
            val psw = binding.Password.text.toString()

            checkScadenze(email , psw)
        }

        binding.Account.setOnClickListener() {
            val i = Intent(this, ActivityCreazioneAccount::class.java)
            startActivity(i)
        }

        binding.PasswordLost.setOnClickListener() {

            val i = Intent(this, ActivityRecuperoPassword::class.java)
            startActivity(i)
        }

    }

    private fun enter(email:String, psw:String): Boolean {

        Log.i("ciao", "sono dentro parseJSON :) ")

        // Esegui la query per ottenere i dati JSON
        val query = "SELECT * " +
                " FROM PRG_Utente " +
                " WHERE PRG_Utente.Psw = '$psw' AND PRG_Utente.Email = '$email'"

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
                                val id = items.get("Codice_utente").asString ?: "N/A"
                                Log.d("id: ", id)

                                val nome = items.get("Nome").asString ?: "N/A"
                                Log.d("Nome: ", nome)

                                val cognome = items.get("Cognome").asString ?: "N/A"
                                Log.d("Cognome: ", cognome)

                                val telefono = items.get("Telefono").asString ?: "N/A"
                                Log.d("Telefono: ", telefono)

                                val email = items.get("Email").asString ?: "N/A"
                                Log.d("Email: ", email)

                                val psw = items.get("Psw").asString ?: "N/A"
                                Log.d("Psw: ", psw)

                                val abilitato = items.get("Abilitato").asString ?: "N/A"
                                Log.d("Abilitato: ", abilitato)

                                Log.i("Response", "Ce l'hai fatta")

                                data = true
                                if (data) {
                                    val i = Intent(this@Login, HomePage::class.java)

                                    i.putExtra("id", id)
                                    i.putExtra("nome", nome)
                                    i.putExtra("cognome", cognome)
                                    i.putExtra("telefono", telefono)
                                    i.putExtra("email", email)
                                    i.putExtra("psw", psw)
                                    i.putExtra("abilitato", abilitato)
                                    startActivity(i)
                                }
                            }
                        } else if (result.size() == 0) {
                            Toast.makeText(
                                this@Login,
                                "Credenziali Errate",
                                Toast.LENGTH_SHORT
                            ).show()
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

    private fun checkScadenze(email:String, psw:String) {

        val queryScadenze =
            "SELECT SUM(datediff(CURDATE(),PRG_In_prestito.Data_fine_prevista)) AS Scadenza, " +
                    "PRG_Libro.Titolo, PRG_In_prestito.Numero_copia " +
                    "FROM PRG_In_prestito, PRG_Libro, PRG_Utente " +
                    "WHERE PRG_In_prestito.FK_ISBN = PRG_Libro.ISBN AND " +
                    "PRG_In_prestito.FK_Codice_utente = PRG_Utente.Codice_utente " +
                    "AND PRG_Utente.Email = '$email' AND PRG_Utente.Psw = '$psw' " +
                    "GROUP BY (PRG_In_prestito.Codice_restituzione) " +
                    "HAVING Scadenza >0 "

        val queryUpdate = "UPDATE PRG_Utente SET PRG_Utente.Abilitato = 0 " +
                "WHERE PRG_Utente.Email = '$email' AND PRG_Utente.Psw = '$psw' "

        Retrofit.retrofit.select(queryScadenze).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                    Log.i("Response", "sono dentro onResponse")


                    if (response.isSuccessful) {
                        Log.i("Response", "sono dentro isSuccessfull")

                        val result = (response.body()?.get("queryset") as JsonArray)

                        if (result.size() > 0) {
                            Log.i("RESPONSE", "SIZE > 0")

                            Retrofit.retrofit.update(queryUpdate).enqueue(
                                object : Callback<JsonObject> {
                                    override fun onResponse(
                                        call: Call<JsonObject>,
                                        response: Response<JsonObject>
                                    ) {

                                        Log.i("Response", "sono dentro onResponseUpdate")
                                        Log.i("Abilitato", response.body().toString())

                                        if (response.isSuccessful) {
                                            Log.i("Response", "sono dentro isSuccessfullUpdate")

                                            enter(email,psw)
                                        }

                                    }

                                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                        Log.i("response", "sono in onFailure, fallito")
                                    }
                                })
                        }
                        else{
                            enter(email,psw)

                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.i("response", "sono in onFailure, fallito")
                }
            })
    }

}