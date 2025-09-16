package co.bubotech.app_device_integrity

import android.app.Activity
import android.content.Context
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** AppDeviceIntegrityPlugin */
class AppDeviceIntegrityPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {

  private lateinit var channel: MethodChannel
  private lateinit var context: Context
  private lateinit var activity: Activity

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "app_attestation")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "getAttestationServiceSupport") {
      val gcp = call.argument<Long>("gcp")
      val challenge = call.argument<String>("challengeString")

      if (gcp == null) {
        result.error("-1", "Missing required argument 'gcp' for Android", null)
        return
      }

      val attestation = AppDeviceIntegrity(context, gcp, challenge)
      attestation.integrityTokenResponse
        .addOnSuccessListener { response ->
          val integrityToken: String = response.token()
          result.success(integrityToken)
        }
        .addOnFailureListener { e ->
          result.error("-2", "Integrity API request failed: ${e.message}", null)
        }
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onDetachedFromActivity() {
    // no-op
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    // no-op
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity;
  }

  override fun onDetachedFromActivityForConfigChanges() {
    // no-op
  }
}