
/**
 * author: Daniel Gaspar Goncalves
 * github: https://github.com/d-gaspar/layout-desktop-compose
 * APACHE LICENSE 2.0
 *
 * */

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
//import androidx.compose.material.Button
//import androidx.compose.material.ButtonConstants
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import java.io.EOFException
import java.io.File
import java.io.IOException
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilderFactory

class DesktopComposeLayout {
    var layoutDir : String = ""
    var onButtonClick : (() -> Unit)? = null
    var ID = HashMap<String, MutableState<String>>()
    //var layoutContentID = HashMap<String, (() -> Unit)>()

    constructor(layoutDir : String = "res/layout/") {
        this.layoutDir = layoutDir
    }

    /*********************************************************************************************************/

    private fun addID(id : String, newValue : String) {
        // empty id
        if (id.isEmpty()) return

        if (id !in ID.keys) {
            ID[id] = mutableStateOf(newValue)
        }
    }

    private fun buttonOnClick(id : String) {
        // empty id
        if (id.isEmpty()) return

        if (id in ID.keys) {
            ID[id]!!.value = "on"

            onButtonClick?.invoke()

            ID[id]!!.value = "off"
        }
    }

    /*********************************************************************************************************/

    @Composable
    fun getLayout(fileName : String, onButtonClick : (() -> Unit)? = null) { // xml file
        if (onButtonClick != null) {
            this.onButtonClick = onButtonClick
        }

        val xmlFile : File = File(layoutDir + fileName)

        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)

        /** first element - must be: box, column or row  */
        //val firstNode = doc.documentElement

