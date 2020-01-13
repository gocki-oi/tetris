package de.hswt.bp.tetris;

import de.hswt.bp.tetris.Piece.Tetrominoe;

/*
 * Abstrakte Klasse. Diese Klasse stellt das funktionale Interface fuer die Projektaufgabe bereit.
 * Diese Klasse bitte nicht aendern.
 * 
 * Autor: Frank Leﬂke
 */

public abstract class BoardController {
	
	private Board board;

	
	public BoardController(Board theBoard) {
		board = theBoard;
	}
	
	/* Die Schnittstellen der im Projekt zu implementierenden Funktionen. */
	
	abstract protected void newPiece();

	abstract protected boolean canMoveTo(Piece newPiece, int newX, int newY);
	
	abstract protected void oneLineDown(Piece piece, int x, int y);
	
	abstract protected Piece rotateLeft(Piece piece);
	
	abstract protected Piece rotateRight(Piece piece);
	
	abstract protected void dropDown(Piece piece, int y, int x);
	
	/* Hilfsfunktionen, damit im Projekt funktional programmiert werden kann */
	
	protected int getPieceXCoord(Piece piece, int index) {
		return piece.x(index);
	}
	
	protected int getPieceYCoord(Piece piece, int index) {
		return piece.y(index);
	}
	
	protected int getBoardHeight() {
		return board.getBoardHeight();
	}
	
	protected int getBoardWidth() {
		return board.getBoardWidth();
	}
	
	protected boolean isNoShapeAt(int x, int y) {
		return (board.shapeAt(x, y) == Tetrominoe.NoShape);
	}
	
	protected void setNewShape(int s) {
		board.getCurrentPiece().setRandomShape(s);
	}
	
	protected int getCurrentYMin() {
		return board.getCurrentPiece().minY();
	}
	
	protected void setCurrentPiecePosition(int x, int y) {
		board.setCurrentX(x);
		board.setCurrentY(y);
	}
	
	protected void setPieceAtBoard(Piece piece, int z) {
		board.setShape(z, piece.getShape());
	}
	
	protected void movePieceDown(int a) {
		int x = a%board.getBoardWidth();
		int y = a/board.getBoardWidth() + 1;
		board.setShape(a, board.shapeAt(x, y));
		
	}
	
	protected void addRemovedLines(int num) {
		board.addRemovedLines(num);
	}
	
	protected void moveTo(Piece piece, int x, int y) {
		board.setCurrentPiece(piece);
		board.setCurrentX(x);
		board.setCurrentY(y);
		board.repaint();
	}
	
	protected Piece createPiece(Tetrominoe shape) {
		var result = new Piece();
		result.setShape(shape);
		return result;
	}
	
	protected void startNewPiece() {
		board.startNewPiece();
	}
	
	protected boolean isSquare(Piece piece) {
		return piece.isSquare();
	}
	
	protected Tetrominoe getShape(Piece piece) {
		return piece.getShape();
	}
	
	protected void setXCoord(Piece piece, int index, int x) {
		piece.setX(index, x);
	}
	
	protected void setYCoord(Piece piece, int index, int y) {
		piece.setY(index, y);
	}

}
