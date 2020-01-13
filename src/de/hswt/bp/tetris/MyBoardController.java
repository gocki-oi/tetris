package de.hswt.bp.tetris;

import de.hswt.bp.tetris.Piece.Tetrominoe;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;
import java.util.Stack;

/*
 * Die unten aufgefuehrten Methoden sind entsprechend der Aufagbenstellung zu implementieren.
 * Bitte die AutorInnen eintragen:
 *
 * Autoren:
 */

public class MyBoardController extends BoardController {

    boolean first = true;

    public MyBoardController(Board theBoard) {
        super(theBoard);
    }

    //--------------------------------------------------------------------------

    protected void newPiece() {


        // Aufgabe 1
        int rand = (int) (Math.random() * 7) + 1;
        setNewShape(rand);

        int quadDown = 0;
        int z = 0;
        Tetrominoe shapeType = new ArrayList<>(EnumSet.allOf(Tetrominoe.class)).get(rand);

        if (shapeType == Tetrominoe.SquareShape || shapeType == Tetrominoe.LShape || shapeType == Tetrominoe.LineShape) {
            quadDown = 1;
        }
        if (shapeType == Tetrominoe.MirroredLShape || shapeType == Tetrominoe.SShape || shapeType == Tetrominoe.ZShape || shapeType == Tetrominoe.TShape || shapeType == Tetrominoe.SquareShape) {
            quadDown = 2;
        }

        int quadRight = 0;

        if (shapeType == Tetrominoe.SquareShape || shapeType == Tetrominoe.ZShape) {
            quadRight = 1;
        }

        // different shapes --> different starting points in "right" and "down" axis
        // quadDown and Right move them away from borders

        setCurrentPiecePosition(4 + quadRight, 22 - quadDown);


        System.out.println("New piece:" + shapeType);
        // board index starts left downside --> 4 = middle and 22 = topside

    }


    //---------------------------------------------------------------------------


    public boolean canMoveTo(Piece newpiece, int newX, int newY) {
        // Aufgabe 2
        for (int i = 0; i < 4; i++) {
            int x = newX + newpiece.x(i);
            if (x < 0 || x >= getBoardWidth()) return false;

            int y = newY - newpiece.y(i);
            if (y == -1) return false;

            boolean shape = isNoShapeAt(x, y);
            if (!shape) return false;
        }
        return true;
    }

    public boolean canMoveDown(Piece piece, int x, int y) {
        return canMoveTo(piece, x, y - 1);
    }

    @Override
    protected void oneLineDown(Piece piece, int x, int y) {

        if (canMoveDown(piece, x, y)) {
            moveTo(piece, x, y - 1);
            return;
        }


        paintEachBrickOfPiece(piece, x, y);
        eraseFullLines();

        startNewPiece();

    }


    private void paintEachBrickOfPiece(Piece piece, int x, int y) {
        for (int i = 0; i < 4; i++) {

            int ndx = x + getPieceXCoord(piece, i);
            int ndy = y - getPieceYCoord(piece, i);

            int ndz = ndy * getBoardWidth() + ndx;
            setPieceAtBoard(piece, ndz);
        }
    }


    protected void eraseFullLines() {

        int lineCounter = 0;

        for (int i = getBoardHeight() - 1; i >= 0; i--) {
            boolean isFull = true;
            for (int j = 0; j < getBoardWidth(); j++) {

                if (isNoShapeAt(j, i)) {
                    isFull = false;
                    break;
                }
            }

            if (isFull) {
                int firstBlock = i * getBoardWidth();
                int lastBlock = firstBlock + getBoardWidth() - 1;

                System.out.println(firstBlock + " - " + lastBlock);

                deleteLine(firstBlock, lastBlock);
                bricksDropOnBoard(lastBlock);

                lineCounter++;
            }
        }
        addRemovedLines(lineCounter);
    }

    private void deleteLine(int firstBlock, int lastBlock) {
        for (int z = firstBlock; z <= lastBlock; z++) {
            setPieceAtBoard(createPiece(Tetrominoe.NoShape), z);
        }
    }

    private void bricksDropOnBoard(int lastBrick) {
        for (int i = lastBrick + 1; i <= getBoardHeight() * getBoardWidth() - 1; i++) {
            System.out.println("move " + (i));
            movePieceDown(i - getBoardWidth());
        }

    }


    @Override
    protected Piece rotateLeft(Piece piece) {

        if (isSquare(piece)) {
            return piece;
        }


        var clone = new Piece();
        clone.setShape(piece.getShape());

//         loop through possible rotations
        for (int i = 0; i < 4; i++) {
            int x = getPieceYCoord(piece, i);
            int y = -getPieceXCoord(piece, i);
            clone.setX(i, y);
            clone.setY(i, x);
        }

        return clone;
    }

    @Override
    protected Piece rotateRight(Piece piece) {

        if (isSquare(piece)) {
            return piece;
        }

        var clone = new Piece();
        clone.setShape(piece.getShape());

        // loop through possible rotations
        for (int i = 0; i < 4; i++) {
            int x = -getPieceYCoord(piece, i);
            int y = getPieceXCoord(piece, i);
            clone.setX(i, x);
            clone.setY(i, y);
        }
        return clone;

    }

    @Override
    protected void dropDown(Piece piece, int y, int x) {
        int newY = y;

        while (canMoveDown(piece, x, newY)) {
            newY--;
            moveTo(piece, x, newY);
        }


    }


/*	}protected void oneLineDown(Piece piece, int x, int y) {
			if (canMoveTo()) {

				movePieceDown();
			}
		}


		// Aufgabe 2 + 3 + 5
	}
	protected void movePieceDown(int a) {
		int x = a%board.getBoardWidth();
		int y = a/board.getBoardWidth() + 1;
		board.setShape(a, board.shapeAt(x, y));
*/

    //--------------------------------------------------------------------------
/*
	protected Piece rotateLeft(Piece piece) {

		// Aufgabe 4



	protected Piece rotateRight(Piece piece) {

		// Aufgabe 4
		return piece;
	}
	
	//----------------------------------------------------------------------

	protected void dropDown(Piece piece, int y, int x) {
		// Aufgabe 6
	}
	*/


}

