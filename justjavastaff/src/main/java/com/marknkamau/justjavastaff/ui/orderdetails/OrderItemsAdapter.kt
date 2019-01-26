package com.marknkamau.justjavastaff.ui.orderdetails

/**
 * Created by Mark Njung'e.
 * mark.kamau@outlook.com
 * https://github.com/MarkNjunge
 */

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marknjunge.core.model.OrderItem
import com.marknkamau.justjavastaff.R
import kotlinx.android.synthetic.main.item_order_item.view.*

class OrderItemsAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<OrderItemsAdapter.ViewHolder>() {
    private val items by lazy { mutableListOf<OrderItem>() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.item_order_item))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    fun setItems(items: MutableList<OrderItem>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(item: OrderItem) {
            itemView.run {
                tvItemName.text = item.itemName
                tvItemQuantity.text = "x ${item.itemQty}"
                tvChocolate.visibility = if (item.itemChoc) View.VISIBLE else View.GONE
                tvCinnamon.visibility = if (item.itemCinnamon) View.VISIBLE else View.GONE
                tvMarshmallows.visibility = if (item.itemMarshmallow) View.VISIBLE else View.GONE
            }
        }
    }

    private fun ViewGroup.inflate(layoutRes: Int): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, false)
    }
}