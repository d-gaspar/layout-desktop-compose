
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
    var layout = DesktopComposeLayout()

    Window (
        title = "layout-desktop-compose"
    ){
        layout.getLayout("example1.xml", onButtonClick = {
            // list all ids
            //mainLayout.ID.forEach { (key, value) -> println("$key = ${value.value}")}

            // button 1
            layout.buttonClicked("button1") {
                 layout.setID("ip", "127.0.0.1")

                if (layout.getID("btnText1") == "CONNECT") {
                    layout.setID("connectionCircle", "#32FF32")
                    layout.setID("btnText1", "DISCONNECT")

                    layout.setID("btn1Background", "#a63603")
                } else {
                    layout.setID("connectionCircle", "#FF3232")
                    layout.setID("btnText1", "CONNECT")

                    layout.setID("btn1Background", "#f16913")
                }
            }

            // button 2
            layout.buttonClicked("button2") {
                layout.setID(
                    "btnText2",
                    layout.getID("btnText2") + "A"
                )

                layout.setID("contentBlock", "logContent.xml")
            }

            // button 3
            layout.buttonClicked("button3") {
                layout.setID(
                    "btnText3",
                    layout.getID("btnText3") + "B"
                )

                layout.setID("contentBlock", "")

                layout.setID("logItems", "")
            }

            // log refresh button
            layout.buttonClicked("logRefreshButton") {
                layout.setID("logItems", "logItem.xml")
            }
        })
    }
}
