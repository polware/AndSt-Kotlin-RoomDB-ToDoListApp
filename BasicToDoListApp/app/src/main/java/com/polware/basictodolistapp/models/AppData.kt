package com.polware.basictodolistapp.models

import com.polware.basictodolistapp.ToDoDatabase

class AppData {

    companion object DataHolder {
        lateinit var database: ToDoDatabase
        var databaseFileName = "todolist_db"
        var groups: MutableList<GroupWithItems> = mutableListOf()

        fun initialize(){
            val group1 = Groups("Casa")
            val item1 = Items("Pagar facturas", group1.name, false)
            val item2 = Items("Comprar mercado", group1.name, false)
            val item3 = Items("Mantener sostenido para borrar", group1.name, true)
            val groupWithItems1 = GroupWithItems(group1, mutableListOf(item1, item2, item3))

            val group2 = Groups("Trabajo")
            val item4 = Items("Hacer informe", group2.name, false)
            val item5 = Items("Responder emails", group2.name, false)
            val groupWithItems2 = GroupWithItems(group2, mutableListOf(item4, item5))

            groups = mutableListOf(groupWithItems1, groupWithItems2)
        }
    }

}