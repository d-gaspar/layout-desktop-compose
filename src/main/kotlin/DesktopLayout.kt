
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class DesktopLayout {
    var layoutDir : String = ""
    private var onButtonClick : (() -> Unit)? = null

    private var appendParentKey : MutableState<String> = mutableStateOf("")
    private var appendParentID : MutableState<Int> = mutableStateOf(0)

    private var ID : LayoutTree<String> = LayoutTree("#document", "")

    constructor(layoutDir : String = "res/layout/") {
        this.layoutDir = layoutDir
    }

    /*********************************************************************************************************/

    /** PUBLIC FUNCTIONS */

    fun get(key : String, id : Int = 0) : String? {
        return ID.get(key, id)
    }

    fun set(key : String, value : String, id : Int = 0) {
        ID.set(key, value, id)
    }

    fun append(key : String, value : String, id : Int = 0) {

        if (ID.find(key, id) != null) {
            // set xml id that will be imported
            ID.set(key, value, id)
            ID.find(key, id)!!.appended = true
            ID.find(key, id)!!.appendedQty++

            // set append vars
            appendParentKey.value = key
            appendParentID.value = id
            //appendID = id

            println("APPEND-- |$key|$value|${get(key, id)}|")
        }
    }

    fun tree() {
        ID.tree()
    }

    fun buttonClicked(key : String, action : (() -> Unit)?) {
        if (ID.get(key) == "on") {
            action?.invoke()
        }
    }

    /*********************************************************************************************************/

    private fun addID(key : String, value : String, layoutTreeNode : LayoutTree<String> = ID) {
        // empty id
        if (key.isEmpty()) return

        /** APPEND = TRUE **/
        if (appendParentKey.value.isNotEmpty() && appendParentKey.value == layoutTreeNode.key && appendParentID.value == layoutTreeNode.id) {
            /** first child **/
            if (!layoutTreeNode.has(key)) {
                layoutTreeNode.addChild(LayoutTree(key, value))

            /** another children **/
            } else {
                var id = 0
                for (child in layoutTreeNode.children) {
                    if (child.key == key && child.id >= id) {
                        id = child.id + 1
                    }
                }
                layoutTreeNode.addChild(LayoutTree(key, value, id))
            }

            //appendParentKey.value = ""
            //appendParentID.value = 0

        /** APPEND = FALSE **/
        } else {
            if (!layoutTreeNode.has(key)) {
                layoutTreeNode.addChild(LayoutTree(key, value))
            }
        }
    }

    private fun buttonOnClick(key : String) {
        // empty id (key)
        if (key.isEmpty()) return

        if (ID.find(key) != null) {
            ID.set(key, "on")

            onButtonClick?.invoke()

            ID.set(key, "off")
        }
    }

    /*********************************************************************************************************/

    @Composable
    //fun getLayout(fileName : String, layoutTreeNode : LayoutTree<String> = ID, onButtonClick : (() -> Unit)? = null) { // xml file
    fun getLayout(fileName : String, childID : Int? = null, layoutTreeNode : LayoutTree<String> = ID, onButtonClick : (() -> Unit)? = null) { // xml file
        if (fileName.isEmpty()) return

        if (onButtonClick != null) this.onButtonClick = onButtonClick

        val xmlFile : File = File(layoutDir + fileName)

        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)

        checkXMLChild(doc, layoutTreeNode, childID)
    }

    /*********************************************************************************************************/

    @Composable
    //private fun checkXMLChild(node: Node, layoutTreeNode : LayoutTree<String>, appendID : Int) {
    private fun checkXMLChild(node: Node, layoutTreeNode : LayoutTree<String>, childID : Int?) {
        val childNodes = node.childNodes

        // empty node
        if (childNodes.length == 0) return

        for (i in 0 until childNodes.length) {
            when (childNodes.item(i).nodeName) {
                "box" -> {
                    val (modifier, otherAttributes) = getModifier(
                        attributes = childNodes.item(i).attributes,
                        layoutTreeNode = layoutTreeNode,
                        childID = childID
                    )

                    Box(
                        modifier = modifier
                    ){
                        checkXMLChild(childNodes.item(i), layoutTreeNode, childID)
                    }

                    // add margin
                    addMargin(otherAttributes)
                }
                "column" -> {
                    val (modifier, otherAttributes) = getModifier(
                        attributes = childNodes.item(i).attributes,
                        layoutTreeNode = layoutTreeNode,
                        childID = childID
                    )

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
                    ) {
                        if ("id" in otherAttributes) {
                            otherAttributes["id"] = otherAttributes["id"]!!.replace("$", "")

                            // default layout
                            if (!layoutTreeNode.has(otherAttributes["id"]!!)) {
                                addID(
                                    key = otherAttributes["id"]!!,
                                    value = "",
                                    layoutTreeNode = layoutTreeNode
                                )
                            }

                            // import xml
                            if (layoutTreeNode.get(otherAttributes["id"]!!)!!.isNotEmpty()) {
                                // check if current layout item is a list component
                                if (layoutTreeNode.find(otherAttributes["id"]!!, allGenerations = false)!!.appended) {
                                    var childrenIDs = layoutTreeNode.getChildrenIDs(otherAttributes["id"]!!)

                                    if (appendParentKey.value.isNotEmpty()) {
                                        if (childrenIDs.size == 0) {
                                            childrenIDs.add(0)
                                        } else {
                                            childrenIDs.add(childrenIDs.max()!! + 1)
                                        }
                                    }

                                    //println("AAA: |${childrenIDs.size}|${childrenIDs.max()}|")
                                    for (childID in childrenIDs) {
                                        println("DDD: |$childID|${otherAttributes["id"]!!}|")
                                        getLayout(
                                            fileName = layoutTreeNode.get(otherAttributes["id"]!!)!!,
                                            childID = childID,
                                            layoutTreeNode = layoutTreeNode.find(otherAttributes["id"]!!, allGenerations = false)!!
                                        )
                                        if (appendParentKey.value.isNotEmpty()) {
                                            appendParentKey.value = ""
                                            appendParentID.value = 0
                                        }
                                    }

                                    /*var nChildren = layoutTreeNode.find(otherAttributes["id"]!!, allGenerations = false)!!.appendedQty
                                    for (nChildren in 0 until nChildren) {
                                        getLayout(
                                            fileName = layoutTreeNode.get(otherAttributes["id"]!!)!!,
                                            layoutTreeNode = layoutTreeNode.find(otherAttributes["id"]!!, allGenerations = false)!!
                                        )
                                        if (appendParentKey.value.isNotEmpty()) {
                                            appendParentKey.value = ""
                                            appendParentID.value = 0
                                        }
                                    }*/
                                } else {
                                    getLayout(
                                        fileName = layoutTreeNode.get(otherAttributes["id"]!!)!!,
                                        layoutTreeNode = layoutTreeNode.find(otherAttributes["id"]!!, allGenerations = false)!!
                                    )
                                }
                            } else {
                                checkXMLChild(childNodes.item(i), layoutTreeNode, childID)
                            }
                        } else {
                            checkXMLChild(childNodes.item(i), layoutTreeNode, childID)
                        }
                    }

                    // add margin
                    addMargin(otherAttributes)
                }
                "row" -> {
                    val (modifier, otherAttributes) = getModifier(
                        attributes = childNodes.item(i).attributes,
                        layoutTreeNode = layoutTreeNode,
                        childID = childID
                    )

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
                    ) {
                        checkXMLChild(childNodes.item(i), layoutTreeNode, childID)
                    }

                    // add margin
                    addMargin(otherAttributes)
                }
                "text" -> {
                    val (modifier, otherAttributes) = getModifier(
                        attributes = childNodes.item(i).attributes,
                        layoutTreeNode = layoutTreeNode,
                        childID = childID
                    )

                    val text = childNodes.item(i).textContent.replace(
                        regexText()
                    ){
                        //println("JJJJ: |${it.groupValues[1]}|${it.groupValues[2]}|${layoutTreeNode.get(it.groupValues[1])}|${layoutTreeNode.key}|${layoutTreeNode.id}|")
                        addID(
                            key = it.groupValues[1],
                            value = it.groupValues[2],
                            layoutTreeNode = layoutTreeNode
                        )
                        "" + layoutTreeNode.get(it.groupValues[1], childID ?: 0)
                        //"" + layoutTreeNode.get(it.groupValues[1])
                    }

                    Text(
                        text,
                        modifier = modifier,
                        color = if ("color" in otherAttributes.keys) getColorByHex(otherAttributes["color"]!!) else Color.Unspecified,
                        fontSize = if ("fontSize" in otherAttributes.keys && otherAttributes["fontSize"]!!.isNotEmpty()) otherAttributes["fontSize"]!!.toInt().sp else TextUnit.Unspecified,
                        fontWeight = if ("fontWeight" in otherAttributes.keys && otherAttributes["fontWeight"]!!.isNotEmpty()) FontWeight(otherAttributes["fontWeight"]!!.toInt()) else null
                    )

                    // add margin
                    addMargin(otherAttributes, Pair(10,10))
                }
                "button" -> {
                    // ignore background and color because they aren't a modifier
                    var ignoreAttributes : ArrayList<String> = arrayListOf("background", "color")

                    val (modifier, otherAttributes) = getModifier(
                        attributes = childNodes.item(i).attributes,
                        ignoreAttributes = ignoreAttributes,
                        layoutTreeNode = layoutTreeNode,
                        childID = childID
                    )

                    // add onClick key
                    if ("onClick" in otherAttributes.keys){
                        addID(
                            key = otherAttributes["onClick"]!!.toString().replace("\$", ""),
                            value = "off",
                            layoutTreeNode = layoutTreeNode
                        )
                    }

                    // add text key
                    childNodes.item(i).textContent.replace(
                        regexText()
                    ){
                        // id
                        addID(
                            key = it.groupValues[1],
                            value = it.groupValues[2],
                            layoutTreeNode = layoutTreeNode
                        )

                        // default text
                        if (it.groupValues[1].isNotEmpty()) {
                            "" + layoutTreeNode.get(it.groupValues[1])
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
                            contentColor = if ("color" in otherAttributes.keys) getColorByHex(otherAttributes["color"]!!) else contentColorFor(
                                MaterialTheme.colors.primary)
                        )
                    ) {
                        checkXMLChild(childNodes.item(i), layoutTreeNode, childID)
                    }

                    // add margin
                    addMargin(otherAttributes, Pair(10,10))
                }
            }
        }
    }

    // add margin on layout
    @Composable
    fun addMargin(otherAttributes : HashMap<String, String>, default : Pair<Int, Int> = Pair(0, 0)) {
        if ("margin" in  otherAttributes.keys) {
            var marginAux = otherAttributes["margin"]!!.split(",").map { it.toInt() }

            Spacer(modifier = Modifier.width(marginAux.first().dp).height(marginAux.last().dp))
        } else {
            Spacer(modifier = Modifier.width(default.first.dp).height(default.second.dp))
        }
    }

    /*********************************************************************************************************/

    private fun getModifier(
        attributes : NamedNodeMap,
        ignoreAttributes : ArrayList<String> = arrayListOf(),
        layoutTreeNode : LayoutTree<String>,
        childID : Int?
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
                var nodeKey = ""
                //println("HHHHHHHHH: |$nodeName|$nodeKey|${attributes.item(i).nodeValue}|")
                var nodeValue = attributes.item(i).nodeValue.toString().replace(
                    regexAttribute()
                ){
                    // nodeID
                    addID(
                        key = it.groupValues[1],
                        value = it.groupValues[2],
                        layoutTreeNode = layoutTreeNode
                    )
                    nodeKey = it.groupValues[1]

                    // nodeValue
                    it.groupValues[2]
                }

                // ignore attribute
                if (nodeName in ignoreAttributes) {
                    //if (ID.has(nodeKey)) {
                    if (layoutTreeNode.has(nodeKey)) {
                        //otherAttributes[nodeName] = ID.get(nodeKey)!!
                        otherAttributes[nodeName] = layoutTreeNode.get(nodeKey)!!
                    } else {
                        otherAttributes[nodeName] = nodeValue
                    }

                    continue
                }

                when (nodeName) {
                    "background" -> {
                        //modifier = if (layoutTreeNode.has(nodeKey)) {
                        modifier = if (layoutTreeNode.has(nodeKey)) {
                            modifier.background(
                                //getColorByHex(ID.get(nodeKey)!!)
                                getColorByHex(layoutTreeNode.get(nodeKey)!!)
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