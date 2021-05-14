package no.uia.ikt205.thgame

import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import no.uia.ikt205.knotsandcrosses.App
import no.uia.ikt205.thgame.api.data.GameState
import no.uia.ikt205.thgame.api.GameServiceCallback
import no.uia.ikt205.thgame.api.State
import org.json.JSONArray
import org.json.JSONObject
import java.lang.NullPointerException


object GameService {
    private val TAG = "GameService"

    private val context = App.context

    private val requestQue: RequestQueue = Volley.newRequestQueue(context)

    private enum class APIEndpoint {
        CREATE_GAME,
        UPDATE_GAME,
        JOIN_GAME,
        POLL_GAME
    }

    private fun getAPIEndpoints(endpoint: APIEndpoint, gameId: String? = null): String {
        val basePath = "${context.getString(no.uia.ikt205.knotsandcrosses.R.string.protocol)}" +
                "${context.getString(no.uia.ikt205.knotsandcrosses.R.string.domain)}" +
                "${context.getString(no.uia.ikt205.knotsandcrosses.R.string.base_path)}"
        return when (endpoint) {
            APIEndpoint.CREATE_GAME -> basePath
            APIEndpoint.JOIN_GAME -> "$basePath/$gameId/${context.getString(no.uia.ikt205.knotsandcrosses.R.string.join_game)}"
            APIEndpoint.POLL_GAME -> "$basePath/$gameId/${context.getString(no.uia.ikt205.knotsandcrosses.R.string.poll_game)}"
            APIEndpoint.UPDATE_GAME -> "$basePath/$gameId/${context.getString(no.uia.ikt205.knotsandcrosses.R.string.update_game)}"
        }
    }

    fun createGame(player: String, state: State, callback: GameServiceCallback) {
        val data = JSONObject()
        data.put("player", player)
        data.put("state", JSONArray(state))

        request(data, getAPIEndpoints(APIEndpoint.CREATE_GAME), callback)
    }

    fun updateGame(player: List<String>, gameId: String, state: State, callback: GameServiceCallback) {
        val data = JSONObject()
        data.put("player", player)
        data.put("gameId", gameId)
        data.put("state", JSONArray(state))

        request(data, getAPIEndpoints(APIEndpoint.UPDATE_GAME, gameId), callback)
    }

    fun joinGame(player: String, gameId: String, callback: GameServiceCallback) {
        val data = JSONObject()
        data.put("player", player)
        data.put("gameId", gameId)

        Log.v(TAG, "Payload: $data")
        request(data, getAPIEndpoints(APIEndpoint.JOIN_GAME, gameId), callback)
    }

    fun pollGame(gameId: String, callback: GameServiceCallback) {
        request(null, getAPIEndpoints(APIEndpoint.POLL_GAME, gameId), callback)
    }

    private fun request(data: JSONObject?, url: String, callback: GameServiceCallback) {
        val method: Int = if (data != null) {
            Request.Method.POST
        } else {
            Request.Method.GET
        }

        val request = object : JsonObjectRequest(method, url, data,
            Response.Listener { response ->
                Log.v(TAG, "Response from server: $response")

                val gameState = Gson().fromJson(response.toString(), GameState::class.java)
                callback(gameState, null)
            },
            Response.ErrorListener { error ->
                Log.e(TAG, "Volley Error: $error")

                try {
                    callback(null, error.networkResponse.statusCode)
                } catch (e: NullPointerException) {
                    callback(null, 0)
                }
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val header = HashMap<String, String>()
                header["Content-Type"] = "application/json"
                header["Game-Service-Key"] = context.getString(no.uia.ikt205.knotsandcrosses.R.string.API_key)
                return header
            }
        }
        request.retryPolicy = DefaultRetryPolicy(10000,
            3,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQue.add(request)
    }


}

