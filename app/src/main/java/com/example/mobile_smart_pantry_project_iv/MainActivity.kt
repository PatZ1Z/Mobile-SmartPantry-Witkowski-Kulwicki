package com.example.mobile_smart_pantry_project_iv

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.mobile_smart_pantry_project_iv.databinding.ActivityMainBinding
import kotlinx.serialization.json.Json
import java.io.File
import kotlinx.serialization.encodeToString
import androidx.core.widget.doAfterTextChanged


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding


    private val productArray = mutableListOf<Product>()

    private var selectedPosition: Int = -1

    lateinit var adapter: ProductAdapter

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

        val listView = findViewById<ListView>(R.id.ListOfProducts)
        adapter = ProductAdapter(this, productArray)
        listView.adapter = adapter

        loadMoviesFromJsonFile()




        listView.setOnItemClickListener { _, _, position, _ ->
            selectedPosition = position
            adapter.setSelectedPosition(position)
        }

        val searchInput = findViewById<EditText>(R.id.product_search)
        searchInput.addTextChangedListener {
            adapter.filter.filter(it.toString())
        }



        listView.setOnItemClickListener { _, _, position, _ ->
            selectedPosition = position

            adapter.setSelectedPosition(position)
        }


        val increaseButton = findViewById<Button>(R.id.product_increaseQuantity)

        increaseButton.setOnClickListener {
            if (selectedPosition != -1) {
                productArray[selectedPosition].Quantity++
                adapter.notifyDataSetChanged()
            }
        }

        val decreaseButton = findViewById<Button>(R.id.product_decreaseQuantity)

        decreaseButton.setOnClickListener {
            if (selectedPosition != -1) {
                if (productArray[selectedPosition].Quantity > 0) {
                    productArray[selectedPosition].Quantity--
                    adapter.notifyDataSetChanged()
                }
            }
        }

        val saveBtn = findViewById<Button>(R.id.product_saveBtn)

        saveBtn.setOnClickListener {
            saveProductsToJsonFile()
        }

        loadMoviesFromJsonFile()

    }

    private fun loadMoviesFromJsonFile() {
        try {
            val file = File(filesDir, "pantry.json")
            val jsonString = if (file.exists()) {
                file.readText()
            } else {
                val inputStream = resources.openRawResource(R.raw.pantry)
                inputStream.bufferedReader().use { it.readText() }
            }

            val json = Json { ignoreUnknownKeys = true }
            val loadedList = json.decodeFromString<List<Product>>(jsonString)

            // 🔑 zamiast tylko productArray, aktualizujemy adapter od razu
            productArray.clear()
            productArray.addAll(loadedList)

            adapter.clear()
            adapter.addAll(loadedList)  // lista w adapterze teraz od razu pełna
            adapter.notifyDataSetChanged()

            setupCategorySpinner()

            Toast.makeText(this, "Działa", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Błąd odczytu: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveProductsToJsonFile() {
        try {
            try {

                val file = File(filesDir, "pantry.json")

                val json = Json {
                    prettyPrint = true
                }

                val jsonString = json.encodeToString(productArray)

                file.writeText(jsonString)

                Toast.makeText(
                    this,
                    "Zapisano poprawnie",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                e.printStackTrace()

                Toast.makeText(
                    this,
                    e.message ?: "Nieznany błąd zapisu",
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Błąd zapisu",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupCategorySpinner() {
        val spinner = findViewById<Spinner>(R.id.category_spinner)

        // Pobieramy unikalne kategorie z produktów
        val categories = productArray
            .map { it.Category }
            .distinct()
            .sorted()
            .toMutableList()

        categories.add(0, "All") // opcja pokazująca wszystko

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                filterByCategory(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun filterByCategory(category: String) {
        val filteredList = if (category == "All") {
            productArray
        } else {
            productArray.filter { it.Category == category }
        }

        adapter.clear()
        adapter.addAll(filteredList)
        adapter.notifyDataSetChanged()
    }
}