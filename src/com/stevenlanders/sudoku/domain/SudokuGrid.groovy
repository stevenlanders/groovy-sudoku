package com.stevenlanders.sudoku.domain

/**
 * Created by stevenlanders on 7/29/14.
 */
class SudokuGrid{

    static final def BLANK_SOLVED_PUZZLE = "1,2,3,4,5,6,7,8,9,4,5,6,7,8,9,1,2,3,7,8,9,1,2,3,4,5,6,2,3,1,6,7,4,8,9,5,8,7,5,9,1,2,3,6,4,6,9,4,5,3,8,2,1,7,3,1,7,2,6,5,9,4,8,5,4,2,8,9,7,6,3,1,9,6,8,3,4,1,5,7,2,"

    static def levels = [
            easy : 36,
            medium: 31,
            hard: 26
    ]

    def Point[][] grid;
    List<Point> flatGrid = []

    SudokuGrid(Integer[][] inputGrid){
        grid = new Point[inputGrid.length][inputGrid[0].length]
        inputGrid.eachWithIndex { Integer[] row, int x ->
            row.eachWithIndex { Integer value, int y ->
                Point thisPoint = new Point(x:x, y:y, value: value, grid:grid)
                grid[x][y] = thisPoint;
                flatGrid.add(thisPoint)
            }
        }
    }

    static def SudokuGrid generateEasy(){
        return generate(levels.easy)
    }

    static def SudokuGrid generateMedium(){
        return generate(levels.medium)
    }

    static def SudokuGrid generateHard(){
        return generate(levels.hard)
    }

    private static def SudokuGrid generate(int keepThisMany){
        SudokuGrid grid = parse(BLANK_SOLVED_PUZZLE)
        (1..(grid.getFlatGrid().size() - keepThisMany)).each{
            clearRandomSpot(grid.getFlatGrid())
        }
        exchangeNumbers(grid.getFlatGrid())
        return grid
    }

    private static def exchangeNumbers(List<Point> list){
        List<Integer> nums = new ArrayList<Integer>((1..9))
        Collections.shuffle(nums)
        list.findAll{it.value != null}.each{point->
            point.value = nums.indexOf(point.value)+1
        }
    }

    private static def clearRandomSpot(List<Point> list){
        def filledPoints = list.findAll{
            it.getValue() != null
        }
        filledPoints.get(new Random().nextInt(filledPoints.size())).setValue(null)
    }

    static def SudokuGrid parse(String s){
        def commaStrArray = s.split(",") as List
        while(commaStrArray.size() < 81){
            commaStrArray.add("")
        }

        def edgeSize = Math.sqrt(commaStrArray.size())
        Integer[][] inputGrid = new Integer[edgeSize][edgeSize]
        int col = -1
        commaStrArray.eachWithIndex { def entry, int i ->
            int row = i % edgeSize
            if(row == 0){
                col++;
            }
            inputGrid[col][row] = entry.isEmpty() ? null : Integer.parseInt(entry)

        }
        return new SudokuGrid(inputGrid)
    }

    def solve(){
        if(isSolved()) return true //yay
        if(anyStuck()) return false //damn
        Point p = getGuessPoint()
        if(p != null) {
            for(Integer choice : p.getChoices()){
                p.setValue(choice)
                if (solve()){
                    return true
                }
            }
            p.setValue(null)
        }
        return false
    }

    private def anyStuck(){
        flatGrid.any{
            it.getValue() == null && it.getChoices().size() == 0
        }
    }

    private def Point getGuessPoint(){
        def result = flatGrid.findAll{
            it.getChoices().size() > 0
        }.sort{
            it.getChoices().size()
        }
        return result.isEmpty() ? null : result.first()
    }

    def isSolved(){
        boolean solved = !flatGrid.any{
            it.getValue() == null
        }
        return solved
    }

    def toCommaString(){
        StringBuilder sb = new StringBuilder()
        flatGrid.each{
            String val = "${it.getValue()}"
            val = val.replace("null","")
            sb.append(val).append(",")
        }
        return sb.toString()
    }

    def toGridString(){
        StringBuilder sb = new StringBuilder()
        toCommaString().split(",").eachWithIndex{ String entry, int i ->
            if(i%grid.length == 0){
                sb.append("\n")
            }
            sb.append(entry)
        }
        return sb.toString()
    }

    def printGrid(){
        println(toGridString())
    }

}

class Point{
    int x;
    int y;
    Integer value;
    Point[][] grid;

    List<Integer> getChoices(){
        if(value != null) return []
        def row = grid[x]
        def column = grid.collect{it[y]}
        def square = getSquare()

        return (1..grid.length).findAll{num->
            !row.any{it.equalsValue(num)} &&
                    !column.any{it.equalsValue(num)} &&
                        !square.any{it.equalsValue(num)}

        }

    }

    def equalsValue(Integer val){
        val == value;
    }

    def getSquareRow(){
        double dividend = Math.sqrt(grid.length)
        (int)(x/dividend)
    }

    def getSquareCol(){
        double dividend = Math.sqrt(grid.length)
        (int)(y/dividend)
    }

    def sameSquareGroup(Point p){
        p.getSquareCol() == this.getSquareCol() && p.getSquareRow() == this.getSquareRow();
    }

    List<Point> getSquare(){
        def result = []
        grid.each{row->
            result.addAll(row.findAll{point->
                this.sameSquareGroup(point)
            })
        }
        return result
    }

}
