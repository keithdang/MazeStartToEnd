import kotlin.random.Random

object LocateAndGo {
    private var count = 0
    private var moves = 0

    enum class Directions {
        NORTH, WEST, EAST, SOUTH
    }

    enum class Move {
        FORWARD, TURNLEFT, TURNRIGHT
    }

    class Coord(var c: Pair<Int, Int>? = null, var dir: Directions) {
        var infront = false
    }

    class Space(var c: Pair<Int, Int>? = null) {
        var isNotWall = true
    }

    fun start() {
        var matLen = 6
        var start = Pair(5, 3)
        singleCase(matLen, Coord(start, Directions.NORTH))
//        multipleCases(Coord(Pair(0,2),Directions.NORTH))

    }

    private fun singleCase(matLen: Int, c: Coord) {
        var walls = wallTemplate(matLen)
        var startPath = mutableListOf(c)
        var end = Pair(0, 0)
        ShortestPathInMaze.printStartingMap(startPath[0].c!!, end, walls, matLen)
        determineLocation(matLen, walls, startPath)
        println("Iterations: $count")
        println("Moves: $moves")
        ShortestPathInMaze.start(startPath.last().c!!, end, walls, matLen)
    }

    private fun multipleCases(c: Coord) {
        var it = 500
        var total = 0

        for (ms in 3..8) {
            var walls = wallTemplate(ms)
            var startPath = mutableListOf(c)
            for (i in 0 until it) {
                determineLocation(ms, walls, startPath)
                total += count
                count = 0
            }
            println("Matrix size: $ms av:${(total / it)}")
            total = 0
        }
    }

    private fun determineLocation(ms: Int, walls: Array<Pair<Int, Int>>, startPath: MutableList<Coord>) {
        var mapOfCoordList = mutableMapOf<String, MutableList<Coord>>()
        fillCoordListMap(mapOfCoordList, ms)
        println("All Possiblities: ${mapOfCoordList.count()}")
        printMapCoord(mapOfCoordList)
        var mxSpace = Array(ms) { i -> Array(ms) { j -> Space(Pair(i, j)) } }

        removeWallsFromMap(walls, mxSpace, mapOfCoordList)
        println("Removing walls from possiblities: ${mapOfCoordList.count()}")
        printMapCoord(mapOfCoordList)
        while (mapOfCoordList.count() > 1) {
            compareAndMoveCoord(startPath, mapOfCoordList, mxSpace)
            moves++
        }
        if (mapOfCoordList.count() != 1) {
            println("---FAILURE---")
        }
    }

    private fun compareAndMoveCoord(
        startPath: MutableList<Coord>,
        mapOfCoordList: MutableMap<String, MutableList<Coord>>,
        mxSpace: Array<Array<Space>>
    ) {
        checkInfrontIsWall(startPath.last(), mxSpace)
        scanAndFilterMap(mapOfCoordList, mxSpace, startPath.last().infront)
        var nxMov = nextMove(startPath.last())
        moveCoord(nxMov, startPath)
        moveAllPossibilities(nxMov, mapOfCoordList)

        print("Robot Direction: ${startPath.last().dir}\t\t")
        print("Wall infront: ${!startPath.last().infront}\t\t")
        print("Possibilities: ${mapOfCoordList.count()}\t\t")
        println("Moving: $nxMov")
        printMapCoord(mapOfCoordList)
    }

    private fun moveAllPossibilities(move: Move, mapOfCoordList: MutableMap<String, MutableList<Coord>>) {
        mapOfCoordList.forEach { (key, value) ->
            count++
            moveCoord(move, value)
        }
    }

    private fun moveCoord(move: Move, cList: MutableList<Coord>) {
        var c = cList.last()
        when (move) {
            Move.FORWARD -> {
                //need to account for which direction it's facing
                when (c.dir) {
                    Directions.NORTH -> cList.add(Coord(Pair(c.c!!.first - 1, c.c!!.second), Directions.NORTH))
                    Directions.EAST -> cList.add(Coord(Pair(c.c!!.first, c.c!!.second + 1), Directions.EAST))
                    Directions.WEST -> cList.add(Coord(Pair(c.c!!.first, c.c!!.second - 1), Directions.WEST))
                    Directions.SOUTH -> cList.add(Coord(Pair(c.c!!.first + 1, c.c!!.second), Directions.SOUTH))
                }
            }
            Move.TURNLEFT -> {
                //need to account for which direction it's facing
                when (c.dir) {
                    Directions.NORTH -> cList.add(Coord(Pair(c.c!!.first, c.c!!.second), Directions.WEST))
                    Directions.EAST -> cList.add(Coord(Pair(c.c!!.first, c.c!!.second), Directions.NORTH))
                    Directions.WEST -> cList.add(Coord(Pair(c.c!!.first, c.c!!.second), Directions.SOUTH))
                    Directions.SOUTH -> cList.add(Coord(Pair(c.c!!.first, c.c!!.second), Directions.EAST))
                }
            }
            Move.TURNRIGHT -> {
                //need to account for which direction it's facing
                when (c.dir) {
                    Directions.NORTH -> cList.add(Coord(Pair(c.c!!.first, c.c!!.second), Directions.EAST))
                    Directions.EAST -> cList.add(Coord(Pair(c.c!!.first, c.c!!.second), Directions.SOUTH))
                    Directions.WEST -> cList.add(Coord(Pair(c.c!!.first, c.c!!.second), Directions.NORTH))
                    Directions.SOUTH -> cList.add(Coord(Pair(c.c!!.first, c.c!!.second), Directions.WEST))
                }
            }
        }
    }

