package com.example.piano

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.piano.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class MainActivity : AppCompatActivity() {
    private val LOG_TAG: String = "piano:MainActivity"

    private lateinit var binding: ActivityMainBinding
    private lateinit var pianoLayout: PianoLayout

    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pianoLayout = supportFragmentManager.findFragmentById(binding.PianoLayout.id) as PianoLayout
        pianoLayout.onSave = { fileUri ->
            this.uploadFile(fileUri)
        }

        auth = Firebase.auth
        signInAnonymously()

    }

    private fun uploadFile(noteSheetFile: Uri) {
        Log.d(LOG_TAG, "Uploading file to firebase: $noteSheetFile")

        val ref = FirebaseStorage.getInstance().reference.child("pianoSongs/${noteSheetFile.lastPathSegment}")
        val uploadTask = ref.putFile(noteSheetFile)

        uploadTask.addOnSuccessListener {
            Log.d(LOG_TAG, "successfully uploaded file: $it")
        }.addOnFailureListener{
            Log.d(LOG_TAG, "Failed to upload file: $noteSheetFile", it)
        }
    }

    private fun signInAnonymously() {
        auth.signInAnonymously().addOnSuccessListener {
            Log.d(LOG_TAG, "Signed in Anonymously with user: ${it.user?.toString()}")
        }.addOnFailureListener {
            Log.d(LOG_TAG, "Signed in Anonymously failed", it)

        }
    }
}