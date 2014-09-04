package com.stevenlanders.sudoku.domain

import org.junit.Test

/**
 * Created by stevenlanders on 7/29/14.
 */
class GridTests {

    @Test
    def void testCanary(){
        assert true
    }

    @Test
    def void testSquare(){
        checkSquare([0,1,2],[0,1,2],0,0)
        checkSquare([3,4,5],[0,1,2],1,0)
        checkSquare([6,7,8],[0,1,2],2,0)
        checkSquare([0,1,2],[3,4,5],0,1)
        checkSquare([3,4,5],[3,4,5],1,1)
        checkSquare([6,7,8],[3,4,5],2,1)
        checkSquare([0,1,2],[6,7,8],0,2)
        checkSquare([3,4,5],[6,7,8],1,2)
        checkSquare([6,7,8],[6,7,8],2,2)
    }

    @Test
    def void testChoices(){
        Point point = new Point(
                x: 0,
                y: 0,
                grid: new SudokuGrid(getTestGrid()).getGrid()
        )
        assert point.getChoices().containsAll([1,9])
    }

    @Test
    def void testParse(){
        String s = ",,,,,,8,3,1,,5,,9,,1,,,,6,7,,,3,,5,,,7,4,5,,8,,,6,9,3,9,,,,4,2,5,7,1,,,,9,5,,8,,5,3,6,,,,,,,,,4,,,9,,1,6,,,,6,2,,,,5"
        SudokuGrid g = SudokuGrid.parse(s);
        assert g.getGrid()[0][6].getValue() == 8;
        assert g.getGrid()[0][0].getChoices().equals([2,4,9])
        assert !g.isSolved()
        assert g.solve()
    }

    @Test
    def void testSolveMedium(){
        String s = ",,,6,,7,,5,,1,7,,5,,,,,9,5,3,,,9,4,,,8,,,2,8,1,,3,,,,6,,2,4,3,9,,,,4,,,6,9,,,2,,,4,,,8,6,7,,2,,,9,,6,,,,,,3,4,7,,2,9,"
        SudokuGrid g = SudokuGrid.parse(s)
        assert g.solve()
    }

    @Test
    def void testSolveHard(){
        String s = ",,6,1,,,,5,,2,,,6,,5,,,8,,,,,9,,,,2,,,,,1,9,3,,,,,2,,,,8,,,,,3,5,7,,,,,9,,,,4,,,,,8,,,3,,1,,,9,,4,,,,6,1,,"
        SudokuGrid g = SudokuGrid.parse(s)
        g.solve()
        assert g.isSolved()
    }

    @Test
    def void testSolveEvil(){
        String s = ",,5,,,,4,,,,4,,,6,,5,8,,,,,5,7,,,,,2,,7,,,,8,6,,5,,,1,,,,7,,4,,,,9,,,1,,9,,2,3,,,,,8,,,,2,5,,,,,,,3,,,,1,,"
        SudokuGrid g = SudokuGrid.parse(s)
        assert g.solve()
        assert g.isSolved()
    }

    @Test
    def void testSolveBlank(){
        SudokuGrid g = SudokuGrid.parse("")
        g.solve()
        g.getGrid()[0][0].getChoices().containsAll([2,1])
        assert g.isSolved()
    }

    @Test
    def void testGenerateEasy(){
        SudokuGrid g = SudokuGrid.generateEasy()
        assert g.getFlatGrid().findAll{
            it.getValue() != null
        }.size() == 36
    }

    @Test
    def void testGenerateMedium(){
        SudokuGrid g = SudokuGrid.generateMedium()
        assert g.getFlatGrid().findAll{
            it.getValue() != null
        }.size() == 31
    }

    @Test
    def void testGenerateHard(){
        SudokuGrid g = SudokuGrid.generateHard()
        assert g.getFlatGrid().findAll{
            it.getValue() != null
        }.size() == 26
    }

    static def checkSquare(def row, def col, Integer squareRow, Integer squareCol){
        SudokuGrid g = new SudokuGrid(getTestGrid());
        row.each{x->
            col.each{y->
                Point p = new Point(
                        x: x,
                        y: y,
                        grid: g.getGrid()
                )
                assert p.getSquareCol() == squareCol
                assert p.getSquareRow() == squareRow
            }
        }
    }


    static def getTestGrid(){
        Integer[][] grid = [
                [null,2,3,4,5,6,7,8,null],
                [2,3,4,5,6,7,8,9,1],
                [3,4,5,6,7,8,9,1,2],
                [4,5,6,7,8,9,1,2,3],
                [5,6,7,8,9,1,2,3,4],
                [6,7,8,9,1,2,3,4,5],
                [7,8,9,1,2,3,4,5,6],
                [8,9,1,2,3,4,5,6,7],
                [null,1,2,3,4,5,6,7,8]
        ]
    }

}
