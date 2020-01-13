package de.hswt.bp.tetris;

import de.hswt.bp.tetris.Piece.Tetrominoe;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/*
 * Dsa Spielfeld bzw. Spielbrett. Es besteht eigentlich aus zwei Teilen. Das Feld an und fuer sich und 
 * das aktuell sich bewegende Tetrominoe.
 * 
 * Autor: Frank Leßke
 */

public class Board extends JPanel {

	/* Spielfeldgroesse */
	private final int BOARD_WIDTH = 10;
	private final int BOARD_HEIGHT = 22;
	/* Die Spielfeldmatrix */
	private Tetrominoe[] board;
	/* Wartezeit für die Animation */
	private final int PERIOD_INTERVAL = 300; //300
	/* Scheduler für die Animation */
	private Timer timer;
	/* Ist das aktuelle Tetromino noch im Fallen */
	private boolean isFallingFinished = false;
	/* Wurde die Pause-Taste gedrückt */
	private boolean isPaused = false;
	/* Anzahl der vollen Zeilen */
	private int numLinesRemoved = 0;
	/* Aktuelle Koordinaten */
	private int currentX = 0;
	private int currentY = 0;
	/* Das aktuelle Tetromino */
	private Piece currentPiece;
	/* das zugehoerige Tetrisspiel */
	private Tetris tetris;
	/* Controller zur Umsetzung der Aktionen */
	private BoardController control;

