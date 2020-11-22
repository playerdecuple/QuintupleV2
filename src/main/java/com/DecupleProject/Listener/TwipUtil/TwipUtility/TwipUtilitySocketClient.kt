// Based on patrick-mc/twipe.
// It is under the terms of the GNU GPL 2.

// TwipUtil By DeveloperDecuple

package com.DecupleProject.Listener.TwipUtil.TwipUtility

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketFactory
import com.neovisionaries.ws.client.WebSocketFrame
import java.lang.RuntimeException
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

internal class TwipUtilitySocketClient(streamer: String, key: String) {
    init {
        requireNotNull(WebSocketFactory().apply {
            sslContext = try {
                SSLContext.getInstance("TLS").apply {
                    init(null, arrayOf(object : X509TrustManager {
                        override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {}
                        override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {}
                        override fun getAcceptedIssuers(): Array<X509Certificate>? {
                            return null
                        }
                    }), null)
                }
            } catch (exception: Exception) {
                throw RuntimeException("Failed to initialize SSLContext", exception)
            }

            verifyHostname = false
        }.createSocket(twipAddress.format(key, "1.1.59")).apply {
            addListener(object : WebSocketAdapter() {
                override fun onConnected(websocket: WebSocket?, headers: MutableMap<String, MutableList<String>>?) {
                    println("Connected to twip ~ $streamer")
                }

                override fun onDisconnected(
                    websocket: WebSocket?,
                    serverCloseFrame: WebSocketFrame?,
                    clientCloseFrame: WebSocketFrame?,
                    closedByServer: Boolean
                ) {
                    println("Disconnected from ~ $streamer")
                    TwipUtilitySocketClient(streamer, key)
                }

                override fun onTextMessage(websocket: WebSocket?, text: String) {
                    when (text.substring(0, 1).toInt()) {
                        0 -> { // OPEN
                            val json: JsonObject = parser.parse(text.substring(1)).asJsonObject
                            val interval = json.getAsJsonPrimitive("pingInterval").asLong

                            var timer = Timer().schedule(object : TimerTask() {
                                override fun run() {
                                    websocket!!.sendText("2")
                                }
                            }, interval, interval)
                        }

                        4 -> { // MESSAGE
                            when (text.substring(1, 2).toInt()) {
                                2 -> { // EVENT
                                    val json: JsonArray = parser.parse(text.substring(2)).asJsonArray
                                    when (json[0].asJsonPrimitive.asString) {
                                        "new donate" -> { // DONATION EVENT
                                            val donation = json[1].asJsonObject

                                            val amount = donation.getAsJsonPrimitive("amount").asInt
                                            val comment = donation.getAsJsonPrimitive("comment").asString
                                            val nickname = donation.getAsJsonPrimitive("nickname").asString

                                            TwipUtilityEventHandler.callEvent(TwipUtilitySocketClient.javaClass, streamer, amount, comment, nickname)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            })
        }).connect()
    }

    private companion object {
        private const val twipAddress = "wss://io.mytwip.net/socket.io/?alertbox_key=%s&version=%s&transport=websocket"

        private val parser by lazy {
            JsonParser()
        }
    }

}
