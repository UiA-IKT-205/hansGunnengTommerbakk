package no.uia.ikt205.thgame.api

import no.uia.ikt205.thgame.api.data.GameState

typealias State = MutableList<MutableList<String>>
typealias GameServiceCallback = (game: GameState?, error: Int?) -> Unit
typealias GameManagerCallback = (Unit) -> Unit

class Marks {
    companion object {
        const val X = "X"
        const val O = "O"
        const val blank = "0"
    }
}
