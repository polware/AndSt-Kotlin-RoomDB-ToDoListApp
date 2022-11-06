package com.polware.basictodolistapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.polware.basictodolistapp.R
import com.polware.basictodolistapp.interfaces.GroupListener
import com.polware.basictodolistapp.models.GroupWithItems

class GroupsAdapter(private val list: List<GroupWithItems>, listenerContext: GroupListener):
    RecyclerView.Adapter<GroupsViewHolder>() {

    private var groupInterface: GroupListener = listenerContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return GroupsViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        val groupWithItems: GroupWithItems = list[position]
        holder.bind(groupWithItems)

        holder.itemView.setOnClickListener{
            groupInterface.groupClicked(position)
        }

        holder.itemView.setOnLongClickListener{
            groupInterface.groupHoldDown(position)
            true
        }
    }

    override fun getItemCount(): Int = list.size

}

class GroupsViewHolder(inflater: LayoutInflater, parent: ViewGroup):
    RecyclerView.ViewHolder(inflater.inflate(R.layout.group_items, parent, false)) {
        private var textViewGroupName: TextView? = null
        private var textViewGroupItemsCount: TextView? = null

        init{
            textViewGroupName = itemView.findViewById(R.id.textViewGroupName)
            textViewGroupItemsCount = itemView.findViewById(R.id.textViewGroupItemsCount)
        }

        fun bind(groupWithItems: GroupWithItems){
            textViewGroupName!!.text = groupWithItems.group.name
            textViewGroupItemsCount!!.text = "${groupWithItems.items.count()} items"
        }
    }