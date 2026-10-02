package com.a.injector.data.system.adb

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import com.a.injector.R
import dadb.AdbKeyPair
import dadb.Dadb
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.annotation.Single
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Single
class AdbApiImpl(
    private val context: Context
): AdbApi {
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager

    private suspend fun findActiveAdbPort(): Int {
        return suspendCancellableCoroutine { continuation ->
            val isActive = continuation.isActive
            val throwable = Throwable(context.getString(R.string.exception_adb_failed))

            val resolveListener = object : NsdManager.ResolveListener {
                override fun onResolveFailed(
                    serviceInfo: NsdServiceInfo?,
                    errorCode: Int
                ) {
                    if (isActive) {
                        continuation.resumeWithException(throwable)
                    }
                }

                override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
                    val port = serviceInfo?.port ?: -1
                    if (port > 0 && isActive) {
                        continuation.resume(port)
                    } else {
                        continuation.resumeWithException(throwable)
                    }
                }
            }

            val discoveryListener = object : NsdManager.DiscoveryListener {
                override fun onDiscoveryStarted(serviceType: String?) = Unit
                override fun onDiscoveryStopped(serviceType: String?) = Unit
                override fun onServiceLost(serviceInfo: NsdServiceInfo?) = Unit

                override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
                    if (serviceInfo?.serviceType?.contains("_adb-tls-connect") == true) {
                        nsdManager.resolveService(serviceInfo, resolveListener)
                        nsdManager.stopServiceDiscovery(this)
                    }
                }

                override fun onStartDiscoveryFailed(
                    serviceType: String?,
                    errorCode: Int
                ) {
                    nsdManager.stopServiceDiscovery(this)
                    if (isActive) {
                        continuation.resumeWithException(throwable)
                    }
                }

                override fun onStopDiscoveryFailed(
                    serviceType: String?,
                    errorCode: Int
                ) {
                    nsdManager.stopServiceDiscovery(this)
                }
            }

            nsdManager.discoverServices(
                "_adb-tls-connect._tcp.",
                NsdManager.PROTOCOL_DNS_SD,
                discoveryListener
            )

            continuation.invokeOnCancellation {
                nsdManager.stopServiceDiscovery(discoveryListener)
            }
        }
    }

    private var adbInstance: Dadb? = null

    override suspend fun connect() {
        val activePort = findActiveAdbPort()
        AdbKeyPair.generate(
            privateKeyFile = File(context.filesDir, "adbkey"),
            publicKeyFile = File(context.filesDir, "adbkey.pub")
        )
        val keyPair = AdbKeyPair.read(
            privateKeyFile = File(context.filesDir, "adbkey"),
            publicKeyFile = File(context.filesDir, "adbkey.pub")
        )

        adbInstance?.close()
        adbInstance = Dadb.create(
            host = "127.0.0.1",
            port = activePort,
            keyPair = keyPair
        )
    }

    override suspend fun executeCommand(command: String): String {
        val adb = adbInstance ?: throw Exception(context.getString(R.string.exception_adb_failed))
        val response = adb.shell(command)

        if (response.exitCode != 0) {
            throw Exception("Command Failed")
        }

        return response.allOutput
    }

    override fun disconnect() {
        adbInstance?.close()
        adbInstance = null
    }
}