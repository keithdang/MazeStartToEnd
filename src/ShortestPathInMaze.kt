object ShortestPathInMaze {
    fun start(start: Pair<Int, Int>, end: Pair<Int, Int>, arr: Array<Pair<Int, Int>>, ms:Int) {
        var matrix = Array(ms) { BooleanArray(ms) }
        fillArray(matrix, arr)
        printMatrix(matrix, start, end)

        var tree = TreeNode(start)

        checkIfDirectionsAvailable(matrix, start, end, tree)
        println("\nPossible Paths from S to E")
        val hello = arrayOfNulls<Pair<Int, Int>>(100)
        var shortestPath = mutableListOf<Pair<Int, Int>?>()
        printPathRecur(tree, hello, 0, shortestPath)
        println("Shortest Path")
        println("Min steps: ${minDepth(tree)}")
        shortestPath.forEach {
            printPair(it!!)
        }
    }
    fun printStartingMap(start: Pair<Int, Int>, end: Pair<Int, Int>, arr: Array<Pair<Int, Int>>,ms:Int){
        var matrix = Array(ms) { BooleanArray(ms) }
        fillArray(matrix, arr)
        printMatrix(matrix, start, end)
    }
    private fun matchingCoord(c: Pair<Int, Int>, e: Pair<Int, Int>): Boolean =
        c.first == e.first && c.second == e.second

    private fun checkSubPath(mx: Array<BooleanArray>, c: Pair<Int, Int>, end: Pair<Int, Int>, tree: TreeNode): Boolean {
        return if (matchingCoord(c, end)) {
            tree.addChild(TreeNode(c, true))
            true
        } else {
            tree.addChild(TreeNode(c))
            if (checkIfDirectionsAvailable(mx, c, end, tree.children.last())) {
                true
            } else {
                tree.children.removeAt(tree.children.size - 1)
                false
            }
        }
    }

    private fun checkIfDirectionsAvailable(
        mx: Array<BooleanArray>,
        c: Pair<Int, Int>,
        end: Pair<Int, Int>,
        tree: TreeNode
    ): Boolean {
        mx[c.first][c.second] = false
        var pathIsValid = false
        //left
        if (c.second > 0 && mx[c.first][c.second - 1]) {
            var left = Pair(c.first, c.second - 1)
            if (checkSubPath(mx, left, end, tree)) {
                pathIsValid = true
            }
        }
        //right
        if (c.second < (mx.size - 1) && mx[c.first][c.second + 1]) {
            var right = Pair(c.first, c.second + 1)
            if (checkSubPath(mx, right, end, tree)) {
                pathIsValid = true
            }
        }
        //down
        if (c.first < (mx.size - 1) && mx[c.first + 1][c.second]) {
            var down = Pair(c.first + 1, c.second)
            if (checkSubPath(mx, down, end, tree)) {
                pathIsValid = true
            }
        }
        //up
        if (c.first > 0 && mx[c.first - 1][c.second]) {
            var up = Pair(c.first - 1, c.second)
            if (checkSubPath(mx, up, end, tree)) {
                pathIsValid = true
            }
        }
        mx[c.first][c.second] = true
        return pathIsValid
    }

    private fun printMatrix(matrix: Array<BooleanArray>, start: Pair<Int, Int>, end: Pair<Int, Int>) {
        for (i in 0 until matrix.size) {
            for (j in 0 until matrix.size) {
                when {
                    i == start.first && j == start.second -> print("S,")
                    i == end.first && j == end.second -> print("E,")
                    matrix[i][j] -> print("T,")
                    else -> print("F,")
                }
            }
            println()
        }
    }

    private fun fillArray(matrix: Array<BooleanArray>, arr: Array<Pair<Int, Int>>) {
        for (i in 0 until matrix.size) {
            for (j in 0 until matrix.size) {
                matrix[i][j] = true
            }
        }
        arr.forEach {
            matrix[it.first][it.second] = false
        }
    }

    private fun minDepth(tree: TreeNode): Int {
        if (tree.children.isEmpty()) return 1

        var arr = mutableListOf<Int>()
        tree.children.forEach {
            arr.add(minDepth(it))
        }
        return arr.min()!! + 1
    }

    private fun printPathRecur(
        node: TreeNode,
        path: Array<Pair<Int, Int>?>,
        _pathLen: Int,
        shortestPath: MutableList<Pair<Int, Int>?>
    ) {
        path[_pathLen] = node.data
        var pathLen = _pathLen + 1
        if (node.children.isEmpty()) {
            printArray(path, pathLen, shortestPath)
        } else {
            node.children.forEach {
                printPathRecur(it, path, pathLen, shortestPath)
            }
        }
    }

    private fun printArray(arr: Array<Pair<Int, Int>?>, len: Int, shortestPath: MutableList<Pair<Int, Int>?>) {
        for (i in 0 until len) {
            print("(${arr[i]?.first},${arr[i]?.second}),")
        }
        println()
        if (shortestPath.isEmpty() || len < shortestPath.size) {
            shortestPath.clear()
            for (i in 0 until len) {
                shortestPath.add(arr[i])
            }
        }
    }

    private fun minPath(tree: TreeNode, path: MutableList<TreeNode>): Int {

        if (tree.children.isEmpty()) return 1

        var minNode = Pair(0, minPath(tree.children[0], path))
//        var arr= mutableListOf<Int>()
        for (i in 1 until tree.children.size - 1) {
            var num = minPath(tree.children[i], path)
            if (minNode.second > num) {
                minNode = Pair(i, num)
            }
        }

        printPair(tree.children[minNode.first].data)
        return minNode.second + 1
    }

    private fun printPair(p: Pair<Int, Int>) {
        print("(${p.first},${p.second}),")
    }

    class TreeNode(var data: Pair<Int, Int>, var isEnd: Boolean = false) {
        var parent: TreeNode? = null
        var children: MutableList<TreeNode> = mutableListOf()
        fun addChild(node: TreeNode) {
            children.add(node)
            node.parent = this
        }
    }
}