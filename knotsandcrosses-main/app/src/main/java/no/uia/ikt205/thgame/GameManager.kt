package no.uia.ikt205.thgame

import android.util.Log
import no.uia.ikt205.knotsandcrosses.api.GameService
import no.uia.ikt205.knotsandcrosses.api.data.Game
import no.uia.ikt205.knotsandcrosses.api.data.GameState

object GameManager {

    private const val TAG = "GameManager"
    var player:String? = null
    var state:GameState? = null

    private val StartingGameState:GameState = mutableListOf(
        mutableListOf("0", "0", "0"),
        mutableListOf("0", "0", "0"),
        mutableListOf("0", "0", "0"))

    fun createGame(player:String){
        GameService.createGame(player,StartingGameState) { game: Game?, err: Int? ->
            if(err != null){
                Log.e(TAG, "Error connecting to server: $err")
                ///TODO("What is the error code? 406 you forgot something in the header. 500 the server di not like what you gave it")
            } else {
                /// TODO("We have a game. What to do?)
            }
        }

    }



}