
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
import java.util.function.BiConsumer

fun main() {
    var layout = DesktopLayout()

    Window (
        title = "layout-desktop-compose"
    ){
        layout.getLayout("example1.xml") {

            /*************************************************************************************************/

            /** button 1 */
            layout.buttonClicked("button1") {
                println(">> button1:")

                if (layout.get("btnText1") == "CONNECT") {
                    layout.set("ip", "127.0.0.1")
                    layout.set("connectionCircle", "#32FF32")

                    layout.set("btnText1", "DISCONNECT")
                    layout.set("btn1Background", "#a63603")
                } else {
                    layout.set("ip", "")
                    layout.set("connectionCircle", "#FF3232")

                    layout.set("btnText1", "CONNECT")
                    layout.set("btn1Background", "#f16913")
                }

                // test
                layout.tree()
            }

            /*************************************************************************************************/

            /** button 2 */
            layout.buttonClicked("button2") {
                println(">> button2:")

                layout.set("btnText2", "on")

                layout.set("btnText3", "off")

                layout.set("contentBlock", "logContent.xml")
            }

            /*************************************************************************************************/

            /** button 3 */
            layout.buttonClicked("button3") {
                println(">> button3:")

                layout.set("btnText2", "off")

                layout.set("btnText3", "on")

                layout.set("contentBlock", "")
            }

            /*************************************************************************************************/

            // add log button
            layout.buttonClicked("addButton") {
                println(">> addButton:")

                //layout.set("logItems", "logItem.xml")
                layout.append("logItems", "logItem.xml")

                layout.set("itemText", "WWWWWWW", 0)
            }

            /*************************************************************************************************/

            /*
            // remove last log button
            layout.buttonClicked("removeButton") {
                layout.removeIDItem("logItems", layout.getIDList("logItems").last())

                // print test
                println(layout.getIDList("logItems"))
                layout.ID["logItems"]!!.forEach { (key, value) -> println("$key = ${value.value}")}
            }

            // clear log button
            layout.buttonClicked("clearButton") {
                layout.clearIDItems("logItems")
            }
            * */
        }
    }
}
