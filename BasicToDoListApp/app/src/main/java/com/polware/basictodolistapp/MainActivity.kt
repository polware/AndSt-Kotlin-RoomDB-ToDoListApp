package com.polware.basictodolistapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.polware.basictodolistapp.adapters.GroupsAdapter
import com.polware.basictodolistapp.databinding.ActivityMainBinding
import com.polware.basictodolistapp.interfaces.GroupListener
import com.polware.basictodolistapp.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception

class MainActivity : AppCompatActivity(), GroupListener {
    private lateinit var binding: ActivityMainBinding
    private var groupsAdapter: GroupsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.recyclerViewMain.layoutManager = LinearLayoutManager(this)
        groupsAdapter = GroupsAdapter(AppData.groups, this)
        binding.recyclerViewMain.adapter = groupsAdapter

        AppData.database = ToDoDatabase.getDatabase(this)!!
        if (checkDatabaseExists()){
            // Read database content
            CoroutineScope(Dispatchers.IO).launch {
                AppData.groups = AppData.database.toDoListDao().getGroupsWithItems()
                withContext(Dispatchers.Main) {
                    groupsAdapter = GroupsAdapter(AppData.groups, this@MainActivity)
                    binding.recyclerViewMain.adapter = groupsAdapter
                }
            }
        }
        else {
            // First time run the App
            AppData.initialize()
            groupsAdapter = GroupsAdapter(AppData.groups, this)
            binding.recyclerViewMain.adapter = groupsAdapter

            // Saving groups/items to Room database
            CoroutineScope(Dispatchers.IO).launch {
                for (groupWithItems in AppData.groups){
                    AppData.database.toDoListDao().insertGroup(groupWithItems.group)
                    for (item in groupWithItems.items){
                        AppData.database.toDoListDao().insertItem(item)
                    }
                }
            }
        }

        binding.buttonNewList.setOnClickListener{
            createNewGroup()
        }
    }

    private fun checkDatabaseExists(): Boolean{
        return try {
            File(getDatabasePath(AppData.databaseFileName).absolutePath).exists()
        }
        catch (e: Exception){
            false
        }
    }

    override fun groupClicked(index: Int) {
        val intent = Intent(this, ItemsListActivity::class.java)
        intent.putExtra("groupIndex", index)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun groupHoldDown(index: Int) {
        // Delete a group
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Group")
        builder.setMessage("Are you sure to delete the group?")
        builder.setPositiveButton("Yes"){
                _, _ ->
            var groupName = AppData.groups[index].group.name

            CoroutineScope(Dispatchers.IO).launch {
                AppData.database.toDoListDao().deleteGroup(groupName)
                AppData.database.toDoListDao().deleteGroupWithItems(groupName)
            }

            AppData.groups.removeAt(index)
            groupsAdapter!!.notifyItemRemoved(index)
            groupsAdapter!!.notifyItemRangeChanged(index, AppData.groups.count())
        }
        builder.setNegativeButton("No"){
                _, _ ->
        }
        val dialogue: AlertDialog = builder.create()
        dialogue.show()
    }

    override fun onResume() {
        super.onResume()
        groupsAdapter!!.notifyDataSetChanged()
    }

    private fun createNewGroup(){
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("New Group")
        builder.setMessage("Please enter a name for the group")
        val myInput = EditText(this)
        myInput.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(myInput)
        builder.setPositiveButton("Save"){
                _, _ ->
            val groupName: String = myInput.text.toString()
            val newGroup = Groups(groupName)
            val newGroupWithItems = GroupWithItems(newGroup, mutableListOf())
            AppData.groups.add(newGroupWithItems)
            groupsAdapter!!.notifyItemInserted(AppData.groups.count())
            CoroutineScope(Dispatchers.IO).launch {
                AppData.database.toDoListDao().insertGroup(newGroup)
            }
        }
        builder.setNegativeButton("Cancel"){
                _, _ ->
        }
        val dialogue: AlertDialog = builder.create()
        dialogue.show()
    }

}