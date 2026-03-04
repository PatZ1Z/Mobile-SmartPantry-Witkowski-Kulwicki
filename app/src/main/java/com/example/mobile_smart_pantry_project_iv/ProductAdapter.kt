package com.example.mobile_smart_pantry_project_iv

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Filter

class ProductAdapter(
    private val context: Context,
    private val allProducts: List<Product>,
    private var selectedPosition: Int = -1
) : ArrayAdapter<Product>(context, 0, allProducts.toMutableList()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_product, parent, false)

        val product = getItem(position)!!

        val imageView = view.findViewById<ImageView>(R.id.productImage)
        val nameText = view.findViewById<TextView>(R.id.productName)
        val quantityText = view.findViewById<TextView>(R.id.productQuantity)

        nameText.text = product.Name
        quantityText.text = "Quantity: ${product.Quantity}"

        val resId = context.resources.getIdentifier(
            product.ImageRef,
            "drawable",
            context.packageName
        )


        Log.d("IMG_DEBUG", "Szukam: ${product.ImageRef}, resId=$resId")

        if (resId != 0) {
            imageView.setImageResource(resId)
        }

        // kolorowanie
        if (position == selectedPosition) {
            view.setBackgroundColor(context.getColor(android.R.color.background_light))
        } else if (product.Quantity < 6) {
            view.setBackgroundColor(context.getColor(android.R.color.holo_red_light))
        } else {
            view.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }

        return view
    }

    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()

                val filteredList = if (constraint.isNullOrEmpty()) {
                    allProducts   // zawsze używamy pełnej listy
                } else {
                    val query = constraint.toString().lowercase()
                    allProducts.filter { it.Name.lowercase().contains(query) }
                }

                results.values = filteredList
                results.count = filteredList.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                clear()
                addAll(results?.values as List<Product>)  // aktualizujemy tylko widoczną listę
                notifyDataSetChanged()
            }
        }
    }
}