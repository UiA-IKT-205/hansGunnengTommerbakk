package com.example.huskeliste

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.huskeliste.lists.data.Lists
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.huskeliste.databinding.ActivityMainBinding
import com.example.huskeliste.lists.ListCollectionAdapter
import com.example.huskeliste.lists.ListDepositoryManager
import com.example.huskeliste.lists.ListDetailsActivity

class ListHolder{

    companion object{
        var PickedList:Lists? = null
    }
}

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val TAG = "MainActivityCat"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listListing.layoutManager = LinearLayoutManager(this)
        binding.listListing.adapter = ListCollectionAdapter(emptyList<Lists>(), this::onListsClicked)

        ListDepositoryManager.instance.onLists = {
            (binding.listListing.adapter as ListCollectionAdapter).updateCollection(it)
        }

        ListDepositoryManager.instance.load(this)


        binding.addBtn.setOnClickListener {
            val checkList = binding.titleList.text.toString()

            binding.titleList.setText("")

            addLists(checkList)

            val ipm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            ipm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    private fun addLists(list: String) {
        val lists = Lists(list)
        val db = Firebase.firestore

        val ex = hashMapOf(
            "exists" to 1
        )

        db.collection("Lists").document(list)
            .set(ex)
            .addOnSuccessListener {
                Log.d(TAG, "Added with ID: $list")
                db.collection("Lists").document(list)
                    .set(lists)
                    .addOnSuccessListener {
                        Log.d(TAG, "List added with ID: $lists")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Failed to add List, error: ", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding exists ref", e)
            }

        ListDepositoryManager.instance.addLists(lists)
    }

    private fun onListsClicked(lists: Lists): Unit {
        ListHolder.PickedList = lists

        val intent = Intent(this, ListDetailsActivity::class.java)

        startActivity(intent)
    }
}