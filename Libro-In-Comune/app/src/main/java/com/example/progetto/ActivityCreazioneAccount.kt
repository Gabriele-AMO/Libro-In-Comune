package com.example.progetto


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.progetto.databinding.ActivityCreazioneAccountBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityCreazioneAccount : AppCompatActivity() {

    private lateinit var binding: ActivityCreazioneAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreazioneAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonCreaAccount.setOnClickListener() {
            Creazione()
        }

    }

    fun Creazione() {

        val nome = binding.editTextCreaNome.text.toString()
        val cognome = binding.editTextCreaCognome.text.toString()
        val email = binding.editTextCreaEmail.text.toString()
        val numeroDiTelefono = binding.editTextCreaNumeroTelefono.text.toString()
        val psw = binding.editTextCreaPsw.text.toString()

        val queryInsert = "INSERT INTO PRG_Utente " +
                "(Codice_utente, Nome, Cognome, Telefono, Email, Psw, Abilitato) " +
                "VALUES " +
                "(FLOOR(RAND() * (1000 - 1 + 1)) + 1, '$nome', '$cognome', '$numeroDiTelefono', '$email', '$psw', '1')"

        Log.i("QUERY", queryInsert)

        Retrofit.retrofit.insert(queryInsert).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                    Log.i("Response", "sono dentro onResponse")

                    Log.i("Response", response.body().toString())

                    if (response.isSuccessful) {
                        Log.i("Response", "sono dentro isSuccessfull")

                        Toast.makeText(
                            this@ActivityCreazioneAccount,
                            "Utente creato correttamente",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.i("response", "sono in onFailure, fallito")
                }
            }
        )
    }

}
