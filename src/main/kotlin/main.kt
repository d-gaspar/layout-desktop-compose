
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
import androidx.compose.ui.unit.dp

fun main() {
    var mainLayout = DesktopComposeLayout()

    Window (
        title = "layout-desktop-compose"
    ){
        mainLayout.getLayout("example1.xml", onVariableChange = {
            if(mainLayout.ID["button1"]?.value == "on") {
                mainLayout.ID["ip"]?.value = "127.0.0.1"

                mainLayout.ID["button1"]?.value = "off"

                mainLayout.ID.forEach { (key, value) -> println("$key = ${value.value}")}
                mainLayout.ID["AAA"]?.value = "red"
            }
        })
    }
}