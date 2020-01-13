package de.hswt.bp.tetris;

import java.util.Random;

/*
 * Ein geometrisches Teilchen. Jedes Teilchen besteht aus 4 Quadraten (Tetrominos).
 * 
 * Autor: Frank Leßke
 */

import de.hswt.bp.tetris.Piece.Tetrominoe;

public class Piece {

	/* die 8 geometrischen Formen */
	protected enum Tetrominoe {
		NoShape, ZShape, SShape, LineShape, TShape, SquareShape, LShape, MirroredLShape
	}

	private Tetrominoe pieceShape;
	/* jedes Piece wird durch die linken oberen Eckpunkte der 4 Quadrate definiert */
	private int[][] coords;

	/* Konstruktor */
	public Piece() {
		coords = new int[4][2];
		setShape(Tetrominoe.NoShape);
	}

	/*
	 * In der Tabelle sieht man die Definitionen der einzelnen geometrischen Formen.
	 * Jede Form besteht aus 4 Quadraten. für jedes Quadrat ist der linke obere Eckpunkt in Bezug auf den Nullpunkt
	 * angegeben.
	 */
	protected void setShape(Tetrominoe shape) {

		int[][][] coordsTable = new int[][][] { { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } },
				{ { 0, -1 }, { 0, 0 }, { -1, 0 }, { -1, 1 } }, { { 0, -1 }, { 0, 0 }, { 1, 0 }, { 1, 1 } },
				{ { 0, -1 }, { 0, 0 }, { 0, 1 }, { 0, 2 } }, { { -1, 0 }, { 0, 0 }, { 1, 0 }, { 0, 1 } },
				{ { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } }, { { -1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } },
				{ { 1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } } };

		for (int i = 0; i < 4; i++) {

			System.arraycopy(coordsTable[shape.ordinal()], 0, coords, 0, 4);
		}

		pieceShape = shape;
	}

	/* Setter und Getter Funktionen */
	protected void setX(int index, int x) {

		coords[index][0] = x;
	}

	protected void setY(int index, int y) {

		coords[index][1] = y;
	}

	protected int x(int index) {

		return coords[index][0];
	}

	protected int y(int index) {

		return coords[index][1];
	}

	protected Tetrominoe getShape() {

		return pieceShape;
	}
	
	protected boolean isSquare() {
		return pieceShape == Tetrominoe.SquareShape;
	}

	/*
	 * Zufällige Auswahl der Geometrie.
	 */
	protected void setRandomShape() {

		var r = new Random();
		int x = Math.abs(r.nextInt()) % 7 + 1;

		Tetrominoe[] values = Tetrominoe.values();
		setShape(values[x]);
	}

	protected void setRandomShape(int s) {
		int x = Math.abs(s) % 7 + 1;
		Tetrominoe[] values = Tetrominoe.values();
		setShape(values[x]);
	}

	/*
	 * Minimum, damit der nötige Platz berechnet werden kann
	 */
	public int minX() {

		int m = coords[0][0];

		for (int i = 0; i < 4; i++) {

			m = Math.min(m, coords[i][0]);
		}

		return m;
	}

	public int minY() {

		int m = coords[0][1];

		for (int i = 0; i < 4; i++) {

			m = Math.min(m, coords[i][1]);
		}

		return m;
	}

}