package no.uia.ikt205.thgame.view.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import no.uia.ikt205.knotsandcrosses.App
import no.uia.ikt205.knotsandcrosses.R
import no.uia.ikt205.knotsandcrosses.databinding.JoinGameDialogBinding
import no.uia.ikt205.thgame.GameManager
import no.uia.ikt205.thgame.api.Marks

class JoinGameDialog : BottomSheetDialogFragment() {

    private lateinit var binding: JoinGameDialogBinding

    private val context = App.context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = JoinGameDialogBinding.inflate(layoutInflater, container, false)

        GameManager.snackbarMessage.observe(viewLifecycleOwner, { message ->
            if (!message.isNullOrBlank()) {
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
                GameManager.resetSnackbar()
            }
        })

        val sharedPref = context.getSharedPreferences(context.getString(R.string.Preference_file), Context.MODE_PRIVATE)

        binding.apply {
            dialogPlayername.setText(sharedPref.getString(context.getString(R.string.Pref_Player), ""))

            dialogButton.setOnClickListener {
                val gameId = dialogGameId.text.toString()
                val player = dialogPlayername.text.toString()

                if (player != "" && player.length <= 30) {
                    GameManager.joinGame(player, gameId) {
                        with(sharedPref.edit()) {
                            putString(context.getString(R.string.Pref_Player), player)
                            apply()
                        }

                        val args = JoinGameDialogDirections.actionJoinGameDialogToGameFragment(Marks.O, player)
                        findNavController().navigate(args)
                    }
                } else {
                    Snackbar.make(binding.root, "Invalid username", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }


}