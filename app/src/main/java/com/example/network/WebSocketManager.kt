package com.example.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.concurrent.TimeUnit

interface WebSocketEventListener {
    fun onConnected()
    fun onDisconnected()
    fun onChatMessageReceived(senderPhone: String, text: String, mediaUri: String)
    fun onCallOfferReceived(callerName: String, callerPhone: String, callType: String, sdpOffer: String)
    fun onCallAnswerReceived(sdpAnswer: String)
    fun onCallHangupReceived()
}

class WebSocketManager {
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .connectTimeout(5000, TimeUnit.MILLISECONDS)
        .build()

    private var webSocket: WebSocket? = null
    var isConnected = false
        private set

    var listener: WebSocketEventListener? = null
    private var serverUrl = "ws://10.0.2.2:4000/ws"

    fun connect(url: String = serverUrl, userPhone: String = "") {
        if (isConnected) return
        serverUrl = url

        val request = Request.Builder()
            .url(url)
            .addHeader("User-Phone", userPhone)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                isConnected = true
                Log.d("WebSocketManager", "Connected to signaling server: $url")
                // Register user
                val registerPayload = JSONObject().apply {
                    put("type", "register")
                    put("phone", userPhone)
                }
                webSocket.send(registerPayload.toString())
                listener?.onConnected()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val json = JSONObject(text)
                    when (json.optString("type")) {
                        "chat_message" -> {
                            val from = json.optString("from")
                            val msg = json.optString("text")
                            val media = json.optString("mediaUri")
                            listener?.onChatMessageReceived(from, msg, media)
                        }
                        "call_offer" -> {
                            val callerName = json.optString("callerName", "Caller")
                            val callerPhone = json.optString("from")
                            val callType = json.optString("callType", "AUDIO")
                            val sdp = json.optString("sdp")
                            listener?.onCallOfferReceived(callerName, callerPhone, callType, sdp)
                        }
                        "call_answer" -> {
                            val sdp = json.optString("sdp")
                            listener?.onCallAnswerReceived(sdp)
                        }
                        "call_hangup" -> {
                            listener?.onCallHangupReceived()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("WebSocketManager", "Error parsing WebSocket message: $text", e)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                isConnected = false
                listener?.onDisconnected()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                isConnected = false
                Log.w("WebSocketManager", "Signaling server offline or unreachable (${t.message}). Local fallback active.")
                listener?.onDisconnected()
            }
        })
    }

    fun sendChatMessage(from: String, to: String, text: String, mediaUri: String = "") {
        val payload = JSONObject().apply {
            put("type", "chat_message")
            put("from", from)
            put("to", to)
            put("text", text)
            put("mediaUri", mediaUri)
            put("timestamp", System.currentTimeMillis())
        }
        webSocket?.send(payload.toString())
    }

    fun sendCallOffer(from: String, callerName: String, to: String, callType: String, sdp: String = "simulated_sdp_offer") {
        val payload = JSONObject().apply {
            put("type", "call_offer")
            put("from", from)
            put("callerName", callerName)
            put("to", to)
            put("callType", callType)
            put("sdp", sdp)
        }
        webSocket?.send(payload.toString())
    }

    fun sendCallAnswer(from: String, to: String, sdp: String = "simulated_sdp_answer") {
        val payload = JSONObject().apply {
            put("type", "call_answer")
            put("from", from)
            put("to", to)
            put("sdp", sdp)
        }
        webSocket?.send(payload.toString())
    }

    fun sendCallHangup(from: String, to: String) {
        val payload = JSONObject().apply {
            put("type", "call_hangup")
            put("from", from)
            put("to", to)
        }
        webSocket?.send(payload.toString())
    }

    fun disconnect() {
        webSocket?.close(1000, "Normal closure")
        webSocket = null
        isConnected = false
    }
}
