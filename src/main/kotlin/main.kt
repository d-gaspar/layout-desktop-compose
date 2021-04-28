
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
                println(">> button1")

                if (layout.ID.getValue("btnText1") == "CONNECT") {
                    layout.ID.setValue("ip", "127.0.0.1")
                    layout.ID.setValue("connectionCircle", "#32FF32")

                    layout.ID.setValue("btnText1", "DISCONNECT")
                    layout.ID.setValue("btn1Background", "#a63603")
                } else {
                    layout.ID.setValue("ip", "")
                    layout.ID.setValue("connectionCircle", "#FF3232")

                    layout.ID.setValue("btnText1", "CONNECT")
                    layout.ID.setValue("btn1Background", "#f16913")
                }

                // test
                layout.ID.tree()
            }

            /*************************************************************************************************/

            /** button 2 */
            layout.buttonClicked("button2") {
                println(">> button2:")

                layout.ID.setValue("btnText2", "on")

                layout.ID.setValue("btnText3", "off")

                layout.ID.setValue("contentBlock", "logContent.xml")
            }

            /*************************************************************************************************/

            /** button 3 */
            layout.buttonClicked("button3") {
                println(">> button3:")

                layout.ID.setValue("btnText2", "off")

                layout.ID.setValue("btnText3", "on")

                layout.ID.setValue("contentBlock", "")
            }

            /*************************************************************************************************/

            // add log button
            layout.buttonClicked("addButton") {
                println("AAAAAAAAAAAAAA")

                layout.ID.setValue("logItems", "logItem.xml")

                //layout.ID.find("logItems")?.addChild(LayoutTree("itemText", ""))

                /*layout.appendID("logItems", "logItem.xml") {

                    layout.ID["itemText"]!![0]!!.value = "TESTE"

                    layout.ID["logItems"]?.forEach { (key, value) -> println("logItems > $key = ${value.value}")}

                    layout.ID["itemText"]?.forEach { (key, value) -> println("itemText > $key = ${value.value}")}
                }*/
            }

            /*************************************************************************************************/

            /*
            // add log button
            layout.buttonClicked("addButton") {
                layout.appendID("logItems", "logItem.xml") {

                    layout.ID["itemText"]!![0]!!.value = "TESTE"

                    layout.ID["logItems"]?.forEach { (key, value) -> println("logItems > $key = ${value.value}")}

                    layout.ID["itemText"]?.forEach { (key, value) -> println("itemText > $key = ${value.value}")}
                }
            }

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
