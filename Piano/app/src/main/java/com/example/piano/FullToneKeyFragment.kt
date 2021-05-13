package com.example.piano

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import com.example.piano.databinding.FragmentFullToneKeyBinding
import com.example.piano.databinding.FragmentPianoLayoutBinding
import kotlinx.android.synthetic.main.fragment_full_tone_key.view.*

class FullToneKeyFragment : Fragment() {

    private var _binding: FragmentFullToneKeyBinding? = null
    private val binding get() = _binding!!
    private lateinit var note:String

    var onKeyDown:((note:String) -> Unit)? = null
    var onKeyUp:((note:String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            note = it.getString("NOTE") ?: "?"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFullToneKeyBinding.inflate(inflater)
        val view = binding.root

        view.fullToneKey.setOnTouchListener(object: View.OnTouchListener {



            override fun onTouch(v: View?, event: MotionEvent?):Boolean {
                when(event?.action){
                    MotionEvent.ACTION_DOWN -> this@FullToneKeyFragment.onKeyDown?.invoke(note)
                    MotionEvent.ACTION_UP -> this@FullToneKeyFragment.onKeyUp?.invoke(note)
                }
                return true
            }
        })

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(note: String) =
            FullToneKeyFragment().apply {
                arguments = Bundle().apply {
                    putString("NOTE", note)
                }
            }
    }
}