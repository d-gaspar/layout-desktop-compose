
/**
 * author: Daniel Gaspar Goncalves
 * github: https://github.com/d-gaspar/layout-desktop-compose
 * APACHE LICENSE 2.0
 *
 * */

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class DesktopComposeLayout {
    var layoutDir : String = ""

    constructor(layoutDir : String = "res/layout/") {
        this.layoutDir = layoutDir
    }

    fun readLayout(fileName : String) { // xml file
        try {
            val xmlFile : File = File(layoutDir + fileName)

            val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)

            // first element
            val body = doc.documentElement

            // get all childs
            val nodes = body.childNodes

            for (i in 0..nodes.length) {
                println("nodeName: " + nodes.item(i).nodeName)

                if(nodes.item(i).attributes != null) {
                    println("att: " + nodes.item(i).attributes)
                }

                println("content: " + nodes.item(i).textContent)
            }

        } catch (e : Exception) {
            //e.printStackTrace()
        }
    }
}