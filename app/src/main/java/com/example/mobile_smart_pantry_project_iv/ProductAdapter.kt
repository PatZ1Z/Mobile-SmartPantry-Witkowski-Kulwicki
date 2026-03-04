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
import com.example.mobile_smart_pantry_project_iv.databinding.ListItemProductBinding

class ProductAdapter(
    private val context: Context,
    private val allProducts: List<Product>,
    private var selectedPosition: Int = -1
) : ArrayAdapter<Product>(context, 0, allProducts.toMutableList()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val binding: ListItemProductBinding

        if (convertView == null) {
            binding = ListItemProductBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            binding.root.tag = binding
        } else {
            binding = convertView.tag as ListItemProductBinding
        }
        val product = getItem(position)!!




        binding.productName.text = product.Name
        binding.productQuantity.text = "Quantity: ${product.Quantity}"

        val resId = context.resources.getIdentifier(
            product.ImageRef,
            "drawable",
            context.packageName
        )


        Log.d("IMG_DEBUG", "Szukam: ${product.ImageRef}, resId=$resId")

        if (resId != 0) {
            binding.productImage .setImageResource(resId)
        }

        // kolorowanie
        if (position == selectedPosition) {
            binding.root.setBackgroundColor(context.getColor(android.R.color.background_light))
        } else if (product.Quantity < 6) {
            binding.root.setBackgroundColor(context.getColor(android.R.color.holo_red_light))
        } else {
            binding.root.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }

        return binding.root
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