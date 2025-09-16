package co.bubotech.app_device_integrity

import com.google.android.gms.tasks.Task
import com.google.android.play.core.integrity.IntegrityTokenResponse
import com.google.android.play.core.integrity.IntegrityManager
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import android.content.Context
import android.util.Base64

class AppDeviceIntegrity(context: Context, cloudProjectNumber: Long, challenge: String?) {

    // If challenge is provided, use it as the nonce (base64 url-safe, no-wrap). Otherwise use a 40-byte placeholder.
    var nonce = if (!challenge.isNullOrBlank()) {
        Base64.encodeToString(challenge.toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)
    } else {
        Base64.encodeToString(ByteArray(40), Base64.URL_SAFE or Base64.NO_WRAP)
    }

    // Create an instance of a manager.
    val integrityManager: IntegrityManager = IntegrityManagerFactory.create(context)

    // Request the integrity token by providing a nonce.
    val integrityTokenResponse: Task<IntegrityTokenResponse> = integrityManager.requestIntegrityToken(
        IntegrityTokenRequest.builder()
            .setNonce(nonce)
            .setCloudProjectNumber(cloudProjectNumber)
            .build())

}