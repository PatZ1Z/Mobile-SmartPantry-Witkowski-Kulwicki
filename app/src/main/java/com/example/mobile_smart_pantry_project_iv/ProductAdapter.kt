package com.example.mobile_smart_pantry_project_iv

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class ProductAdapter(
    private val context: Context,
    private val products: List<Product>
) : ArrayAdapter<Product>(context, 0, products) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_product, parent, false)

        val product = products[position]

        val imageView = view.findViewById<ImageView>(R.id.productImage)
        val nameText = view.findViewById<TextView>(R.id.productName)
        val quantityText = view.findViewById<TextView>(R.id.productQuantity)

        nameText.text = product.Name
        quantityText.text = "Quantity: ${product.Quantity}"

        // ustawianie obrazka z drawable na podstawie ImageRef
        val resId = context.resources.getIdentifier(
            product.ImageRef.replace(".png", ""),
            "drawable",
            context.packageName
        )

        if (resId != 0) {
            imageView.setImageResource(resId)
        }

        return view
    }
}