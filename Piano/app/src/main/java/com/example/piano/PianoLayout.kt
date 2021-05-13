package com.example.piano

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.piano.databinding.FragmentPianoLayoutBinding
import kotlinx.android.synthetic.main.fragment_piano_layout.view.*


class PianoLayout : Fragment() {

    private var _binding:FragmentPianoLayoutBinding? = null
    private val binding get() = _binding!!

    private val fullTones = listOf("C", "D", "E", "F", "G", "A", "B", "C", "D", "E", "F", "G", "A", "B")
    private val halfTones = listOf("C#", "D#", "F#", "G#", "A#", "C#", "D#", "F#", "G#", "A#")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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

            fullToneKey.onKeyDown = {
                println("Piano key down $it")
            }

            fullToneKey.onKeyUp = {
                println("Piano key up $it")
            }

            ft.add(view.whitePianoKeys.id,fullToneKey,"note_$it")

        }

        halfTones.forEach{

            val halfToneKey = HalfToneKeyFragment.newInstance(it)

            halfToneKey.onKeyDown = {
                println("Piano key down $it")
            }

            halfToneKey.onKeyUp = {
                println("Piano key up $it")
            }

            ft.add(view.blackPianoKeys.id,halfToneKey,"note_$it")

        }
        ft.commit()

        return view // Return root view
    }

}