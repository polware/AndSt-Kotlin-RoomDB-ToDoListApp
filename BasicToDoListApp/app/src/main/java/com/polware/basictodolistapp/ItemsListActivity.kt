package com.polware.basictodolistapp

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.polware.basictodolistapp.adapters.ItemsAdapter
import com.polware.basictodolistapp.databinding.ActivityItemsListBinding
import com.polware.basictodolistapp.interfaces.ItemListener
import com.polware.basictodolistapp.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemsListActivity : AppCompatActivity(), ItemListener {
    private lateinit var bindingItems: ActivityItemsListBinding
    private var itemsAdapter: ItemsAdapter? = null
    lateinit var groupWithItems: GroupWithItems

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingItems = ActivityItemsListBinding.inflate(layoutInflater)
        val view = bindingItems.root
        setContentView(view)

        var selectedIndex = intent.getIntExtra("groupIndex", 0)
        groupWithItems = AppData.groups[selectedIndex]

        bindingItems.textViewTitleToolbar.text = groupWithItems.group.name
        setSupportActionBar(bindingItems.toolbarItemsList)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        bindingItems.recyclerViewActivities.layoutManager = LinearLayoutManager(this)
        itemsAdapter = ItemsAdapter(groupWithItems, this)
        bindingItems.recyclerViewActivities.adapter = itemsAdapter

        bindingItems.editTextAddItem.setOnKeyListener { view, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_ENTER){
                if (event.action == KeyEvent.ACTION_DOWN){
                    val name: String = bindingItems.editTextAddItem.text.toString()
                    val item = Items(name, groupWithItems.group.name , false)
                    groupWithItems.items.add(item)

                    CoroutineScope(Dispatchers.IO).launch {
                        AppData.database.toDoListDao().insertItem(item)
                    }

                    itemsAdapter!!.notifyItemInserted(groupWithItems.items.count())
                    bindingItems.editTextAddItem.text.clear()
                    // Hiding keyboard
                    val inputManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
            false
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun itemClicked(index: Int) {
        val item = groupWithItems.items[index]
        item.completedStatus = !(item.completedStatus)

        CoroutineScope(Dispatchers.IO).launch {
            AppData.database.toDoListDao().updateItem(groupWithItems.group.name, item.name, item.completedStatus)
        }

        itemsAdapter!!.notifyDataSetChanged()
    }

    override fun itemHoldDown(index: Int) {
        // Delete a item
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Item")
        builder.setMessage("Are you sure to delete the item?")
        builder.setPositiveButton("Yes"){
                _, _ ->
            val groupName = groupWithItems.group.name
            val itemName = groupWithItems.items[index].name

            CoroutineScope(Dispatchers.IO).launch {
                AppData.database.toDoListDao().deleteItem(groupName, itemName)
            }

            groupWithItems.items.removeAt(index)
            itemsAdapter!!.notifyItemRemoved(index)
        }
        builder.setNegativeButton("No"){
                _, _ ->
        }
        val dialogue: AlertDialog = builder.create()
        dialogue.show()
    }
}