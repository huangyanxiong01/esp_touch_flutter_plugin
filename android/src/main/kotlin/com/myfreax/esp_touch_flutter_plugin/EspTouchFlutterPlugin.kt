package com.myfreax.esp_touch_flutter_plugin

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import android.util.Log
import androidx.annotation.NonNull
import com.espressif.iot.esptouch2.provision.TouchNetUtil

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

///** EspTouchFlutterPlugin */
class EspTouchFlutterPlugin : FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  companion object {
    const val TAG = "EspTouchPlugin"
  }

  private lateinit var channel: MethodChannel
  private lateinit var context: Context
  private lateinit var wifiManager: WifiManager
  private lateinit var espTouchingEventChannel: EventChannel
  private lateinit var espTouchFinishedEventChannel: EventChannel
  private var espTouchingEventSink: EventChannel.EventSink? = null
  private var espTouchFinishedEventSink: EventChannel.EventSink? = null
  private var espTouchTask: EspTouchTask? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "esp_touch")
    channel.setMethodCallHandler(this)

    espTouchingEventChannel =
      EventChannel(flutterPluginBinding.binaryMessenger, "esp_touch_sending")
    espTouchingEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
      override fun onCancel(arguments: Any?) {
        espTouchingEventSink = null
      }

      override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
        espTouchingEventSink = events
      }
    })
    espTouchFinishedEventChannel =
      EventChannel(flutterPluginBinding.binaryMessenger, "esp_touch_finished")
    espTouchFinishedEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
      override fun onCancel(arguments: Any?) {
        espTouchFinishedEventSink = null
      }

      override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        espTouchFinishedEventSink = events
      }
    })

    context = flutterPluginBinding.applicationContext
    wifiManager = context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "send" -> send(call, result)
      "cancel" -> cancel(result)
      else -> result.notImplemented()
    }
  }

  private fun send(call: MethodCall, result: Result) {
    if (!TouchNetUtil.isWifiConnected(wifiManager)) {
      Log.d(TAG, "wifi_disconnected")
      return result.success("wifi_disconnected")
    }
    val args = call.arguments as HashMap<*, *>
    val password = args["password"] as String
    val bssid = wifiManager.connectionInfo.bssid
    val ssid = TouchNetUtil.getSsidString(wifiManager.connectionInfo)
    val count = if (args["count"] as Int == -1) Int.MAX_VALUE else args["count"] as Int
    val isBroadcast = args["isBroadcast"] as Boolean

    espTouchTask = EspTouchTask(
      context,
      password,
      ssid,
      bssid,
      count,
      isBroadcast,
      espTouchingEventSink,
      espTouchFinishedEventSink
    )
    espTouchTask!!.execute()
    return result.success("sending_password")
  }

  private fun cancel(result: Result) {
    espTouchTask?.cancel()
    result.success(true)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
