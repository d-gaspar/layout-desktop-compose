
/**
 * author: Daniel Gaspar Goncalves
 * github: https://github.com/d-gaspar/layout-desktop-compose
 * APACHE LICENSE 2.0
 *
 * */

import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

fun main() {
    var layoutManager = DesktopComposeLayout()

    Window (
        title = "layout-desktop-compose"
    ){
        layoutManager.getLayout("example1.xml")

        //layoutManager.layoutIds["ip"]?.value = "AAAAA"
    }
}