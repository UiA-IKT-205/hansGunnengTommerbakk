package com.example.huskeliste.lists

import android.content.Context
import com.example.huskeliste.lists.data.Tasks


class TasksDepositoryManager {

    private lateinit var tasksCollection: MutableList<Tasks>

    var onTasks: ((List<Tasks>) -> Unit)? = null

    fun deleteTask(task: Tasks) {
        tasksCollection.remove(task)
        onTasks?.invoke(tasksCollection)
        println("Deleted")
    }

    fun loadTasks(context: Context) {
        tasksCollection = mutableListOf()
        onTasks?.invoke(tasksCollection)
    }


    fun addTask(task: Tasks) {
        tasksCollection.add(task)
        onTasks?.invoke(tasksCollection)
    }

    companion object {
        val instance = TasksDepositoryManager()
    }

}