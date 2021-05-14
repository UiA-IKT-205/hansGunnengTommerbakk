package no.uia.ikt205.thgame.api.data


import no.uia.ikt205.thgame.api.State

data class GameState(val players: MutableList<String>, val gameId: String, val state: State) {
    override fun toString(): String {
        return "Players = $players, gameId = $gameId, state = $state"
    }
}