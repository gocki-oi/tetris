package de.hswt.bp.tetris;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;
import java.util.stream.IntStream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


/*
 * Die Hauptklasse mit der UI (Benutzeroberflaeche)
 * Autor: Frank Leßke
 */

public class Tetris extends JFrame {

	private JLabel statusbar;
	private JLabel result;
	private JButton start;
	private JButton stop;
	private Board board;
	
	private String propsFile = "tetris.properties";
	private String[] best = new String[6];
	private int[] bestScores = new int[6];
	private Properties props;
	private String message;

	/*
	 * Konstruktor
	 */
	public Tetris() {
		
		// Properties (Bestenliste) einlesen
		try {
			props = new Properties();
			Reader reader = new FileReader(propsFile);
			props.load(reader);
			
			for (int i = 1; i <= 6; i++) {
				best[i-1] = props.getProperty("best"+i);
				try {
					int score = Integer.parseInt(props.getProperty("score"+i)); 
					bestScores[i-1] = score;
				} catch (NumberFormatException nfe) {
					bestScores[i-1] = 0;
				}
			}
		} catch (FileNotFoundException fne) {
			IntStream.range(1, 7).forEach(j -> {best[j-1] = "none"; bestScores[j-1] = 0;});

		} catch (IOException e) {
			e.printStackTrace();
		}

		initUI();
	}

	/*
	 * Initialisierung des Fensters
	 */
	private void initUI() {

		result = new JLabel("0");
		result.setBorder(BorderFactory.createTitledBorder("Punktestand"));
		result.setPreferredSize(new Dimension(100,8));
		result.setBackground(Color.white);
		//result.setForeground(new Color(122,184,0));
		start = new JButton("Start");
		start.setAlignmentX(CENTER_ALIGNMENT);
		stop = new JButton("Stop");
		stop.setEnabled(false);
		statusbar = new JLabel("Spiel beginnen mit Start");
		statusbar.setBorder(BorderFactory.createTitledBorder("Statusmeldungen"));
		statusbar.setPreferredSize(new Dimension(220,40));
		//statusbar.setForeground(Color.red);

		var buttonPanel = new JPanel();
		GridLayout gl = new GridLayout(2,1, 10, 5);
		buttonPanel.setLayout(gl);	
		
		Box buttonBar = Box.createHorizontalBox();
		buttonBar.add(Box.createHorizontalGlue());
		buttonBar.add(start);
		buttonBar.add(Box.createHorizontalStrut(10));
		buttonBar.add(stop);
		buttonBar.add(Box.createHorizontalGlue());
		
		Box textBar = Box.createHorizontalBox();
		textBar.add(Box.createHorizontalGlue());
		textBar.add(result);
		textBar.add(Box.createHorizontalGlue());
		textBar.add(statusbar);
		textBar.add(Box.createHorizontalGlue());
		
		buttonPanel.add(buttonBar);
		buttonPanel.add(textBar);
		
		buttonPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		add(buttonPanel, BorderLayout.SOUTH);

		board = new Board(this);
		add(board);

		setTitle("HSWT Tetris");
		setSize(400, 750);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		start.addActionListener(ev -> {
			board.reset();
			start.setEnabled(false);
			stop.setEnabled(true);
			statusbar.setText("Spiel läuft...");
			board.start();
		});

		stop.addActionListener(ev -> {
			board.stop();
			stop.setEnabled(false);
			start.setEnabled(true);
		});
		
		ImageIcon img = new ImageIcon("HSWT.png");
		this.setIconImage(img.getImage());
		

	}

	/*
	 * Bisherige Punktzahl erneuern
	 */
	protected void updatePoints(int z) {
		result.setText(" "+z);

	}

	/*
	 * Wenn das Spiel vorbei ist, wird überprüft, ob ein neuer Eintrag in die Bestenliste nötig ist.
	 */
	protected void gameOver(String msg) {

		statusbar.setText(msg);
		start.setEnabled(true);
		String feedback = "Sorry, Sie haben keinen HighScore erreicht!";
		// HighScore anzeigen
		for (int i = 0; i < 6; i++) {
			if (board.getScore() > bestScores[i]) {
				for (int j = 5; j > i; j--) {
					bestScores[j] = bestScores[j-1];
					best[j] = best[j-1];
				}
				bestScores[i] = board.getScore();
				var name = new JTextField(24);
				int action = JOptionPane.showConfirmDialog(null, name, "Bitte Namen eingeben: ",
						JOptionPane.OK_CANCEL_OPTION);
				String neuName = "neu";
				if (action < 0) {
					JOptionPane.showMessageDialog(null, "Cancel, X or escape key selected");
					
				} else {
					neuName = new String(name.getText());
				}
				best[i] = neuName;
				// properties speichern
				IntStream.range(1, 7).forEach(j -> {props.setProperty("best"+j, best[j-1]); props.setProperty("score"+j, ""+bestScores[j-1]);});
				try {
					props.store(new FileWriter(propsFile), "neuer Eintrag in der Bestenliste");
				} catch (IOException e) {
					e.printStackTrace();
				}
				feedback = "Gratulation, Sie haben den " + (i+1) + "-ten Platz in der Bestenliste erreicht.";
				break;
			}
		}
		message = feedback + "\n\nBestenliste:\n" ;
		IntStream.range(1, 7).forEach(j -> {message = message + best[j-1] + " \t\t" + bestScores[j-1]+ "\n";});
		JOptionPane.showMessageDialog(null, message);
	}
	
	
	protected void setPause() {
		statusbar.setText("Pause eingelegt.");
	}
	
	/*
	 * Hauptmethode zum Aufruf bei Programmstart
	 */
	public static void main(String[] args) {
		// Das Anzeigen der GUI wird als nächste Aktion
		// an die Swing EventQueue gehängt.
		EventQueue.invokeLater(() -> {

			var game = new Tetris();
			game.setVisible(true);
		});
	}
}