    private fun nextMove(c: Coord): Move {
        //I would do it as equally weights for left,right & forward but through testing its been shown that
        //going forward whenever possibly yields the best results as 2/3 of the time you remain in the same coordinate
        //moving forward allows for new terrain to scan and less likely to loop
        //on average, this new implementation can reduces the amount of iterations by 25%!
        return when (c.infront) {
            true -> Move.FORWARD
            false -> when (Random.nextInt(0, 2)) {
                0 -> Move.TURNLEFT
                else -> Move.TURNRIGHT
            }
        }
        //Previous Method of equal distribution
//        var limit=when(c.infront){
//            true->3
//            false->2
//        }
//        return when(Random.nextInt(0,limit)){
//            0->Move.TURNLEFT
//            1->Move.TURNRIGHT
//            else->Move.FORWARD
//        }
    }

    private fun scanAndFilterMap(
        mapOfCoordList: MutableMap<String, MutableList<Coord>>,
        mxSpace: Array<Array<Space>>,
        isInfrontFree: Boolean
    ) {
        var keyList = mutableListOf<String>()
        mapOfCoordList.forEach { (key, value) ->
            count++
            var c = value.last()
            checkInfrontIsWall(c, mxSpace)
            if (c.infront != isInfrontFree) {
                keyList.add(key)
            }
        }
        keyList.forEach { mapOfCoordList.remove(it) }
    }

    private fun checkInfrontIsWall(c: Coord, mx: Array<Array<Space>>) {
        c.infront = when (c.dir) {
            Directions.NORTH -> (c.c!!.first > 0 && mx[c.c!!.first - 1][c.c!!.second].isNotWall)
            Directions.EAST -> (c.c!!.second < (mx.size - 1) && mx[c.c!!.first][c.c!!.second + 1].isNotWall)
            Directions.SOUTH -> (c.c!!.first < (mx.size - 1) && mx[c.c!!.first + 1][c.c!!.second].isNotWall)
            Directions.WEST -> (c.c!!.second > 0 && mx[c.c!!.first][c.c!!.second - 1].isNotWall)
        }
    }

    private fun num2Dir(n: Int): Directions {
        return when (n) {
            0 -> Directions.NORTH
            1 -> Directions.EAST
            2 -> Directions.SOUTH
            else -> Directions.WEST
        }
    }

    private fun printCoord(c: Coord) {
        print("[(${c.c!!.first},${c.c!!.second}),${c.dir}]")
    }

    private fun fillCoordListMap(cListMap: MutableMap<String, MutableList<Coord>>, ms: Int) {
        for (i in 0 until ms) {
            for (j in 0 until ms) {
                for (k in 0 until 4) {
                    var muDirList = mutableListOf(Coord(Pair(i, j), num2Dir(k)))
                    cListMap["$i,$j,$k"] = muDirList
                }
            }
        }
    }

    private fun removeWallsFromMap(
        arr: Array<Pair<Int, Int>>,
        mxSpace: Array<Array<Space>>,
        mapOfCoordList: MutableMap<String, MutableList<Coord>>
    ) {
        arr.forEach {
            mxSpace[it.first][it.second].isNotWall = false
            for (k in 0 until 4) {
                mapOfCoordList.remove("${it.first},${it.second},$k")
            }
        }
    }

    private fun printMapCoord(mapOfCoordList: MutableMap<String, MutableList<Coord>>) {
        var firstIt = true
        var savedPair = Pair(0, 0)
        var newLine = false
        mapOfCoordList.forEach { (key, value) ->
            if (savedPair.first != value[0].c!!.first) {
                savedPair = Pair(value[0].c!!.first, value[0].c!!.second)
                newLine = true
            }
            if (newLine) {
                println()
            } else {
                if (!firstIt) {
                    print(" , ")
                }
            }
            firstIt = false
            print("{")
            value.forEachIndexed { num, it ->
                printCoord(it)
                if (num != value.size - 1) {
                    print(" , ")
                }
            }
            print("}")
            newLine = false
        }
        println("\n")
    }

    private fun wallTemplate(ms: Int): Array<Pair<Int, Int>> {
        return when (ms) {
            3 -> arrayOf(
                Pair(0, 1),
                Pair(2, 2)
            )
            4 -> arrayOf(
                Pair(1, 0),
                Pair(1, 1),
                Pair(2, 3),
                Pair(3, 1)
            )
            5 -> arrayOf(
                Pair(1, 0),
                Pair(1, 1),
                Pair(1, 3),
                Pair(2, 3),
                Pair(3, 1),
                Pair(4, 1)
            )
            6 -> arrayOf(
                Pair(1, 0),
                Pair(1, 1),
                Pair(1, 4),
                Pair(2, 3),
                Pair(3, 1),
                Pair(3, 3),
                Pair(4, 1),
                Pair(5, 4)
            )
            7 -> arrayOf(
                Pair(0, 1),
                Pair(1, 1),
                Pair(1, 4),
                Pair(2, 2),
                Pair(2, 5),
                Pair(3, 3),
                Pair(5, 1),
                Pair(5, 4),
                Pair(5, 5),
                Pair(6, 4)
            )
            8 -> arrayOf(
                Pair(1, 0),
                Pair(1, 1),
                Pair(1, 2),
                Pair(1, 6),
                Pair(2, 5),
                Pair(2, 6),
                Pair(3, 2),
                Pair(3, 3),
                Pair(4, 6),
                Pair(4, 7),
                Pair(5, 2),
                Pair(5, 4),
                Pair(6, 4)
            )
            else -> arrayOf(Pair(0, 0))
        }
    }
}