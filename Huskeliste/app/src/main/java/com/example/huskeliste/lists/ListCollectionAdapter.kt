package com.example.huskeliste.lists

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.list_layout.view.*
import com.example.huskeliste.lists.data.Lists
import com.example.huskeliste.databinding.ListLayoutBinding

class ListCollectionAdapter(private var cLists:List<Lists>, private val onListsClicked:(Lists) -> Unit) : RecyclerView.Adapter<ListCollectionAdapter.ViewHolder>() {

    class ViewHolder(private val binding:ListLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(lists: Lists, onListsClicked: (Lists) -> Unit) {
            binding.titleList.text = lists.checklists

            val TAG = "ListCollectionAdapter"
            val db = Firebase.firestore

            // Gets progress for all the checklists
            // Should always match progress data in TasksCollectionAdapter
            db.collection("Progress")
                .document(lists.checklists)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current main data: ${snapshot.data}")
                        val progress = snapshot.data.toString().replace("{progress=", "")
                        val formattedProgress = progress.replace("}", "")
                        binding.mainProgressBar.progress = formattedProgress.toInt()
                    } else {
                        Log.d(TAG, "Current main data: null")
                    }
                }

            binding.card.setOnClickListener {
                onListsClicked(lists)
            }
        }
    }

    override fun getItemCount(): Int = cLists.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lists = cLists[position]
        holder.bind(lists,onListsClicked)

        holder.itemView.apply {
            titleList.text = lists.checklists


            deleteBtn.setOnClickListener {
                val TAG = "Tasks"

                val db = Firebase.firestore

                // Deletes selected checklist
                db.collection("Lists")
                    .document(titleList.text as String)
                    .delete()
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                val remove = Lists(titleList.text as String)
                ListDepositoryManager.instance.removeLists(remove)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    fun updateCollection(newLists:List<Lists>){
        cLists = newLists
        notifyDataSetChanged()
    }


}