        /** recursive function to search all xml childs */
        checkXMLChilds(doc)
    }

    /*********************************************************************************************************/

    @Composable
    private fun checkXMLChilds(node: Node) {
        val childNodes = node.childNodes

        // empty node
        if (childNodes.length == 0) return

        for (i in 0 until childNodes.length) {
            when (childNodes.item(i).nodeName) {
                "box"       -> {
                    val (modifier, otherAttributes) = getModifier(childNodes.item(i).attributes)

                    Box(
                        modifier = modifier
                    ){
                        checkXMLChilds(childNodes.item(i))
                    }

                    // add margin
                    if ("margin" in  otherAttributes.keys) {
                        var marginAux = otherAttributes["margin"]!!.split(",").map { it.toInt() }

                        Spacer(modifier = Modifier.width(marginAux.first().dp).height(marginAux.last().dp))
                    } else {
                        Spacer(modifier = Modifier.width(0.dp).height(0.dp))
                    }
                }
                "column"    -> {
                    val (modifier, otherAttributes) = getModifier(childNodes.item(i).attributes)

                    Column (
                        modifier = modifier,
                        horizontalAlignment = if ("horizontalAlignment" in otherAttributes.keys) {
                            when (otherAttributes["horizontalAlignment"]) {
                                "end" -> Alignment.End
                                "center" -> Alignment.CenterHorizontally
                                "centerHorizontally" -> Alignment.CenterHorizontally
                                else -> Alignment.Start
                            }
                        } else Alignment.Start
                    ){
                        if ("id" in otherAttributes) {
                            otherAttributes["id"] = otherAttributes["id"]!!.replace("$", "")

                            // default layout
                            if (otherAttributes["id"] !in ID.keys) {
                                // create id on hashmap IDs
                                addID(otherAttributes["id"]!!, "")
                            }

                            // import xml
                            if (ID[otherAttributes["id"]]!!.value.isNotEmpty()) {
                                getLayout(ID[otherAttributes["id"]]!!.value)
                            } else {
                                checkXMLChilds(childNodes.item(i))
                            }
                        } else {
                            checkXMLChilds(childNodes.item(i))
                        }
                    }

                    // add margin
                    if ("margin" in  otherAttributes.keys) {
                        var marginAux = otherAttributes["margin"]!!.split(",").map { it.toInt() }

                        Spacer(modifier = Modifier.width(marginAux.first().dp).height(marginAux.last().dp))
                    } else {
                        Spacer(modifier = Modifier.width(0.dp).height(0.dp))
                    }
                }
                "row"       -> {
                    val (modifier, otherAttributes) = getModifier(childNodes.item(i).attributes)

                    Row (
                        modifier = modifier,
                        verticalAlignment = if ("verticalAlignment" in otherAttributes.keys) {
                            when (otherAttributes["verticalAlignment"]) {
                                "bottom" -> Alignment.Bottom
                                "center" -> Alignment.CenterVertically
                                "centerVertically" -> Alignment.CenterVertically
                                else -> Alignment.Top
                            }
                        } else Alignment.Top
                    ){
                        checkXMLChilds(childNodes.item(i))
                    }

                    // add margin
                    if ("margin" in  otherAttributes.keys) {
                        var marginAux = otherAttributes["margin"]!!.split(",").map { it.toInt() }

                        Spacer(modifier = Modifier.width(marginAux.first().dp).height(marginAux.last().dp))
                    } else {
                        Spacer(modifier = Modifier.width(0.dp).height(0.dp))
                    }
                }
                "text"      -> {
                    /*for (ii in 0 until childNodes.item(i).attributes.length) {
                        println("TEXT: " + childNodes.item(i).attributes.item(ii))
                    }*/

                    val (modifier, otherAttributes) = getModifier(childNodes.item(i).attributes)

                    // text id
                    val text = childNodes.item(i).textContent.replace(
                        regexText()
                    ){
                        addID(it.groupValues[1], "")
                        ""+ID[it.groupValues[1]]?.value
                    }

                    Text(
                        text,
                        modifier = modifier,
                        color = if ("color" in otherAttributes.keys) getColorByHex(otherAttributes["color"]!!) else Color.Unspecified,
                        fontSize = if ("fontSize" in otherAttributes.keys && otherAttributes["fontSize"]!!.isNotEmpty()) otherAttributes["fontSize"]!!.toInt().sp else TextUnit.Unspecified,
                        fontWeight = if ("fontWeight" in otherAttributes.keys && otherAttributes["fontWeight"]!!.isNotEmpty()) FontWeight(otherAttributes["fontWeight"]!!.toInt()) else null
                    )

                    // add margin
                    if ("margin" in  otherAttributes.keys) {
                        var marginAux = otherAttributes["margin"]!!.split(",").map { it.toInt() }

                        Spacer(modifier = Modifier.width(marginAux.first().dp).height(marginAux.last().dp))
                    } else {
                        Spacer(modifier = Modifier.width(10.dp).height(10.dp))
                    }
                }
                "button"    -> {
                    // ignore background and color because they aren't a modifier
                    var ignoreAttributes : ArrayList<String> = arrayListOf()
                    for (ii in 0 until childNodes.item(i).attributes.length) {
                        var nodeName = childNodes.item(i).attributes.item(ii).nodeName

                        if (nodeName in arrayOf("background", "color"))
                            ignoreAttributes.add(nodeName)
                    }

                    val (modifier, otherAttributes) = getModifier(childNodes.item(i).attributes, ignoreAttributes)

                    // add id
                    if ("onClick" in otherAttributes.keys){
                        addID(otherAttributes["onClick"]!!.toString().replace("\$", ""), "off")
                    }

                    // text id
                    val text = childNodes.item(i).textContent.replace(
                        regexText()
                    ){
                        // id
                        addID(it.groupValues[1], it.groupValues[2])

                        // default text
                        if (it.groupValues[1].isNotEmpty()) {
                            ""+ID[it.groupValues[1]]?.value
                        } else {
                            it.groupValues[2]
                        }
                    }

                    Button(
                        modifier = modifier,
                        onClick = {
                            if ("onClick" in otherAttributes.keys) {
                                buttonOnClick(otherAttributes["onClick"]!!.toString().replace("\$", ""))
                            }
                        },
                        colors = ButtonConstants.defaultButtonColors(
                            backgroundColor = if ("background" in otherAttributes.keys) getColorByHex(otherAttributes["background"]!!) else MaterialTheme.colors.primary,
                            contentColor = if ("color" in otherAttributes.keys) getColorByHex(otherAttributes["color"]!!) else contentColorFor(MaterialTheme.colors.primary)
                        )
                    ){
                        checkXMLChilds(childNodes.item(i))
                    }

                    // add margin
                    if ("margin" in  otherAttributes.keys) {
                        var marginAux = otherAttributes["margin"]!!.split(",").map { it.toInt() }

                        Spacer(modifier = Modifier.width(marginAux.first().dp).height(marginAux.last().dp))
                    } else {
                        Spacer(modifier = Modifier.width(10.dp).height(10.dp))
                    }
                }
            }
        }

        return
    }

    /*********************************************************************************************************/

    private fun getModifier(
        attributes : NamedNodeMap,
        ignoreAttributes : ArrayList<String> = arrayListOf()
    ) : Pair<Modifier, HashMap<String, String>> {
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
                var nodeName = attributes.item(i).nodeName
                var nodeID = ""
                var nodeValue = attributes.item(i).nodeValue.toString().replace(
                    regexAttribute()
                ){
                    // nodeID
                    addID(it.groupValues[1], it.groupValues[2])
                    nodeID = it.groupValues[1]

                    // nodeValue
                    it.groupValues[2]
                }

                // ignore attribute
                if (nodeName in ignoreAttributes) {
                    if (nodeID in ID.keys) {
                        otherAttributes[nodeName] = ID[nodeID]!!.value
                    } else {
                        otherAttributes[nodeName] = nodeValue
                    }
                    continue
                }

                when (nodeName) {
                    "background" -> {
                        modifier = if (nodeID in ID.keys) {
                            modifier.background(
                                getColorByHex(ID[nodeID]!!.value)
                            )
                        } else {
                            modifier.background(
                                getColorByHex(nodeValue)
                            )
                        }
                    }
                    "clip" -> {
                        when (nodeValue) {
                            "circle" -> modifier = modifier.clip(CircleShape)
                        }
                    }
                    "fillMaxHeight" -> {
                        modifier = modifier.fillMaxHeight(
                            nodeValue.toFloatOrNull() ?: 1f
                        )
                    }
                    "fillMaxSize" -> {
                        modifier = modifier.fillMaxSize(
                            nodeValue.toFloatOrNull() ?: 1f
                        )
                    }
                    "fillMaxWidth" -> {
                        modifier = modifier.fillMaxWidth(
                            nodeValue.toFloatOrNull() ?: 1f
                        )
                    }
                    "padding" -> {
                        var stringValue = nodeValue.replace(" ", "")
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
                        var stringValue = nodeValue.replace(" ", "")
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
                        otherAttributes[nodeName] = nodeValue
                    }
                }
            }
        }

        return Pair(modifier, otherAttributes)
    }

    /*********************************************************************************************************/

    // attribute="$id:#default"
    private fun regexAttribute(): Regex {
        return Regex("""\$([a-zA-Z0-9]*)=?(#[a-zA-Z0-9]*)""")
    }

    // $id
    private fun regexText(): Regex {
        return Regex("""\$([a-zA-Z0-9]*)=?([a-zA-Z0-9]*)""")
    }

    /*********************************************************************************************************/

    private fun getColorByHex(value : String) : Color {
        // empty value
        if (value.isEmpty()) return Color.Unspecified

        // create auxiliary variable
        var valueAux = value

        // remove "#" on the beginning
        if (valueAux[0] == '#') valueAux = valueAux.substring(1)

        // add FF -> transparency 100%
        if (valueAux.length == 6) valueAux = "FF$valueAux"

        return Color(valueAux.toLong(radix = 16))
    }
}