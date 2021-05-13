package com.example.piano

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.piano.data.Note
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.piano.databinding.FragmentPianoLayoutBinding
import kotlinx.android.synthetic.main.fragment_piano_layout.view.*
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class PianoLayout : Fragment() {

    private var _binding:FragmentPianoLayoutBinding? = null
    private val binding get() = _binding!!

    private val fullTones = listOf("C", "D", "E", "F", "G", "A", "B", "C", "D", "E", "F", "G", "A", "B")
    private val halfTones = listOf("C#", "D#", "F#", "G#", "A#", "C#", "D#", "F#", "G#", "A#")

    private var score:MutableList<Note> = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentPianoLayoutBinding.inflate(layoutInflater)
        val view = binding.root


        val fm = childFragmentManager
        val ft = fm.beginTransaction()

        fullTones.forEach{

            val fullToneKey = FullToneKeyFragment.newInstance(it)
            var startPlayFull:Long = 0
            var fullToneStartTime:String = ""

            fullToneKey.onKeyDown = {
                val currentDateTime = LocalDateTime.now()
                fullToneStartTime = currentDateTime.format(DateTimeFormatter.ISO_TIME)
                println("Piano key down $it at $fullToneStartTime")
            }

            fullToneKey.onKeyUp = {
                val endPlayFull = System.nanoTime() // Stop time relative to device in nano seconds
                var totalFullTone:Double = 0.0
                var timeFullTone:Long = 0
                timeFullTone = endPlayFull - startPlayFull // Calculates the time the Full tone key was pressed (in nano seconds)
                totalFullTone = timeFullTone.toDouble() / 1000000000 // Converts timeFullTone to seconds
                val note = Note(it,fullToneStartTime,totalFullTone)

                score.add(note) // Adds it to file
                Toast.makeText(activity,"Note: $it, Start time: $fullToneStartTime, Duration $totalFullTone seconds", Toast.LENGTH_SHORT).show()
            }

            ft.add(view.whitePianoKeys.id,fullToneKey,"note_$it")

        }

        halfTones.forEach{

            val halfToneKey = HalfToneKeyFragment.newInstance(it)
            var startPlayHalf:Long = 0
            var halfToneStartTime:String = ""

            halfToneKey.onKeyDown = {

                startPlayHalf = System.nanoTime()
                val currentDateTime = LocalDateTime.now()
                halfToneStartTime = currentDateTime.format(DateTimeFormatter.ISO_TIME)
                println("Piano key down $it at $halfToneStartTime")
            }

            halfToneKey.onKeyUp = {
                val endPlayHalf = System.nanoTime() // Stop time relative to device in nano seconds
                var totalHalfTone:Double = 0.0
                var timeHalfTone:Long = 0

                timeHalfTone = endPlayHalf - startPlayHalf // Calculates the time the Half tone key was pressed (in nano seconds)
                totalHalfTone = timeHalfTone.toDouble() / 1000000000 // Converts timeHalfTone to seconds
                val note = Note(it,halfToneStartTime,totalHalfTone)

                score.add(note) // Adds the "Note" content to file
                Toast.makeText(activity,"Note: $it, Start time: $halfToneStartTime, Duration $totalHalfTone seconds", Toast.LENGTH_SHORT).show()
            }

            ft.add(view.blackPianoKeys.id,halfToneKey,"note_$it")

        }
        ft.commit()

        view.saveScoreBt.setOnClickListener {
            var fileName = view.fileNameTextEdit.text.toString()
            fileName = "$fileName.musikk"
            val path = this.activity?.getExternalFilesDir(null)
            val file = File(path, fileName)
            val fileExists = file.exists()

            // Checks is the file already exists
            if (score.count() > 0 && fileName.isNotEmpty() && path != null){
                if(fileExists){
                    Toast.makeText(activity,"$fileName already exists. Enter different file name.", Toast.LENGTH_SHORT).show()
                } else{
                    FileOutputStream(File(path,fileName),true).bufferedWriter().use { writer ->
                        score.forEach {
                            writer.write("${it.toString()}\n")
                        }
                    }
                }

            }

        }

        return view // Return root view
    }

}