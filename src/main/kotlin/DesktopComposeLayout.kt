
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
import androidx.compose.runtime.*
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
    /**
     * [id, [index, value]]
     * "0" index is fixed, can't be removed
     * */
    var ID = HashMap<String, HashMap<Int, MutableState<String>>>()
    var itemAppendedParent : MutableState<String> = mutableStateOf("")
    var itemAppendPos : Int = -1
    var itemAppendedAction : (() -> Unit)? = null
    var itemRemoved : MutableState<Boolean> = mutableStateOf(false)

    constructor(layoutDir : String = "res/layout/") {
        this.layoutDir = layoutDir
    }

    /*********************************************************************************************************/

    /** PUBLIC FUNCTIONS */

    fun getID(key : String, pos : Int = 0) : String {
        return if (key in ID.keys) {
            ID[key]!![pos]?.value.toString()
        } else {
            ""
        }
    }

    fun setID(key : String, value : String, pos : Int = 0) {
        if (key in ID.keys) {
            if (pos !in ID[key]!!.keys) {
                ID[key]!![pos] = mutableStateOf(value)
            } else {
                ID[key]!![pos]?.value = value
            }
        }
    }

    fun getIDList(key : String) : List<Int> { //: MutableSet<Int> {
        return ID[key]!!.keys.toList()
    }

    // append new item on ID[key]
    fun appendID(key : String, value : String, action : (() -> Unit)? = null) {
        if (key in ID.keys) {
            var maxIDIndex = getIDList(key).maxOrNull()

            if (maxIDIndex != null) {
                /** "0" index is fixed, can't be removed */
                if (ID[key]!!.size == 1 && ID[key]!![0]!!.value.isEmpty()) {
                    itemAppendPos = 0
                    ID[key]!![itemAppendPos]!!.value = value
                    println("AHHHHHHHHHHHHHHHHHH1: $key | $value | ${ID[key]?.keys}")
                } else {
                    itemAppendPos = maxIDIndex + 1
                    ID[key]!![itemAppendPos] = mutableStateOf(value)
                    println("AHHHHHHHHHHHHHHHHHH2: $key | $value | ${ID[key]?.keys}")
                }

                itemAppendedParent.value = key
                itemAppendedAction = action
            }
        }
    }

    fun removeIDItem(key : String, pos : Int) {
        if (key in ID.keys) {
            /** "0" index is fixed, can't be removed */
            if (pos == 0) {
                ID[key]!![0]!!.value = ""
            } else {
                ID[key]!!.remove(pos)
            }

            itemRemoved.value = true
        }
    }

    fun clearIDItems(key : String) {
        for (i in getIDList(key)) {
            if (i == 0) {
                ID[key]!![0]!!.value = ""
            } else {
                ID[key]!!.remove(i)
            }
        }

        itemRemoved.value = true
    }

    fun buttonClicked(buttonID : String, action : (() -> Unit)?) {
        if (ID[buttonID]?.get(0)?.value == "on") {
            action?.invoke()
        }
    }

    /*********************************************************************************************************/

    private fun addID(key : String, value : String, appendPos : Int) {
        // empty id
        if (key.isEmpty()) return

        if (key !in ID.keys) {
            ID[key] = hashMapOf()

            if (appendPos == -1)
                ID[key]!![0] = mutableStateOf(value)
            else
                ID[key]!![appendPos] = mutableStateOf(value)

        /** item appended */
        } else if (appendPos != -1 && appendPos == itemAppendPos){
            println("NNNNNNNNNNNNNNNNNNNNNNNNNNN: ${itemAppendedParent.value} | $key | $itemAppendPos | $appendPos | ${ID.keys}")
            ID[key]!![itemAppendPos] = mutableStateOf(value)

            //itemAppendedParent.value = ""

            //itemAppendedAction?.invoke()
            //itemAppendedAction = null
        }
    }

    private fun buttonOnClick(key : String) {
        // empty id (key)
        if (key.isEmpty()) return

        if (key in ID.keys) {
            setID(key, "on")

            onButtonClick?.invoke()

            setID(key, "off")
        }
    }

    /*********************************************************************************************************/

    @Composable
    fun getLayout(fileName : String, appendPos : Int = -1, onButtonClick : (() -> Unit)? = null) { // xml file
        if (fileName.isEmpty()) return

        if (onButtonClick != null) {
            this.onButtonClick = onButtonClick
        }

        val xmlFile : File = File(layoutDir + fileName)

        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)

        /** first element - must be: box, column or row  */
        //val firstNode = doc.documentElement

        /** recursive function to search all xml childs */
        checkXMLChilds(doc, appendPos)
    }

    /*********************************************************************************************************/

    @Composable
    private fun checkXMLChilds(node: Node, appendPos : Int) {
        val childNodes = node.childNodes

        // empty node
        if (childNodes.length == 0) return

        for (i in 0 until childNodes.length) {
            when (childNodes.item(i).nodeName) {
                "box"       -> {
                    val (modifier, otherAttributes) = getModifier(childNodes.item(i).attributes, appendPos = appendPos)

                    Box(
                        modifier = modifier
                    ){
                        checkXMLChilds(childNodes.item(i), appendPos)
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
                    val (modifier, otherAttributes) = getModifier(childNodes.item(i).attributes, appendPos = appendPos)

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
                        // otherAttributes["id"] == key
                        if ("id" in otherAttributes) {
                            otherAttributes["id"] = otherAttributes["id"]!!.replace("$", "")

                            // default layout
                            if (otherAttributes["id"] !in ID.keys) {
                                // create id on hashmap IDs
                                addID(otherAttributes["id"]!!, "", appendPos)
                            }

                            // import xml
                            if (getIDList(otherAttributes["id"]!!).size > 1 || getID(otherAttributes["id"]!!).isNotEmpty()) {

                                //println("OOOOOOOOOOOOOOOOOOOOO1: ${otherAttributes["id"]} | ${itemAppendedParent.value}")
                                for (pos in getIDList(otherAttributes["id"]!!)) {
                                    //println("VV: $pos")
                                    getLayout(
                                        fileName = getID(otherAttributes["id"]!!, pos),
                                        appendPos = pos
                                    )
                                    /** reset itemAppendPos */
                                    //itemAppendPos = -1
                                }

                                /*if (otherAttributes["id"] == itemAppendedParent.value) {
                                    //println("OOOOOOOOOOOOOOOOOOOOO2: ${otherAttributes["id"]} | ${itemAppendedParent.value}")
                                    itemAppendedParent.value = ""

                                    itemAppendedAction?.invoke()
                                    itemAppendedAction = null
                                } else {

                                }*/
                                /** item appended or removed */
                                //println("OOOOOOOOOOOOOOOOOOOOO3: ${otherAttributes["id"]} | ${itemAppendedParent.value}")
                                //if (itemAppended.value.isNotEmpty()) itemAppended.value = "" // qqq
                                //if (itemRemoved.value) itemRemoved.value = false // qqq

                            } else {
                                checkXMLChilds(childNodes.item(i), appendPos)
                            }
                        } else {
                            checkXMLChilds(childNodes.item(i), appendPos)
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
                    val (modifier, otherAttributes) = getModifier(childNodes.item(i).attributes, appendPos = appendPos)

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
                        checkXMLChilds(childNodes.item(i), appendPos = appendPos)
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
                    val (modifier, otherAttributes) = getModifier(childNodes.item(i).attributes, appendPos = appendPos)

                    // text id
                    val text = childNodes.item(i).textContent.replace(
                        regexText()
                    ){
                        addID(it.groupValues[1], "", appendPos)
                        "" + getID(it.groupValues[1])
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

                    val (modifier, otherAttributes) = getModifier(childNodes.item(i).attributes, ignoreAttributes, appendPos)

                    // add id
                    if ("onClick" in otherAttributes.keys){
                        addID(otherAttributes["onClick"]!!.toString().replace("\$", ""), "off", appendPos)
                    }

                    // text id
                    /*val text = */childNodes.item(i).textContent.replace(
                        regexText()
                    ){
                        // id
                        addID(it.groupValues[1], it.groupValues[2], appendPos)

                        // default text
                        if (it.groupValues[1].isNotEmpty()) {
                            "" + getID(it.groupValues[1])
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
                        checkXMLChilds(childNodes.item(i), appendPos = appendPos)
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
        ignoreAttributes : ArrayList<String> = arrayListOf(),
        appendPos : Int
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
                    addID(it.groupValues[1], it.groupValues[2], appendPos)
                    nodeID = it.groupValues[1]

                    // nodeValue
                    it.groupValues[2]
                }

                // ignore attribute
                if (nodeName in ignoreAttributes) {
                    //if (nodeID in ID.keys) {
                    if (nodeID in ID.keys) {
                        otherAttributes[nodeName] = getID(nodeID)
                    } else {
                        otherAttributes[nodeName] = nodeValue
                    }
                    continue
                }

                when (nodeName) {
                    "background" -> {
                        modifier = if (nodeID in ID.keys) {
                            modifier.background(
                                getColorByHex(getID(nodeID))
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