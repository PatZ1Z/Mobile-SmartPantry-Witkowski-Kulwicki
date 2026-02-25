package com.example.mobile_smart_pantry_project_iv

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobile_smart_pantry_project_iv.databinding.ActivityMainBinding
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private lateinit var listAdapter: ArrayAdapter<String>

    private var productList = mutableListOf<String>()

    private val productArray = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            productList
        )

        binding.ListOfProducts.adapter = listAdapter

        val listView = findViewById<ListView>(R.id.ListOfProducts)

        val adapter = ProductAdapter(this, productArray)
        listView.adapter = adapter

        loadMoviesFromJsonFile()

    }

    private fun loadMoviesFromJsonFile(){
        try{
            try {
                val inputStream = resources.openRawResource(R.raw.pantry)
                val jsonString = inputStream.bufferedReader().use { it.readText() }

                Log.d("JSON_DEBUG", "JSON content: '$jsonString'")

                val json = Json { ignoreUnknownKeys = true }
                val loadedList = json.decodeFromString<List<Product>>(jsonString)


                productArray.clear()
                productArray.addAll(loadedList)

                productList.clear()
                productList.addAll(
                    productArray.map {
                        "${it.UUID} | ${it.Name} | ${it.Quantity} | ${it.Category} | ${it.ImageRef}"
                    }
                )

                listAdapter.notifyDataSetChanged()

                Toast.makeText(
                    this,
                    "Działa",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                e.printStackTrace()

                Toast.makeText(
                    this,
                    e.message ?: "Nieznany błąd",
                    Toast.LENGTH_LONG
                ).show()
            }


        }catch (e: Exception){
            Toast.makeText(
                this,
                "Błąd odczytu",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}