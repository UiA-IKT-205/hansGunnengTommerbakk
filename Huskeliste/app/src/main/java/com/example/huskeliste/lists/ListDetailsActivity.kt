package com.example.huskeliste.lists

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.tasks_in_list_layout.*
import com.example.huskeliste.ListHolder
import com.example.huskeliste.lists.data.Lists
import com.example.huskeliste.lists.data.Tasks
import com.example.huskeliste.databinding.TasksInListLayoutBinding

var firebaseTaskData = ""
val TAG = "ListDetailsActivity"

class ListDetailsActivity : AppCompatActivity() {

    private lateinit var binding: TasksInListLayoutBinding
    lateinit var lists:Lists

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TasksInListLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvTaskList.layoutManager = LinearLayoutManager(this)
        binding.rvTaskList.adapter = TasksCollectionAdapter(emptyList<Tasks>())

        TasksDepositoryManager.instance.onTasks = {
            (binding.rvTaskList.adapter as TasksCollectionAdapter).updateTaskCollection(it)
        }

        TasksDepositoryManager.instance.loadTasks(this)

        val db = Firebase.firestore

        if(ListHolder.PickedList != null){
            lists = ListHolder.PickedList!!

            firebaseTaskData = lists.toString().replace("Lists(checklists=", "")

            // Imports tasks that are in the Firestone database
            // Checks status of each task
            // Returns errorcode should it not get it.
            db.collection("Lists")
                .document(firebaseTaskData.replace(")", ""))
                .collection(firebaseTaskData.replace(")", ""))
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        val dataForm = document.data.toString().replace("{done=", "")
                        val formattedDone = dataForm.replace("}", "")
                        println(formattedDone)
                        var taskFirebase: Tasks
                        if (formattedDone == "true") {
                            taskFirebase = Tasks(document.id, statCheckBox = true)
                        } else {
                            taskFirebase = Tasks(document.id, statCheckBox = false)
                        }

                        TasksDepositoryManager.instance.addTask(taskFirebase)
                    }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error getting documents: ", e)
                }

            binding.titleList.text = firebaseTaskData.replace(")", "")

        } else{
            setResult(RESULT_CANCELED, Intent().apply {
            })
            finish()
        }




        // Updates the progressbar for the checklist
        db.collection("Progress")
            .document(firebaseTaskData.replace(")", ""))
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val progress = snapshot.data.toString().replace("{progress=", "")
                    val firebaseProgressData = progress.replace("}", "")
                    binding.progressBar.progress = firebaseProgressData.toInt() // Updates progress bar value
                } else {
                    Log.d(TAG, "No data")
                }
            }

        // Adds the task to the list and updates firestore
        addTaskBtn.setOnClickListener {
            val taskTitle = etTaskTitle.text.toString()

            if(taskTitle.isNotEmpty()) {
                var tasks = Tasks(taskTitle, false)
                val firebaseTaskData = lists.toString().replace("Lists(checklists=", "")
                val boolValueTask = hashMapOf(

                    "done" to false
                )

                db.collection("Lists")
                    .document(firebaseTaskData.replace(")", ""))
                    .collection(firebaseTaskData.replace(")", ""))
                    .document(taskTitle)
                    .set(boolValueTask)
                    .addOnSuccessListener {
                        Log.d(TAG, "Task successfully added with ID: $taskTitle")
                        tasks = Tasks(taskTitle, false)
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Failed to add task, error:", e)
                    }


                TasksDepositoryManager.instance.addTask(tasks)
                etTaskTitle.text.clear()
            }
        }


    }

    override fun onBackPressed() {
        finish()
    }
}