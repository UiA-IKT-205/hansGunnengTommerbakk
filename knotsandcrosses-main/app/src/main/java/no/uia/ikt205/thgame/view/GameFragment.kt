package no.uia.ikt205.thgame.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import no.uia.ikt205.knotsandcrosses.App
import no.uia.ikt205.knotsandcrosses.R
import no.uia.ikt205.knotsandcrosses.databinding.GameFragmentBinding
import no.uia.ikt205.thgame.GameManager
import no.uia.ikt205.thgame.api.Marks
import no.uia.ikt205.thgame.api.State

class GameFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: GameFragmentBinding

    private val context = App.context
    private val TAG = "GameFragment"

    private var mark = ""
    private var players = listOf<String>()
    private var currentPlayer = ""


    private var currentState: State = mutableListOf(
        mutableListOf("0", "0", "0"),
        mutableListOf("0", "0", "0"),
        mutableListOf("0", "0", "0")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = GameFragmentBinding.inflate(layoutInflater, container, false)

        // mark is either X or O
        val args:GameFragmentArgs by navArgs()
        val player = args.playerName
        mark = args.mark



        // Start the game, aka polling
        val gameId = GameManager.startGame()
        binding.gameId.text = gameId

        binding.apply {
            position1.setOnClickListener(this@GameFragment)
            position2.setOnClickListener(this@GameFragment)
            position3.setOnClickListener(this@GameFragment)
            position4.setOnClickListener(this@GameFragment)
            position5.setOnClickListener(this@GameFragment)
            position6.setOnClickListener(this@GameFragment)
            position7.setOnClickListener(this@GameFragment)
            position8.setOnClickListener(this@GameFragment)
            position9.setOnClickListener(this@GameFragment)
        }

        GameManager.currentState.observe(viewLifecycleOwner, { newState ->
            Log.v(TAG, "Live data state update: $newState")
            updateBoardUI(newState)

            currentState = newState
        })

        GameManager.players.observe(viewLifecycleOwner, {
            Log.v(TAG, "Live data players update: $it")
            players = it

            binding.Player1.text = players[0]
            if (players.size > 1) {
                binding.Player2.text = players[1]
            }
        })

        GameManager.currentPlayer.observe(viewLifecycleOwner, {
            Log.v(TAG, "Live data current player update: $it")
            currentPlayer = it

            updateTurnUI()
        })

        GameManager.winner.observe(viewLifecycleOwner, {
            when(it) {
                Marks.X -> binding.winner.text = "${players[0]} Wins!"
                Marks.O -> binding.winner.text = "${players[1]} Wins!"
                "draw"   -> binding.winner.text = "No winner"
            }
            binding.turn.text = ""
        })

        return binding.root
    }

    private fun updateTurnUI() {
        when (currentPlayer) {
            Marks.O -> {
                when (mark) {
                    Marks.X -> binding.turn.text = context.getText(R.string.turn_waiting)
                    Marks.O -> binding.turn.text = context.getText(R.string.turn_your)
                }
            }
            Marks.X -> {
                when (mark) {
                    Marks.X -> binding.turn.text = context.getText(R.string.turn_your)
                    Marks.O -> binding.turn.text = context.getText(R.string.turn_waiting)
                }
            }
        }
    }

    private fun updateBoardUI(newState: State) {
        newState.forEachIndexed{i, list ->
            list.forEachIndexed{j, value ->
                if (currentState[i][j] != value) {
                    when ("$i$j") {
                        "00" -> binding.position1.text = value
                        "01" -> binding.position2.text = value
                        "02" -> binding.position3.text = value
                        "10" -> binding.position4.text = value
                        "11" -> binding.position5.text = value
                        "12" -> binding.position6.text = value
                        "20" -> binding.position7.text = value
                        "21" -> binding.position8.text = value
                        "22" -> binding.position9.text = value
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        fun updateState(index1: Int, index2: Int) {
            // Creates a new copy of the current state
            val newState:State = mutableListOf()
            currentState.forEachIndexed { i, list ->
                newState.add(mutableListOf())

                list.forEach { value ->
                    newState[i].add(value)
                }
            }
            newState[index1][index2] = mark

            GameManager.updateGame(newState)
        }

        if (v != null && currentPlayer == mark) {
            when (v.id) {
                R.id.position1 -> updateState(0, 0)
                R.id.position2 -> updateState(0, 1)
                R.id.position3 -> updateState(0, 2)
                R.id.position4 -> updateState(1, 0)
                R.id.position5 -> updateState(1, 1)
                R.id.position6 -> updateState(1, 2)
                R.id.position7 -> updateState(2, 0)
                R.id.position8 -> updateState(2, 1)
                R.id.position9 -> updateState(2, 2)
            }
        }
    }
}