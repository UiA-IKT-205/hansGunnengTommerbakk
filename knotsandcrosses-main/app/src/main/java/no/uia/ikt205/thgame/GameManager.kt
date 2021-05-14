package no.uia.ikt205.thgame

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import no.uia.ikt205.thgame.api.GameManagerCallback
import no.uia.ikt205.thgame.api.Marks
import no.uia.ikt205.thgame.api.State

object GameManager {
    private val TAG = "GameManager"

    var gameId: String? = null

    private val startingGameState: State = mutableListOf(
        mutableListOf("0", "0", "0"),
        mutableListOf("0", "0", "0"),
        mutableListOf("0", "0", "0")
    )

    private var _currentGameState = MutableLiveData(startingGameState)
    val currentState: LiveData<State> get() = _currentGameState

    private var _currentPlayer = MutableLiveData<String>()
    val currentPlayer: LiveData<String> get() = _currentPlayer

    private var _players =  MutableLiveData<List<String>>()
    val players: LiveData<List<String>> = _players

    private val _winner = MutableLiveData<String>()
    val winner: LiveData<String> get() = _winner

    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage

    // Resets snackbar message to avoid it showing multiple times.
    fun resetSnackbar(){
        _snackbarMessage.value = null
    }

    fun createGame(playerName: String, callback: GameManagerCallback) {
        GameService.createGame(playerName, startingGameState) { game, error ->
            if (error != null) {
                Log.e(TAG, "Error connecting to server: $error")
                _snackbarMessage.value = "Error connecting to server: $error"
            } else if (game != null) {
                resetGame()
                gameId = game.gameId
                _players.value = listOf(playerName)
                callback(Unit)
            }
        }
    }

    fun joinGame(playerName: String, id: String, callback: GameManagerCallback) {
        GameService.joinGame(playerName, id) { game, error ->
            if (error != null) {
                Log.e(TAG, "Error connecting to server: $error")
                _snackbarMessage.value = "Error connecting to server: $error"
            } else if (game != null) {
                resetGame()
                gameId = game.gameId
                _players.value = listOf(playerName)
                callback(Unit)
            }
        }
    }

    private fun resetGame() {
        gameId = null
        _players.value = null
        _winner.value = null
        _currentGameState.value = startingGameState
        _currentPlayer.value = Marks.blank
    }

    private fun updateRemoteGame(state: State) {
        if (_players.value != null && gameId != null ) {
            GameService.updateGame(_players.value!!, gameId!!, state) { game, error ->
                if (error != null) {
                    Log.e(TAG, "Error connecting to server: $error")
                    _snackbarMessage.value = "Error connecting to server: $error"
                } else if (game != null) {
                    Log.v(TAG, "Updated game state: $game")
                    _currentGameState.value = game.state
                }
            }
        }
    }

    fun updateGame(newState: State) {
        if (_currentGameState.value != newState) {
            updateGameState(newState) {
                updateRemoteGame(newState)
            }
        }
    }

    fun startGame(): String? {
        if (gameId != null) {
            object : CountDownTimer(500000000, 5000) {
                override fun onFinish() {
                    _snackbarMessage.value = "Game has ended"
                }
                override fun onTick(millisUntilFinished: Long) {
                    pollGame(gameId!!)
                }
            }.start()

            return gameId
        } else {
            Log.e(TAG, "No gameId found")
            _snackbarMessage.value = "No game ID found"
            return null
        }
    }

    private fun pollGame(gameId: String) {
        GameService.pollGame(gameId) { game, error ->
            if (error != null) {
                Log.e(TAG, "Error connecting to server: $error")
                _snackbarMessage.value = "Error connecting to server: $error"

            } else if (game != null) {
                with(game.players) {
                    if (_players.value != this) {
                        _players.value = this
                        _currentPlayer.value = Marks.X
                    }
                }

                with(game.state) {
                    updateGameState(this) {
                        _currentGameState.value = this
                    }
                }
            }
        }
    }

    private fun updateGameState(newState: State, updater: (Unit) -> Unit) {
        _currentGameState.value!!.forEachIndexed{ i, list ->
            list.forEachIndexed{j, value ->
                if (value != newState[i][j]) {
                    if (value == Marks.blank) {
                        updater.invoke(Unit)

                        val result = checkWinner(newState)
                        if (result != null) {
                            _winner.value = result
                        } else {
                            when (newState[i][j]) {
                                Marks.O -> _currentPlayer.value = Marks.X
                                Marks.X -> _currentPlayer.value = Marks.O
                            }
                        }

                        return
                    }
                }
            }
        }
    }


    private fun checkWinner(state: State): String? {
        fun checkRow(first: String, second: String, third: String): Boolean {
            return (first != Marks.blank && first == second && second == third)
        }
        // Check horizontal
        for (i in 0..2) {
            if (checkRow(state[i][0], state[i][1], state[i][2])) {
                return state[i][0]
            }
        }
        // Check vertical
        for (i in 0..2) {
            if (checkRow(state[0][i], state[1][i], state[2][i])) {
                return state[0][i]
            }
        }
        // Check diagonals
        if (checkRow(state[0][0], state[1][1], state[2][2])) {
            return state[0][0]
        }
        if (checkRow(state[0][2], state[1][1], state[2][0])) {
            return state[0][2]
        }

        // Check for draw
        var squaresLeft = 0
        state.forEach { row ->
            if (row.contains(Marks.blank)) {
                squaresLeft++
            }
        }
        if (squaresLeft == 0){
            return "draw"
        }

        return null
    }
}