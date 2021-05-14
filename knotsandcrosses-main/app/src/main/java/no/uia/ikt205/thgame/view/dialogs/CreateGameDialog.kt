package no.uia.ikt205.thgame.view.dialogs

import android.content.Context
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import no.uia.ikt205.knotsandcrosses.App
import no.uia.ikt205.knotsandcrosses.R
import no.uia.ikt205.knotsandcrosses.databinding.CreateGameDialogBinding
import no.uia.ikt205.thgame.GameManager
import no.uia.ikt205.thgame.api.Marks


class CreateGameDialog : BottomSheetDialogFragment() {

    private lateinit var binding: CreateGameDialogBinding

    private val context = App.context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CreateGameDialogBinding.inflate(layoutInflater, container, false)

        val sharedPref = context.getSharedPreferences(context.getString(R.string.Preference_file), Context.MODE_PRIVATE)

        binding.apply {
            dialogPlayername.setText(sharedPref.getString(context.getString(R.string.Pref_Player), ""))

            dialogButton.setOnClickListener {
                val player = dialogPlayername.text.toString()
                if (player != "" && player.length <= 30) {
                    GameManager.createGame(player) {
                        with(sharedPref.edit()) {
                            putString(context.getString(R.string.Pref_Player), player)
                            apply()
                        }

                        val args = CreateGameDialogDirections.actionCreateGameDialogToGameFragment(Marks.X, player)
                        findNavController().navigate(args)
                    }
                } else {
                    Toast.makeText(context, "Invalid username", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }
}