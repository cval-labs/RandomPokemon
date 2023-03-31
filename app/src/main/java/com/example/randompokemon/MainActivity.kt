package com.example.randompokemon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    var pokemonImageURL:String? = ""
    var pokemonName:String = ""
    var pokemonType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.pokemonButton)
        val imageView = findViewById<ImageView>(R.id.pokemonImage)
        val pokeName = findViewById<TextView>(R.id.pokemonName)
        val pokeType = findViewById<TextView>(R.id.pokemonType)

        button.setOnClickListener {
            getPokemon(imageView, pokeName, pokeType)
        }

    }

    private fun getRandomNumber(): Int {
        return Random.nextInt(20)
    }


    private fun getPokemon(imageView: ImageView, pokeName: TextView, pokeType: TextView) {
        val client = AsyncHttpClient()
        val client2 = AsyncHttpClient()

        client["https://pokeapi.co/api/v2/pokemon/", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.d("Poke", "response successful$json")

                val results = json.jsonObject.getJSONArray("results")
                // takes a random int between 0 and 19
                val randomPoke = results.getJSONObject(getRandomNumber())
                Log.i("random number", "$randomPoke")

                pokemonName = randomPoke.getString("name")
                pokeName.text = pokemonName.replaceFirstChar { it.uppercase() }

                // Gets random pokemon's URL where its info and sprites live
                val pokeInfoURL = randomPoke.getString("url")
                Log.i("url from random number", "$pokeInfoURL")

                // Client2 parses random pokemon's URL
                client2[pokeInfoURL, object:JsonHttpResponseHandler() {
                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.d("client2 Error","Did not enter second url")
                    }

                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        Log.d("Client2", "response successful! $json")

                        // Pokemon's sprite
                        val sprite = json.jsonObject.getJSONObject("sprites")
                        pokemonImageURL = sprite.getString("front_default")
                        Glide.with(this@MainActivity)
                            .load(pokemonImageURL)
                            .fitCenter()
                            .into(imageView)

                        // Pokemon's type
                        val types = json.jsonObject.getJSONArray("types").getJSONObject(0)
                        pokemonType = types.getJSONObject("type").getString("name").replaceFirstChar { it.uppercase() }
                        // TODO: string template, ex: "Type: GRASS"
                        val typeText = "Type: "
                        pokeType.text = String.format("%s%s", typeText, pokemonType)
                    }
                }]
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("Poke Error", "error")
            }
        }]

    }


}