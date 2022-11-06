package com.polware.basictodolistapp.adapters

import android.view.LayoutInflater
import android.graphics.Color
import android.graphics.Paint
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.polware.basictodolistapp.R
import com.polware.basictodolistapp.interfaces.ItemListener
import com.polware.basictodolistapp.models.GroupWithItems
import com.polware.basictodolistapp.models.Items

class ItemsAdapter(private val groupWithItems: GroupWithItems, listenerContext: ItemListener):
    RecyclerView.Adapter<ItemsViewHolder>() {

    private var itemsInterface: ItemListener = listenerContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemsViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        val item: Items = groupWithItems.items[position]
        holder.bind(item)

        holder.itemView.setOnClickListener{
            itemsInterface.itemClicked(position)
        }
        holder.itemView.setOnLongClickListener{
            itemsInterface.itemHoldDown(position)
            true
        }

    }

    override fun getItemCount(): Int = groupWithItems.items.size

}

class ItemsViewHolder(inflater: LayoutInflater, parent: ViewGroup):
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_row, parent, false)) {

    private var textViewItemName: TextView? = null
    private var checkBoxItem: CheckBox? = null

    init {
        textViewItemName = itemView.findViewById(R.id.textViewItemName)
        checkBoxItem = itemView.findViewById(R.id.checkBoxItem)
    }

    fun bind(item: Items){
        textViewItemName!!.text = item.name
        checkBoxItem!!.isChecked = item.completedStatus

        if (item.completedStatus) {
            textViewItemName!!.paintFlags = textViewItemName!!.paintFlags or
                    Paint.STRIKE_THRU_TEXT_FLAG
            itemView.setBackgroundColor(Color.LTGRAY)
        }
        else {
            textViewItemName!!.paintFlags = textViewItemName!!.paintFlags and
                    Paint.STRIKE_THRU_TEXT_FLAG.inv()
            itemView.setBackgroundColor(Color.TRANSPARENT)
        }
    }
}