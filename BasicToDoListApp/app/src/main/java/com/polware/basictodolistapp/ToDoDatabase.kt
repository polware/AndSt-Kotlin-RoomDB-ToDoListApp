package com.polware.basictodolistapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.polware.basictodolistapp.interfaces.ToDoListDAO
import com.polware.basictodolistapp.models.AppData
import com.polware.basictodolistapp.models.Groups
import com.polware.basictodolistapp.models.Items

@Database (entities = [Items::class, Groups::class], version = 1)
abstract class ToDoDatabase: RoomDatabase() {

    abstract fun toDoListDao(): ToDoListDAO

    companion object {
        var instance: ToDoDatabase?= null

        fun getDatabase(context: Context): ToDoDatabase? {
            if(instance == null) {
                synchronized(ToDoDatabase::class){
                    instance = Room.databaseBuilder(context.applicationContext,
                        ToDoDatabase::class.java, AppData.databaseFileName).build()
                }
            }
            return instance
        }
    }

}