package com.polware.basictodolistapp.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.polware.basictodolistapp.models.GroupWithItems
import com.polware.basictodolistapp.models.Groups
import com.polware.basictodolistapp.models.Items

@Dao
interface ToDoListDAO {

    // Insert a group
    // Para las corutinas se usa función de tipo suspend
    @Insert
    suspend fun insertGroup(group: Groups)

    // Insert a item
    @Insert
    suspend fun insertItem(item: Items)

    // Get all groups and items
    @Transaction @Query ("SELECT * FROM Groups")
    suspend fun getGroupsWithItems(): MutableList<GroupWithItems>

    // Get items from group
    @Query ("SELECT * FROM Items WHERE group_name=:groupName")
    suspend fun getItemsFromGroup(groupName: String): MutableList<Items>

    // Delete group
    @Query ("DELETE FROM Groups WHERE group_name=:groupName")
    suspend fun deleteGroup(groupName: String)

    // Delete item
    @Query ("DELETE FROM Items WHERE group_name=:groupName AND item_name=:itemName")
    suspend fun deleteItem(groupName: String, itemName: String)

    // Delete all items from group
    @Query ("DELETE FROM Items WHERE group_name=:groupName")
    suspend fun deleteGroupWithItems(groupName: String)

    // Update an item
    @Query (
        "UPDATE Items SET completedStatus=:completedValue " +
            "WHERE item_name=:itemName AND group_name=:groupName")
    suspend fun updateItem(groupName: String, itemName: String, completedValue: Boolean)

}