package com.test.pokedex.Activities

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import com.test.pokedex.R
import kotlinx.android.synthetic.main.activity_details.*

class ActivityDetails : AppCompatActivity() {
    private var context : Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        var num = getPokemonNumber()
        if(num != "0")
            getPokemonAPI(num)
    }

    private fun getPokemonNumber() : String{
        if(intent != null)
            return intent.getStringExtra("pokemon_num")
        return "0"
    }

    private fun getPokemonAPI(pokemon_num: String){
        Ion.with(context)
            .load("https://pokeapi.co/api/v2/pokemon/" + pokemon_num + "/")
            .asJsonObject()
            .done{ event, result ->
                if(event == null){
                    handlePokemon(result)
                }
            }

        var linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.scrollToPosition(0)
    }

    private fun handlePokemon(pokemon: JsonObject){
        // Name
        if(pokemon.get("name") != null){
            val pokemon_name: String = pokemon.get("name").toString().replace(oldValue = "\"", newValue = "").toUpperCase()
            text_name.text = pokemon_name
            text_number.text = "#" + pokemon.get("order").toString()
        }

        // Types
        if(!pokemon.get("types").isJsonNull){
            val types = pokemon.get("types").asJsonArray
            for(i in 0.until(types.size())){
                val type = types.get(i).asJsonObject.get("type").asJsonObject
                val t_name = type.get("name").toString().replace("\"", "").toUpperCase()
                addTextToLayout(t_name, R.id.layout_type)
            }
        }

        // Stats
        if(!pokemon.get("stats").isJsonNull){
            val stats = pokemon.get("stats").asJsonArray
            for (i in 0.until(stats.size())){
                val stat_base = stats.get(i).asJsonObject.get("base_stat")
                val name = stats.get(i).asJsonObject.get("stat").asJsonObject.get("name")
                val stat = name.toString().replace("\"", "").toUpperCase() + " [" + stat_base.toString() + "]"
                addTextToLayout(stat, R.id.layout_stats)
            }
        }

        // Moves
        if(!pokemon.get("moves").isJsonNull){
            val moves = pokemon.get("moves").asJsonArray
            for(i in 0.until(moves.size())){
                var name = moves.get(i).asJsonObject.get("move").asJsonObject.get("name").toString().toUpperCase()
                addTextToLayout(name, R.id.layout_moves)
            }
        }

        // Sprite
        if(!pokemon.get("sprites").isJsonNull){
            if(pokemon.get("sprites").asJsonObject.get("front_default") != null){
                Glide
                    .with(context)
                    .load(pokemon.get("sprites").asJsonObject.get("front_default").asString)
                    .placeholder(R.drawable.pokemon_logo_min)
                    .error(R.drawable.pokemon_logo_min)
                    .into(findViewById(R.id.image_pokemon))
            }
        }
    }

    private fun addTextToLayout(text: String, parent: Int){
        val text_formated = text.replace("\"","").toUpperCase()
        val linearLayout: LinearLayout = findViewById(parent)
        var type = TextView(this)
        type.text = text_formated
        linearLayout.addView(type)
    }


}