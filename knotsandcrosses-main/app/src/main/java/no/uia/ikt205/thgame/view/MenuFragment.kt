package no.uia.ikt205.thgame.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import no.uia.ikt205.knotsandcrosses.R
import no.uia.ikt205.knotsandcrosses.databinding.MenuFragmentBinding
import androidx.navigation.fragment.findNavController
import no.uia.ikt205.thgame.GameManager

class MenuFragment : Fragment() {

    lateinit var binding: MenuFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = MenuFragmentBinding.inflate(layoutInflater)

        binding.startGame.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_createGameDialog)
        }

        binding.joinGame.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_joinGameDialog)
        }

        GameManager.snackbarMessage.observe(viewLifecycleOwner, { message ->
            if (!message.isNullOrBlank()) {
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
                // reset live date value to avoid it being shown again
                GameManager.resetSnackbar()
            }
        })

        return binding.root
    }
}
