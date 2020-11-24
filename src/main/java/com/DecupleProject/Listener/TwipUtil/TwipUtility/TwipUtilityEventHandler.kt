package com.DecupleProject.Listener.TwipUtil.TwipUtility

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors


class TwipUtilityEventHandler {

    companion object {

        var MAX_THREAD_POOL = 5
        var listeners = CopyOnWriteArrayList<TwipUtilityEventListener>()

        @Synchronized
        @JvmStatic
        fun getListeners(): List<TwipUtilityEventListener> {
            return listeners
        }

        @Synchronized
        @JvmStatic
        fun addListener(listener: TwipUtilityEventListener) {
            if (getListeners().indexOf(listener) == -1) listeners.add(listener)
        }

        @Synchronized
        @JvmStatic
        fun removeListener(listener: TwipUtilityEventListener) {
            if (getListeners().indexOf(listener) != -1) listeners.remove(listener)
        }

        @Synchronized
        @JvmStatic
        fun callEvent(caller: Class<*>, streamer: String, amount: Int, comment: String, nickname: String) {
            callEvent(caller, streamer, amount, comment, nickname, true)
        }

        @Synchronized
        @JvmStatic
        fun callEvent(caller: Class<*>, streamer: String, amount: Int, comment: String, nickname: String, doAsync: Boolean) {
            if (doAsync) callEventByAsynch(caller, streamer, amount, comment, nickname) else callEventBySynch(caller, streamer, amount, comment, nickname)
        }

        @Synchronized
        @JvmStatic
        private fun callEventByAsynch(caller: Class<*>, streamer: String, amount: Int, comment: String, nickname: String) {
            val executorService = Executors.newFixedThreadPool(MAX_THREAD_POOL)
            for (listener in listeners) {
                executorService.execute {
                    if (listener.javaClass.name == caller.name) {
                        // Skip
                    } else {
                        listener.onDonateReceived(streamer, amount, comment, nickname)
                    }
                }
            }
            executorService.shutdown()
        }

        @Synchronized
        @JvmStatic
        private fun callEventBySynch(caller: Class<*>, streamer: String, amount: Int, comment: String, nickname: String) {
            for (listener in listeners) {
                if (listener.javaClass.name == caller.name) {
                    // Skip
                } else {
                    listener.onDonateReceived(streamer, amount, comment, nickname)
                }
            }
        }
    }

}