
/**
 * author: Daniel Gaspar Goncalves
 * github: https://github.com/d-gaspar/layout-desktop-compose
 * APACHE LICENSE 2.0
 *
 * */

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class DesktopComposeLayout {
    var layoutDir : String = ""

    constructor(layoutDir : String = "res/layout/") {
        this.layoutDir = layoutDir
    }

    @Composable
    fun getLayout(fileName : String) { // xml file
        val xmlFile : File = File(layoutDir + fileName)

        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)

        /** first element - must be: box, column or row  */
        //val firstNode = doc.documentElement

        /** recursive function to search all xml childs */
        checkXMLChilds(doc)
    }

    @Composable
    private fun checkXMLChilds(node: Node) {
        val childsNodes = node.childNodes

        for (i in 0 until childsNodes.length) {
            when (childsNodes.item(i).nodeName) {
                "box"       -> {
                    val (modifier, otherAttributes) = getModifier(childsNodes.item(i).attributes)

                    Box (
                        modifier = modifier
                    ){
                        checkXMLChilds(childsNodes.item(i))
                    }
                }
                "column"    -> {
                    val (modifier, otherAttributes) = getModifier(childsNodes.item(i).attributes)

                    Column (
                        modifier = modifier//,
                        //horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        checkXMLChilds(childsNodes.item(i))
                    }
                }
                "row"       -> {
                    val (modifier, otherAttributes) = getModifier(childsNodes.item(i).attributes)

                    Row (
                        modifier = modifier,
                        verticalAlignment = if ("verticalAlignment" in otherAttributes.keys) {
                            when (otherAttributes["color"]) {
                                "top" -> Alignment.Top
                                "bottom" -> {
                                    Alignment.Bottom
                                }
                                "centerVertically" -> Alignment.CenterVertically
                                else -> Alignment.Top
                            }
                        } else Alignment.Top
                    ){
                        checkXMLChilds(childsNodes.item(i))
                    }
                }
                "text"      -> {
                    val (modifier, otherAttributes) = getModifier(childsNodes.item(i).attributes)

                    Text(
                        childsNodes.item(i).textContent,
                        color = if ("color" in otherAttributes.keys && otherAttributes["color"]!!.isNotEmpty()) getColorByHex(otherAttributes["color"]!!) else Color.Unspecified,
                        fontSize = if ("fontSize" in otherAttributes.keys && otherAttributes["fontSize"]!!.isNotEmpty()) otherAttributes["fontSize"]!!.toInt().sp else TextUnit.Unspecified,
                        fontWeight = if ("fontWeight" in otherAttributes.keys && otherAttributes["fontWeight"]!!.isNotEmpty()) FontWeight(otherAttributes["fontWeight"]!!.toInt()) else null
                    )
                }
                "button"    -> {
                    val (modifier, otherAttributes) = getModifier(childsNodes.item(i).attributes)

                    Button(
                        onClick = {},
                        modifier = modifier
                    ){
                        Text(childsNodes.item(i).textContent)
                    }

                    // add margin
                    Spacer(modifier = Modifier.width(10.dp).height(10.dp))
                }
            }
        }

        return
    }

    private fun getModifier(attributes: NamedNodeMap) : Pair<Modifier, HashMap<String, String>> {
        var modifier = Modifier.defaultMinSizeConstraints()
        var otherAttributes = HashMap<String, String>()

        if (attributes.length > 0) {
            /** ATTRIBUTE SEQUENCE
             * padding -> last one
             * background
             *  */
            var attributeOrder : ArrayList<Int>  = ArrayList()

            // initialize indexes
            for (i in 0 until attributes.length) {
                attributeOrder.add(i)
            }

            // get attribute order by importance
            var lastAttributes = arrayOf("padding", "background")
            var lastIndex = attributes.length - 1
            for (att in lastAttributes) {
                for (i in 0 until attributes.length) {
                    if (att == attributes.item(i).nodeName) {
                        attributeOrder[lastIndex] = i
                        attributeOrder[i] = lastIndex
                        lastIndex--
                        break
                    }
                }
            }

            /**
             * add desktop compose items
             * */
            for (i in attributeOrder) {
                when (attributes.item(i).nodeName) {
                    "background" -> {
                        var value = attributes.item(i).nodeValue

                        modifier = modifier.background(getColorByHex(value))
                    }
                    "clip" -> {
                        when (attributes.item(i).nodeValue) {
                            "circle" -> modifier = modifier.clip(CircleShape)
                        }
                    }
                    "fillMaxHeight" -> {
                        var value = attributes.item(i).nodeValue.toFloatOrNull()

                        // default value
                        if (value == null) value = 1f

                        modifier = modifier.fillMaxHeight(value)
                    }
                    "fillMaxSize" -> {
                        var value = attributes.item(i).nodeValue.toFloatOrNull()

                        // default value
                        if (value == null) value = 1f

                        modifier = modifier.fillMaxSize(value)
                    }
                    "fillMaxWidth" -> {
                        var value = attributes.item(i).nodeValue.toFloatOrNull()

                        // default value
                        if (value == null) value = 1f

                        modifier = modifier.fillMaxWidth(value)
                    }
                    "padding" -> {
                        var stringValue = attributes.item(i).nodeValue.replace(" ", "")
                        var value : MutableList<Int> = mutableListOf(0, 0, 0, 0)

                        if (stringValue.isNotEmpty()) {
                            for ((aux, v) in stringValue.split(",").withIndex()) {
                                value[aux] = v.toInt()
                            }
                        }

                        modifier = modifier.padding(
                            start = value[0].dp,
                            top = value[1].dp,
                            end = value[2].dp,
                            bottom = value[3].dp
                        )
                    }
                    "size" -> {
                        var stringValue = attributes.item(i).nodeValue.replace(" ", "")
                        var value : MutableList<Int> = mutableListOf()

                        if (stringValue.isNotEmpty()) {
                            for (v in stringValue.split(",")) {
                                value.add(v.toInt())
                            }

                            when (value.size) {
                                1 -> {
                                    modifier = modifier.size(value[0].dp)
                                }
                                2 -> {
                                    modifier = modifier.size(value[0].dp, value[1].dp)
                                }
                            }
                        }
                    }
                    else -> {
                        otherAttributes[attributes.item(i).nodeName] = attributes.item(i).nodeValue
                    }
                }
            }
        }

        return Pair(modifier, otherAttributes)
    }

    private fun getColorByHex(value : String) : Color {
        var valueAux = value

        // remove "#" on the beginning
        if (valueAux[0] == '#') valueAux = valueAux.substring(1)

        // add FF -> transparency 100%
        if (valueAux.length == 6) valueAux = "FF$valueAux"

        return Color(valueAux.toLong(radix = 16))
    }
}