	/*
	 * Konstruktor. 
	 */
	public Board(Tetris parent) {
		control = new MyBoardController(this);
		initBoard(parent);
		currentPiece = new Piece();
		board = new Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];
		clearBoard();
	}

	/*
	 * Initialisierung des Spielbretts
	 */
	private void initBoard(Tetris parent) {

		setFocusable(true);
		tetris = parent;
		addKeyListener(new TAdapter());
	}
	
	/*
	 * Getter- Setter-Methoden für den Zugriff auf das Speilbrett.
	 */
	public int getBoardWidth() {
		return BOARD_WIDTH;
	}

	public int getBoardHeight() {
		return BOARD_HEIGHT;
	}
	

	public void setCurrentX(int currentX) {
		this.currentX = currentX;
	}

	public void setCurrentY(int currentY) {
		this.currentY = currentY;
	}

	public Piece getCurrentPiece() {
		return currentPiece;
	}
	
	public void setCurrentPiece(Piece shape) {
		currentPiece = shape;
	}

	protected int getScore() {
		return numLinesRemoved;
	}
	
	private int squareWidth() {

		return (int) getSize().getWidth() / BOARD_WIDTH;
	}

	private int squareHeight() {

		return (int) getSize().getHeight() / BOARD_HEIGHT;
	}

	protected Tetrominoe shapeAt(int x, int y) {

		return board[(y * BOARD_WIDTH) + x];
	}
	
	protected void setShape(int s, Tetrominoe t) {
		board[s] = t;
	}

	
	/*
	 * Vor jedem Spielstart muss das Spielbrett zurueckgesetzt werden.
	 */
	protected void reset() {
		
		numLinesRemoved = 0;
		tetris.updatePoints(numLinesRemoved);

		currentPiece = new Piece();
		board = new Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];

		clearBoard();
		newPiece();
	}

	/*
	 * Starte ein neues Spiel
	 */
	protected void start() {
		this.requestFocus();
		timer = new Timer(PERIOD_INTERVAL, new GameCycle());
		timer.start();
	}

	/*
	 * Die Stop-Taste wurde gedrueckt. Das Spiel wird beendet.
	 */
	protected void stop() {
		timer.stop();
		tetris.updatePoints(numLinesRemoved);
		tetris.gameOver("Spiel beendet, bisheriger Score: " + numLinesRemoved);
	}

	/*
	 * Die Pause-Taste wurde gedrueckt.
	 */
	private void pause() {

		isPaused = !isPaused;

		if (isPaused) {
			tetris.setPause();
		} else {
			tetris.updatePoints(numLinesRemoved);
			//statusbar.setText(String.valueOf(numLinesRemoved));
		}

		repaint();
	}

	@Override
	/*
	 * Ueberschreibung der Standardmethode zum Anzeigen des Spielbretts.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		doDrawing(g);

	}

	/*
	 * Hier wird das Spielbrett tatsaechlich gezeichnet.
	 * Dazu werden die Brettkoordinaten umgewandelt in Pixel-Koordinaten.
	 */
	private void doDrawing(Graphics g) {

		var size = getSize();
		int boardTop = (int) size.getHeight() - BOARD_HEIGHT * squareHeight();

		for (int i = 0; i < BOARD_HEIGHT; i++) {

			for (int j = 0; j < BOARD_WIDTH; j++) {

				Tetrominoe shape = shapeAt(j, BOARD_HEIGHT - i - 1);

				if (shape != Tetrominoe.NoShape) {

					drawSquare(g, j * squareWidth(), boardTop + i * squareHeight(), shape);
				}
			}
		}

		if (currentPiece.getShape() != Tetrominoe.NoShape) {

			for (int i = 0; i < 4; i++) {

				int x = currentX + currentPiece.x(i);
				int y = currentY - currentPiece.y(i);

				drawSquare(g, x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(),
						currentPiece.getShape());
			}
		}
	}

	

	private void clearBoard() {

		for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++) {

			board[i] = Tetrominoe.NoShape;
		}
	}

	

	private void newPiece() {

		control.newPiece();
		

		if (!control.canMoveTo(currentPiece, currentX, currentY)) {

			//currentPiece.setShape(Tetrominoe.NoShape);
			if (timer != null && timer.isRunning()) {
				currentPiece.setShape(Tetrominoe.NoShape);
				timer.stop();

				var msg = String.format("Game over. Score: %d", numLinesRemoved);
				// statusbar.setText(msg);
				tetris.gameOver(msg);
			}
		}
	}


	
	/*
	 * Es werden weitere vollstaendige Linien hinzugefügt und der Spielstand angezeigt.
	 */
	protected void addRemovedLines(int lines) {
		if (lines > 0) {

			numLinesRemoved += lines;

			// statusbar.setText(String.valueOf(numLinesRemoved));
			tetris.updatePoints(numLinesRemoved);
			isFallingFinished = true;
			currentPiece.setShape(Tetrominoe.NoShape);
		}
		isFallingFinished = true;
	}
	
	protected void startNewPiece() {
		isFallingFinished = true;
	}

	/*
	 * Ein einzelnes Quadrat eines Teromino wird gezeichnet.
	 */
	private void drawSquare(Graphics g, int x, int y, Tetrominoe shape) {

		Color colors[] = { new Color(0, 0, 0), new Color(204, 102, 102), new Color(102, 204, 102),
				new Color(102, 102, 204), new Color(204, 204, 102), new Color(204, 102, 204), new Color(102, 204, 204),
				new Color(218, 170, 0) };

		var color = colors[shape.ordinal()];

		g.setColor(color);
		g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

		g.setColor(color.brighter());
		g.drawLine(x, y + squareHeight() - 1, x, y);
		g.drawLine(x, y, x + squareWidth() - 1, y);

		g.setColor(color.darker());
		g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
		g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
	}

	/*
	 * Innere Klasse die Timer-Ereignisse verarbeitet und einen neuen Spielzyklus aufruft.
	 */
	private class GameCycle implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			doGameCycle();
		}
	}

	/*
	 * Der Spielzyklus besteht aus der Neuberechnung der Position des aktuellen Tetrominos und dem
	 * neuen Zeichnen des Spielfelds.
	 */
	private void doGameCycle() {

		update();
		repaint();
	}

	/*
	 * Die Neuberechnung der Position. Wenn der aktuelle Spielstein nicht mehr weiter fallen kann, wird ein neuer
	 * Spielstein erzeugt. Ansonsten wird der Stein eine Zeile nach unten bewegt.
	 */
	private void update() {

		if (isPaused) {

			return;
		}

		if (isFallingFinished) {

			isFallingFinished = false;
			newPiece();
		} else {
			control.oneLineDown(currentPiece, currentX, currentY);
			//oneLineDown();
		}
	}

	/*
	 * Innere Klasse zur Verarbeitung der Tastaturereignisse. Mit den Pfeiltasten kann man die Steine nach links, oder rechts bewegen,
	 * oder die Steine rotieren.
	 */
	class TAdapter extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {

			if (currentPiece.getShape() == Tetrominoe.NoShape) {

				return;
			}

			int keycode = e.getKeyCode();
			Piece rotate;

			switch (keycode) {

			case KeyEvent.VK_P:
				pause();
				break;
			case KeyEvent.VK_LEFT:
				if (control.canMoveTo(currentPiece, currentX - 1, currentY)) {
					control.moveTo(currentPiece, currentX-1, currentY);
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (control.canMoveTo(currentPiece, currentX + 1, currentY)) {
					control.moveTo(currentPiece, currentX+1, currentY);
				}
				break;
			case KeyEvent.VK_DOWN:
				rotate = control.rotateRight(currentPiece);
				if (control.canMoveTo(rotate, currentX, currentY)) {
					control.moveTo(rotate, currentX, currentY);
				}
				break;
			case KeyEvent.VK_UP:
				rotate = control.rotateLeft(currentPiece);
				if (control.canMoveTo(rotate, currentX, currentY)) {
					control.moveTo(rotate, currentX, currentY);
				}
				break;
			case KeyEvent.VK_SPACE:
				control.dropDown(currentPiece, currentY, currentX);
				break;
			case KeyEvent.VK_D:
				control.oneLineDown(currentPiece, currentX, currentY);
			
			}
		}
	}
}
