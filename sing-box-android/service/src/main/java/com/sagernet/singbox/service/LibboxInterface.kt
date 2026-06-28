package com.sagernet.singbox.service

import android.net.VpnService
import android.os.ParcelFileDescriptor
import io.nekohasekai.sagernet.Libbox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

interface LibboxServiceInterface {
    fun logMessage(message: String)
    fun notifySpeedUpdate(upload: Long, download: Long)
    fun updateTraffic(upload: Long, download: Long)
    fun updateConnections(count: Int)
    fun updateGroups(count: Int)
}

class AndroidLibboxInterface(
    private val vpnService: VpnService,
    private val serviceInterface: LibboxServiceInterface
) : Libbox.BoxServiceInterface {

    private var tunFd: ParcelFileDescriptor? = null
    private var isActive = false

    override fun openTun(mtu: Int): Int {
        val builder = vpnService.Builder()
            .setSession("sing-box")
            .setMtu(mtu)
            .addAddress("172.19.0.1", 30)
            .addRoute("0.0.0.0", 0)
            .addRoute("::", 0)
            .addDnsServer("1.1.1.1")
            .addDnsServer("8.8.8.8")
            .setBlocking(true)

        tunFd = builder.establish()
        isActive = true

        return tunFd?.fd ?: -1
    }

    override fun writePacket(data: ByteArray): Boolean {
        if (!isActive || tunFd == null) return false

        return try {
            tunFd?.fileDescriptor?.let { fd ->
                val outputStream = java.io.FileOutputStream(fd)
                outputStream.write(data)
                outputStream.flush()
                true
            } ?: false
        } catch (e: Exception) {
            serviceInterface.logMessage("Error writing packet: ${e.message}")
            false
        }
    }

    override fun readPacket(): ByteArray? {
        if (!isActive || tunFd == null) return null

        return try {
            tunFd?.fileDescriptor?.let { fd ->
                val inputStream = java.io.FileInputStream(fd)
                val buffer = ByteArray(32767)
                val length = inputStream.read(buffer)
                if (length > 0) buffer.copyOf(length) else null
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun logMessage(message: String) {
        serviceInterface.logMessage(message)
    }

    override fun notifySpeedUpdate(upload: Long, download: Long) {
        serviceInterface.notifySpeedUpdate(upload, download)
    }

    override fun updateTraffic(upload: Long, download: Long) {
        serviceInterface.updateTraffic(upload, download)
    }

    override fun updateConnections(count: Int) {
        serviceInterface.updateConnections(count)
    }

    override fun updateGroups(count: Int) {
        serviceInterface.updateGroups(count)
    }

    fun close() {
        isActive = false
        tunFd?.close()
        tunFd = null
    }
}

class LibboxWrapper(
    private val vpnService: VpnService,
    private val serviceInterface: LibboxServiceInterface
) {
    private var boxService: Libbox.BoxService? = null
    private var libboxInterface: AndroidLibboxInterface? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    fun start(configPath: String): Boolean {
        return try {
            // Initialize Libbox
            val basePath = vpnService.filesDir.absolutePath
            val workingPath = File(basePath, "sing-box").apply { mkdirs() }.absolutePath

            Libbox.boxSetup(basePath, workingPath, BuildConfig.DEBUG)

            // Create platform interface
            libboxInterface = AndroidLibboxInterface(vpnService, serviceInterface)

            // Read configuration
            val configContent = File(configPath).readText()

            // Create and start box service
            boxService = Libbox.newService(configContent, libboxInterface!!)
            boxService?.start()

            serviceInterface.logMessage("sing-box started successfully")
            true
        } catch (e: Exception) {
            serviceInterface.logMessage("Failed to start sing-box: ${e.message}")
            false
        }
    }

    fun stop() {
        try {
            boxService?.stop()
            libboxInterface?.close()
            serviceInterface.logMessage("sing-box stopped")
        } catch (e: Exception) {
            serviceInterface.logMessage("Error stopping sing-box: ${e.message}")
        }
    }

    fun restart(configPath: String): Boolean {
        stop()
        return start(configPath)
    }

    fun isRunning(): Boolean {
        return boxService?.isRunning == true
    }
}
