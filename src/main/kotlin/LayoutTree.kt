import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class LayoutTree<String> (key : String, value : String, id : Int = 0, appended : Boolean = false) {
    /**
     * combination "key"+"id" must be unique
     * id is used when there are multiple nodes with the same "key" => (key, id) = {(keyA, 0), (keyA, 1), (keyB, 0) ... }
     * */
    var key : String = key
    var id : Int = id // used when there are multiple nodes with the same "key"
    var value : MutableState<String> = mutableStateOf(value)
    var parent : LayoutTree<String>? = null

    var appended : Boolean = appended
    var appendedQty : Int = 0

    var children : MutableList<LayoutTree<String>> = mutableListOf()

    fun addChild(node : LayoutTree<String>) { // add return Boolean if a node is successful added
        /** check if node.key and node.id already exist because no two values can be the same */

        children.add(node)
        node.parent = this
    }

    fun removeChild(key : String, id : Int = 0) { // test pending
        for (i in 0 until children.size) {
            if (children[i].key == key) {
                children.removeAt(i)
                return
            }
        }

        println("$key NOT FOUND")
    }

    fun find(key : String, id : Int = 0, allGenerations : Boolean = true, node : LayoutTree<String> = this) : LayoutTree<String>? {
        if (allGenerations) {
            /** all generations */
            if (node.key == key && node.id == id) return node

            for (i in 0 until node.children.size) {
                val n = find(key, id, allGenerations, node.children[i])

                if (n != null) return n // n -> node
            }
        } else {
            /** only first generation */
            for (child in children) {
                if (child.key == key && child.id == id) return child
            }
        }

        return null
    }

    fun get(key : String, id : Int = 0) : String? {
        return find(key, id)?.value?.value
    }

    fun getIDs(key : String, parentKey : String) : List<Int> {


        return listOf()
    }

    fun set(key : String, value : String, id : Int = 0) {
        find(key, id)?.value?.value = value
    }

    /** check if any child has "key" */
    fun has(key : String, id : Int = 0) : Boolean {
        for (child in children) {
            if (child.key == key && child.id == id) return true
        }

        return false
    }

    fun clear(node : LayoutTree<String> = this) {
        node.children.clear()
    }

    fun tree(node : LayoutTree<String> = this, spaces : Int = 0) {

        var sizeText = if (node.children.size > 0) "(${node.children.size})" else ""
        var idText = "${node.id}"
        var value = if (node.value.value != "") "\'${node.value.value}\'" else "\'\'"

        println("${" ".repeat(spaces * 4)} ${node.key}:$idText=$value $sizeText ")

        if (node.children.size == 0) return

        for (i in 0 until node.children.size) {
            tree(node.children[i], spaces + 1)
        }
    }
}