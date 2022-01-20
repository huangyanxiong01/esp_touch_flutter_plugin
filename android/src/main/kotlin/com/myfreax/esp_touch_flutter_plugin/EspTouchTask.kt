package com.myfreax.esp_touch_flutter_plugin

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.espressif.iot.esptouch.EsptouchTask
import com.espressif.iot.esptouch.IEsptouchResult
import com.espressif.iot.esptouch.IEsptouchTask
import com.espressif.iot.esptouch.util.TouchNetUtil
import com.google.gson.Gson
import io.flutter.plugin.common.EventChannel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

data class EspTouchResult(val address: String, val bssid: String)
class EspTouchTask(
    private val context: Context,
    private val password: String,
    private val ssid: String,
    private val bssid: String,
    private val count: Int,
    private val isBroadcast: Boolean,
    private val espTouchingEventSink: EventChannel.EventSink?,
    private val espTouchFinishedEventSink: EventChannel.EventSink?
) : CoroutineScope {
    companion object {
        private const val TAG = "EspTouchTask"
    }

    private val gson by lazy {
        Gson()
    }
    private var uiThreadHandler: Handler? = Handler(Looper.getMainLooper())
    private var espTouchTask: IEsptouchTask? = null
    private val lock = Any()
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    // call this method to cancel a coroutine when you don't need it anymore,
    fun cancel() {
        Log.d(TAG, "cancel")
        espTouchTask?.interrupt()
        job.cancel()
    }

    fun execute() = launch {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Doing in background")
            synchronized(lock) {
                espTouchTask = EsptouchTask(
                    ssid.toByteArray(),
                    TouchNetUtil.parseBssid2bytes(bssid),
                    password.toByteArray(),
                    context
                )
                espTouchTask?.setPackageBroadcast(isBroadcast)
                espTouchTask?.setEsptouchListener {
                    Log.d(TAG, "The ESP device: ${it.bssid} Connected wifi")
                    uiThreadHandler?.post {
                        espTouchingEventSink?.success(toJSON(it))
                    }
                }
            }
            val espTouchResultList = espTouchTask?.executeForResults(count)
            Log.d(TAG, "The ESP devices: ${espTouchResultList?.size} Connected wifi")
            uiThreadHandler?.post {
                espTouchFinishedEventSink?.success(espTouchResultList?.map {
                    toJSON(it)
                })
            }
        }
    }

    private fun toJSON(iEsptouchResult: IEsptouchResult?): String {
        return gson.toJson(
            EspTouchResult(
                iEsptouchResult?.inetAddress?.hostAddress ?: "",
                iEsptouchResult?.bssid?:""
            )
        )
    }
}