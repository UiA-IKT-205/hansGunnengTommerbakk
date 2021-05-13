package no.uia.ikt205.thgame.dialogs

import androidx.fragment.app.DialogFragment

interface GameDialogListener {
    fun onDialogCreateGame(player:String)
    fun onDialogJoinGame(player: String, gameId:String)
}
