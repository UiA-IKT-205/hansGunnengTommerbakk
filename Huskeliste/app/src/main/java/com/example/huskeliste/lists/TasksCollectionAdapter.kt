package com.example.huskeliste.lists


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.task_layout.view.*
import com.example.huskeliste.lists.data.Tasks
import com.example.huskeliste.databinding.TaskLayoutBinding

var progressTasks: Int = 0

class TasksCollectionAdapter(private var tasks:List<Tasks>) : RecyclerView.Adapter<TasksCollectionAdapter.ViewHolder>() {

    class ViewHolder(private val binding: TaskLayoutBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Tasks) {
            binding.taskTitle.text = task.task
        }
    }

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        val db = Firebase.firestore
        var count = 0
        var totalTasks: Int
        var progressCalc: Float = 0.00F
        holder.bind(task)

        holder.itemView.apply {
            taskTitle.text = task.task
            checkBox.isChecked = task.statCheckBox

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                task.statCheckBox = !task.statCheckBox

                // Updates status (Checkmark) for firestore
                db.collection("Lists")
                    .document(firebaseTaskData.replace(")", ""))
                    .collection(firebaseTaskData.replace(")", ""))
                    .document(taskTitle.text as String)
                    .update("done", checkBox.isChecked)
                    .addOnSuccessListener {
                        Log.d(TAG, "Changed checkbox status successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.d(TAG,"Failed to change status, error: ", e)
                    }

                // Calculates new progress based on checked tasks and updates firestore
                db.collection("Lists")
                    .document(firebaseTaskData.replace(")", ""))
                    .collection(firebaseTaskData.replace(")", ""))
                    .whereEqualTo("done", true)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            count++
                        }
                        totalTasks = tasks.size
                        progressCalc = (count.toFloat() / totalTasks.toFloat()) * 100

                        progressTasks = progressCalc.toInt()

                        val doc = hashMapOf(
                            "progress" to progressTasks
                        )

                        db.collection("Progress")
                            .document(firebaseTaskData.replace(")", ""))
                            .set(doc)
                            .addOnSuccessListener {
                                Log.w(TAG,"Progress changed")
                            }
                            .addOnFailureListener {e ->
                                Log.w(TAG,"Failed to update progress, error: ", e)
                            }

                        count = 0
                        totalTasks = 0
                        progressCalc = 0.0F
                    }
                    .addOnFailureListener { e ->
                       Log.w(TAG,"Failed to get progress, error: ", e)
                    }

            }
            // Deletes the selected task
            deleteTaskBtn.setOnClickListener {

                val remove = Tasks(taskTitle.text as String)
                task.statCheckBox = false

                db.collection("Lists")
                    .document(firebaseTaskData.replace(")", ""))
                    .collection(firebaseTaskData.replace(")", ""))
                    .document(taskTitle.text as String)
                    .update("done", false)
                    .addOnSuccessListener {Log.d(TAG, "Checkmark was successfully removed")}
                    .addOnFailureListener { e -> Log.w(TAG, "Failed to remove checkmark, error: ", e )}


                db.collection("Lists")
                    .document(firebaseTaskData.replace(")", ""))
                    .collection(firebaseTaskData.replace(")", ""))
                    .document(taskTitle.text as String)
                    .delete()
                    .addOnSuccessListener {Log.d(TAG, "DocumentSnapshot successfully deleted")}
                    .addOnFailureListener { e -> Log.w(TAG, "Failed to delete task, error: ", e )}

                TasksDepositoryManager.instance.deleteTask(remove)

            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TaskLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    fun updateTaskCollection(newTasks:List<Tasks>){
        tasks = newTasks
        notifyDataSetChanged()
    }

}