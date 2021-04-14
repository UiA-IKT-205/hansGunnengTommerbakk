package com.example.huskeliste.lists

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.huskeliste.lists.data.Lists

class ListDepositoryManager {

    private lateinit var listCollection: MutableList<Lists>

    var onLists: ((List<Lists>) -> Unit)? = null

    fun load(context: Context) {

        listCollection = mutableListOf()

        val TAG = "ListLists"
        val db = Firebase.firestore

        // Imports checklists that are in the Firestone database
        // Returns errorcode should it not get it.
        db.collection("Lists")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val addFirebase = Lists(document.id)
                    addLists(addFirebase)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting documents: ", e)
            }

        onLists?.invoke(listCollection)
    }

    fun addLists(lists: Lists) {
        listCollection.add(lists)
        onLists?.invoke(listCollection)
    }

    fun removeLists(lists: Lists) {
        listCollection.remove(lists)
        onLists?.invoke(listCollection)
    }

    companion object {
        val instance = ListDepositoryManager()
    }

}