package it.pidaydev.story01

import it.pidaydev.common.quest

private val quester = STORY quest 2 withRowParser { row ->
    when (InstructionType.valueOf(row.substringBefore(" "))) {
        InstructionType.ADD -> {
            // ADD id=1 left=[10,A] right=[30,H]
            val (_, id, left, right) = row.split(" ")
            val idNumber = id.substringAfter("=").toInt()
            val (leftRank, leftSymbol) = left.substringAfter("[").substringBefore("]").split(",")
            val (rightRank, rightSymbol) = right.substringAfter("[").substringBefore("]").split(",")
            TreeAdd(
                id = idNumber,
                left = TreeNode(idNumber, leftRank.toInt(), leftSymbol.first()),
                right = TreeNode(idNumber, rightRank.toInt(), rightSymbol.first()),
            )
        }

        InstructionType.SWAP -> {
            TreeSwap(id = row.substringAfter(" ").toInt())
        }
    }
}

private data class TreeNode(
    val id: Int,
    var rank: Int,
    var symbol: Char,
    var leftChild: TreeNode? = null,
    var rightChild: TreeNode? = null,
) {
    override fun toString() = "{$id}[$rank,$symbol]->L=${leftChild?.id ?: "-"},R=${rightChild?.id ?: "-"}"

    fun addChild(child: TreeNode) {
        if (child.rank < rank) {
            leftChild?.addChild(child) ?: run { leftChild = child }
        } else {
            rightChild?.addChild(child) ?: run { rightChild = child }
        }
    }

    fun findNode(id: Int): TreeNode? {
        if (this.id == id) return this
        return leftChild?.findNode(id) ?: rightChild?.findNode(id)
    }

    fun findAllNodes(id: Int): List<TreeNode> {
        val list = mutableListOf<TreeNode>()
        if (this.id == id) list += this
        list += leftChild?.findAllNodes(id) ?: emptyList()
        list += rightChild?.findAllNodes(id) ?: emptyList()
        return list
    }
}

private enum class InstructionType { ADD, SWAP }

private sealed class TreeInstruction

private data class TreeAdd(val id: Int, val left: TreeNode, val right: TreeNode) : TreeInstruction() {
    override fun toString() = "ADD{$id}: left=$left, right=$right"
}

private data class TreeSwap(val id: Int) : TreeInstruction() {
    override fun toString() = "SWAP{$id}"
}

private class DoubleTree(val leftRoot: TreeNode, val rightRoot: TreeNode) {
    private constructor(instruction: TreeAdd) : this(instruction.left, instruction.right)

    private fun process(instruction: TreeInstruction, swapFullBranch: Boolean = false) {
        when (instruction) {
            is TreeAdd -> add(instruction)
            is TreeSwap -> if (swapFullBranch) swapBranch(instruction) else swap(instruction)
        }
    }

    private fun add(instruction: TreeAdd) {
        leftRoot.addChild(instruction.left)
        rightRoot.addChild(instruction.right)
    }

    private fun swap(instruction: TreeSwap) {
        val leftNode = leftRoot.findNode(instruction.id) ?: return
        val rightNode = rightRoot.findNode(instruction.id) ?: return
        val temp = leftNode.copy()
        leftNode.rank = rightNode.rank
        leftNode.symbol = rightNode.symbol
        rightNode.rank = temp.rank
        rightNode.symbol = temp.symbol
    }

    private fun swapBranch(instruction: TreeSwap) {
        val nodes = leftRoot.findAllNodes(instruction.id) + rightRoot.findAllNodes(instruction.id)
        if (nodes.size != 2) return // Only swap if there are exactly two nodes
        val (leftNode, rightNode) = nodes
        val temp = leftNode.copy()
        leftNode.rank = rightNode.rank
        leftNode.symbol = rightNode.symbol
        leftNode.leftChild = rightNode.leftChild
        leftNode.rightChild = rightNode.rightChild
        rightNode.rank = temp.rank
        rightNode.symbol = temp.symbol
        rightNode.leftChild = temp.leftChild
        rightNode.rightChild = temp.rightChild
    }

    private fun bfs(root: TreeNode): Map<Int, List<TreeNode>> {
        val result = mutableMapOf<Int, MutableList<TreeNode>>()
        val queue = ArrayDeque<Pair<TreeNode, Int>>()
        queue.add(root to 1)
        while (queue.isNotEmpty()) {
            val (current, level) = queue.removeFirst()
            result.getOrPut(level) { mutableListOf() } += current
            current.leftChild?.let { queue.add(it to level + 1) }
            current.rightChild?.let { queue.add(it to level + 1) }
        }
        return result
    }

    fun findMessage(): String {
        val left = bfs(leftRoot)
        val right = bfs(rightRoot)

        fun Map<Int, List<TreeNode>>.extractMessage(): String =
            values.maxBy { it.size }.joinToString("") { it.symbol.toString() }

        return left.extractMessage() + right.extractMessage()
    }

    override fun toString() = "DoubleTree(leftRoot=$leftRoot, rightRoot=$rightRoot)"

    companion object {
        fun fromInstructions(instructions: List<TreeInstruction>, swapFullBranch: Boolean = false): DoubleTree {
            val tree = DoubleTree(instructions.first() as TreeAdd)
            instructions.drop(1).forEach { tree.process(it, swapFullBranch) }
            return tree
        }
    }
}

fun main() {

    fun part1(): String {
        val instructions = quester.read(1)
        val tree = DoubleTree.fromInstructions(instructions)
        return tree.findMessage()
    }

    fun part2(): String {
        val instructions = quester.read(2)
        val tree = DoubleTree.fromInstructions(instructions)
        return tree.findMessage()
    }

    fun part3(): String {
        val instructions = quester.read(3)
        val tree = DoubleTree.fromInstructions(instructions, swapFullBranch = true)
        return tree.findMessage()
    }

    quester.printAndVerify(::part1, ::part2, ::part3)
}
