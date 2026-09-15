package it.pidaydev.story03

import it.pidaydev.common.Direction
import it.pidaydev.common.quest

private val quester = STORY quest 3 withRowParser { line ->
    val (id, plug, leftSocket, rightSocket, data) =
        line.split(", ").map { it.substringAfter("=") }
    Node(id.toInt(), plug, leftSocket, rightSocket, data)
}

private data class Node(
    val id: Int,
    val plug: String,
    val leftSocket: String,
    val rightSocket: String,
    val data: String
) {
    var leftChild: Node? = null
    var rightChild: Node? = null

    fun append(
        child: Node,
        canHostLeft: Predicate,
        canHostRight: Predicate,
    ) {
        val candidates = clockwiseOrder()
        for (node in candidates) {
            when {

                node.canHostLeft(child) -> {
                    node.leftChild = child
                    return
                }

                node.canHostRight(child) -> {
                    node.rightChild = child
                    return
                }
            }
        }
    }

    fun appendOrReplace(
        child: Node,
        canHostLeft: Predicate,
        canHostRight: Predicate,
        candidatesOrder: List<Pair<Node, Direction>> = clockwisePortOrder()
    ) {
        for ((node, side) in candidatesOrder) {
            if (side == Direction.LEFT && node.canHostLeft(child)) {
                val displaced = node.leftChild
                node.leftChild = child
                if (displaced != null) {
                    val newOrder = clockwisePortOrder()
                    val replacedIndex = newOrder.indexOf(node to Direction.RIGHT)
                    val searchOrder = newOrder.drop(replacedIndex) + newOrder.take(replacedIndex)
                    appendOrReplace(displaced, canHostLeft, canHostRight, searchOrder)
                }
                return
            } else if (side == Direction.RIGHT && node.canHostRight(child)) {
                val displaced = node.rightChild
                node.rightChild = child
                if (displaced != null) {
                    val newOrder = clockwisePortOrder()
                    val replacedIndex = newOrder.indexOf(child to Direction.RIGHT)
                    val searchOrder = newOrder.drop(replacedIndex + 1) + newOrder.take(replacedIndex + 1)
                    appendOrReplace(displaced, canHostLeft, canHostRight, searchOrder)
                }
                return
            }
        }
    }

    fun clockwiseOrder(): List<Node> {
        val a = leftChild?.clockwiseOrder() ?: emptyList()
        val b = this
        val c = rightChild?.clockwiseOrder() ?: emptyList()
        return a + b + c
    }

    private fun clockwisePortOrder(): List<Pair<Node, Direction>> {
        val leftSubtree = leftChild?.clockwisePortOrder() ?: emptyList()
        val rightSubtree = rightChild?.clockwisePortOrder() ?: emptyList()
        return listOf(this to Direction.LEFT) + leftSubtree + listOf(this to Direction.RIGHT) + rightSubtree
    }

    override fun toString() = "[$id]" +
            (leftChild?.id?.let { "/L:$it" } ?: "") +
            (rightChild?.id?.let { "/R:$it" } ?: "")

    fun dfsPrint(depth: Int = 0) {
        println("  ".repeat(depth) + this)
        leftChild?.dfsPrint(depth + 1)
        rightChild?.dfsPrint(depth + 1)
    }

}

private typealias Predicate = Node.(Node) -> Boolean

fun main() {

    fun buildTree(
        nodes: List<Node>,
        canHostLeft: Predicate,
        canHostRight: Predicate,
        insertFn: Node.(Node, Predicate, Predicate) -> Unit = Node::append
    ): Node {
        val tree = nodes.first()
        nodes.drop(1).forEach { child ->
            tree.insertFn(child, canHostLeft, canHostRight)
        }
        return tree
    }

    fun checksum(tree: Node): Int =
        tree.clockwiseOrder().mapIndexed { index, node -> node.id * (index + 1) }.sum()

    fun part1(): Int {
        val nodes = quester.read(1)

        fun Node.canHostLeft(child: Node) = leftChild == null && child.plug == leftSocket
        fun Node.canHostRight(child: Node) = rightChild == null && child.plug == rightSocket

        val tree = buildTree(nodes, Node::canHostLeft, Node::canHostRight)
        return checksum(tree)
    }

    fun part2(): Int {
        val nodes = quester.read(2)

        fun String.canBond(other: String) = split(" ").zip(other.split(" ")).any { (a, b) -> a == b }

        fun Node.canHostLeft(child: Node) = leftChild == null && child.plug.canBond(leftSocket)
        fun Node.canHostRight(child: Node) = rightChild == null && child.plug.canBond(rightSocket)

        val tree = buildTree(nodes, Node::canHostLeft, Node::canHostRight)
        return checksum(tree)
    }

    fun part3(): Int {
        val nodes = quester.read(3)

        fun String.bondStrength(other: String) = split(" ").zip(other.split(" ")).count { (a, b) -> a == b }

        fun Node.canHostLeft(child: Node) =
            child.plug.bondStrength(leftSocket) > (leftChild?.plug?.bondStrength(leftSocket) ?: 0)

        fun Node.canHostRight(child: Node) =
            child.plug.bondStrength(rightSocket) > (rightChild?.plug?.bondStrength(rightSocket) ?: 0)

        val tree = buildTree(nodes, Node::canHostLeft, Node::canHostRight, Node::appendOrReplace)
        return checksum(tree)
    }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
