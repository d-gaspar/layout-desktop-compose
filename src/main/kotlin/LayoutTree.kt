import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class LayoutTree<String> (key : String, value : String) {
    var key : String = key // must be unique
    var value : MutableState<String> = mutableStateOf(value) // must be unique
    var parent : LayoutTree<String>? = null

    var children : MutableList<LayoutTree<String>> = mutableListOf()

    fun addChild(node : LayoutTree<String>) {
        children.add(node)
        node.parent = this
    }

    fun removeChild(key : String) {
        for (i in 0 until children.size) {
            if (children[i].key == key) {
                children.removeAt(i)
                return
            }
        }

        println("$key NOT FOUND")
    }

    fun find(key : String, node : LayoutTree<String> = this) : LayoutTree<String>? {
        if (node.key == key) return node

        for (i in 0 until node.children.size) {
            val n = find(key, node.children[i])

            if (n != null) return n
        }

        return null
    }

    fun getValue(key : String) : String? {
        return find(key)?.value?.value
    }

    fun setValue(key : String, value : String) {
        find(key)?.value?.value = value
    }

    /** check if any child has "key" */
    fun has(key : String) : Boolean {
        for (child in children) {
            if (child.key == key) return true
        }

        return false
    }

    fun clear(node : LayoutTree<String> = this) {
        node.children.clear()
    }

    fun tree(node : LayoutTree<String> = this, spaces : Int = 0) {

        var sizeText = if (node.children.size > 0) "(${node.children.size})" else ""
        var value = if (node.value.value != "") "=\'${node.value.value}\'" else "=\'\'"

        println("${" ".repeat(spaces * 4)} ${node.key}$value $sizeText ")

        if (node.children.size == 0) return

        for (i in 0 until node.children.size) {
            tree(node.children[i], spaces + 1)
        }
    }
}