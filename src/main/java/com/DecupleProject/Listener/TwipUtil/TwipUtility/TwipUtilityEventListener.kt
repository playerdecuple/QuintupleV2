package com.DecupleProject.Listener.TwipUtil.TwipUtility

import java.util.*

interface TwipUtilityEventListener : EventListener {

    fun onDonateReceived(streamer: String, amount: Int, comment: String, nickname: String)

}