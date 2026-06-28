package com.sagernet.singbox.service

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
class SingBoxTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()

        if (qsTile.state == Tile.STATE_ACTIVE) {
            // Stop VPN
            SingBoxVpnService.stopService(this)
        } else {
            // Start VPN
            SingBoxVpnService.startService(this)
        }

        // Update tile state
        qsTile.state = if (qsTile.state == Tile.STATE_ACTIVE) {
            Tile.STATE_INACTIVE
        } else {
            Tile.STATE_ACTIVE
        }
        qsTile.updateTile()
    }

    private fun updateTile() {
        qsTile.state = Tile.STATE_INACTIVE
        qsTile.label = "sing-box"
        qsTile.updateTile()
    }
}
