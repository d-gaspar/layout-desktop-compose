
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
        mainLayout.getLayout("example1.xml", onButtonClick = {
            // list all ids
            //mainLayout.ID.forEach { (key, value) -> println("$key = ${value.value}")}

            // button 1
            mainLayout.buttonClicked("button1") {
                mainLayout.ID["ip"]?.value = "127.0.0.1"

                if (mainLayout.ID["btnText1"]?.value == "CONNECT") {
                    mainLayout.ID["connectionCircle"]?.value = "#32FF32"
                    mainLayout.ID["btnText1"]?.value = "DISCONNECT"

                    mainLayout.ID["btn1Background"]?.value = "#a63603"
                } else {
                    mainLayout.ID["connectionCircle"]?.value = "#FF3232"
                    mainLayout.ID["btnText1"]?.value = "CONNECT"

                    mainLayout.ID["btn1Background"]?.value = "#f16913"
                }
            }

            // button 2
            mainLayout.buttonClicked("button2") {
                mainLayout.ID["btnText2"]?.value += "A"

                mainLayout.ID["contentBlock"]?.value = "logContent.xml"
            }

            // button 3
            mainLayout.buttonClicked("button3") {
                mainLayout.ID["btnText3"]?.value += "B"

                mainLayout.ID["contentBlock"]?.value = ""
            }

            // log refresh button
            mainLayout.buttonClicked("logRefreshButton") {
                mainLayout.ID["logItems"]?.value = "logItem.xml"
            }
        })
    }
}
